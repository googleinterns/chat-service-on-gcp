package Controller;

import Entity.User;
import Entity.Chat;
import Entity.Message;
import Helper.UniqueIDGenerator;
import DBAccesser.User.UserAccessor;
import DBAccesser.Chat.ChatAccessor;
import DBAccesser.Message.MessageAccessor;
import Helper.SuccessResponseGenerator;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;
import Exceptions.UserIDMissingFromRequestURLPathException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;
import com.google.cloud.Timestamp;

@RestController
public final class ListChats {
    
    @Autowired 
    private UserAccessor queryUser;

    @Autowired 
    private ChatAccessor queryChat;

    @Autowired
    private MessageAccessor queryMessage;

    @Autowired
    private UniqueIDGenerator uniqueIDGenerator;

    public static final class UsernameChatID {

        @Column(name = "Username")
        private String username;

        @Column(name = "ChatID")
        private long chatID;

        public UsernameChatID () {}

        public UsernameChatID (String username, long chatID) {
            this.username = username;
            this.chatID = chatID;
        }

        public String getUsername() {
            return username;
        }

        public long getChatID() {
            return chatID;
        }
    }

    Map<String, Object> getChatInfoOfChatInMap(Chat chat, String usernameOfSecondUser) {
        
        Map<String, Object> chatInfoOfChatInMap = new LinkedHashMap<String, Object>();
        chatInfoOfChatInMap.put("ChatID", chat.getChatID());
        chatInfoOfChatInMap.put("Username", usernameOfSecondUser);
        chatInfoOfChatInMap.put("LastSentMessageID", chat.getLastSentMessageID());

        return chatInfoOfChatInMap;
    }

    @GetMapping("/users/chats")
    public void listChatsWithoutUserIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userID}/chats")
    public Map<String,List<Map<String, Object>>> listChats(@PathVariable("userID") String userIDString, HttpServletRequest request) {
        
        String path = request.getRequestURI();

        long userID = Long.parseLong(userIDString);
        
        //check if the passed userID is valid
        if (!queryUser.checkIfUserIDExists(userID)) {
            throw new UserIDDoesNotExistException(path);
        }
        
        User user = new User(userID);
        List<Chat> chatsOfUser = queryChat.getChatsForUser(user);
        List<Message> listOfChatIDCreationTSOfLastSentMessageID = queryMessage.getLastSentMessageIDCreationTSForChatsOfUser(userID);
        Map<Long, Timestamp> chatIDCreationTSOflastSentMessageIDMap = new LinkedHashMap<Long, Timestamp>();

        for (Message chatIDCreationTSOfLastSentMessageID : listOfChatIDCreationTSOfLastSentMessageID) {
            chatIDCreationTSOflastSentMessageIDMap.put(chatIDCreationTSOfLastSentMessageID.getChatID(), chatIDCreationTSOfLastSentMessageID.getCreationTS());
        }
        
        for (Chat chat : chatsOfUser) {
            if (chat.getLastSentMessageID() == 0) { 
                chat.setLastSentTime(chat.getCreationTS());
            } else {
                chat.setLastSentTime(chatIDCreationTSOflastSentMessageIDMap.get(chat.getChatID()));
            }
        }
        
        Collections.sort(chatsOfUser, Comparator.comparing(Chat::getLastSentTime).reversed());

        List<Long> listOfChatIDDesc = new ArrayList<Long>();

        for (Chat chat : chatsOfUser) {
            listOfChatIDDesc.add(chat.getChatID());
        }

        List<UsernameChatID> usernameChatIDForSecondUsers = queryUser.getUsernameChatIDForSecondUsers(userID);
        Map<Long, String> chatIDSecondUsernameMap = new LinkedHashMap<Long, String>();

        for (UsernameChatID usernameChatID : usernameChatIDForSecondUsers) {
            chatIDSecondUsernameMap.put(usernameChatID.getChatID(), usernameChatID.getUsername());
        }

        List<Map<String, Object>> chatInfoOfChatsOfUser = new ArrayList<Map<String, Object>>();

        for (Chat chat : chatsOfUser) {
            chatInfoOfChatsOfUser.add(getChatInfoOfChatInMap(chat, chatIDSecondUsernameMap.get(chat.getChatID())));
        }

        return SuccessResponseGenerator.getSuccessResponseForListChats(chatInfoOfChatsOfUser);
    }
}