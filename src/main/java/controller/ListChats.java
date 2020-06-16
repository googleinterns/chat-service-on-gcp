package controller;

import entity.User;
import entity.Chat;
import entity.Message;
import helper.UniqueIdGenerator;
import dbaccessor.user.UserAccessor;
import dbaccessor.chat.ChatAccessor;
import dbaccessor.message.MessageAccessor;
import helper.SuccessResponseGenerator;
import exceptions.UserIdDoesNotExistException;
import exceptions.UserIdMissingFromRequestURLPathException;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
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
    private UniqueIdGenerator uniqueIdGenerator;

    public static final class UsernameChatId {

        @Column(name = "Username")
        private String username;

        @Column(name = "ChatID")
        private long chatId;

        public UsernameChatId () {}

        public UsernameChatId (String username, long chatId) {
            this.username = username;
            this.chatId = chatId;
        }

        public String getUsername() {
            return username;
        }

        public long getChatId() {
            return chatId;
        }
    }

    Map<String, Object> getChatInfoOfChatInMap(Chat chat, String usernameOfSecondUser) {
        
        Map<String, Object> chatInfoOfChatInMap = new LinkedHashMap<String, Object>();
        chatInfoOfChatInMap.put("ChatId", chat.getChatId());
        chatInfoOfChatInMap.put("Username", usernameOfSecondUser);
        chatInfoOfChatInMap.put("LastSentMessageId", chat.getLastSentMessageId());

        return chatInfoOfChatInMap;
    }

    @GetMapping("/users/chats")
    public void listChatsWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userId}/chats")
    public Map<String,List<Map<String, Object>>> listChats(@PathVariable("userId") String userIdString, HttpServletRequest request) {
        
        String path = request.getRequestURI();

        long userId = Long.parseLong(userIdString);
        
        /*
            * Checks if the passed userId is valid
            */
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        
        User user = new User(userId);
        List<Chat> chatsOfUser = queryChat.getChatsForUser(user);
        List<Message> listOfChatIdCreationTsOfLastSentMessageId = queryMessage.getLastSentMessageIdCreationTsForChatsOfUser(userId);
        Map<Long, Timestamp> chatIdCreationTsOflastSentMessageIdMap = new LinkedHashMap<Long, Timestamp>();

        for (Message chatIdCreationTsOfLastSentMessageId : listOfChatIdCreationTsOfLastSentMessageId) {
            chatIdCreationTsOflastSentMessageIdMap.put(chatIdCreationTsOfLastSentMessageId.getChatId(), chatIdCreationTsOfLastSentMessageId.getCreationTs());
        }
        
        for (Chat chat : chatsOfUser) {
            if (chat.getLastSentMessageId() == 0) { 
                chat.setLastSentTime(chat.getCreationTs());
            } else {
                chat.setLastSentTime(chatIdCreationTsOflastSentMessageIdMap.get(chat.getChatId()));
            }
        }
        
        Collections.sort(chatsOfUser, Comparator.comparing(Chat::getLastSentTime).reversed());

        List<Long> listOfChatIdDesc = new ArrayList<Long>();

        for (Chat chat : chatsOfUser) {
            listOfChatIdDesc.add(chat.getChatId());
        }

        List<UsernameChatId> usernameChatIdForSecondUsers = queryUser.getUsernameChatIdForSecondUsers(userId);
        Map<Long, String> chatIdSecondUsernameMap = new LinkedHashMap<Long, String>();

        for (UsernameChatId usernameChatId : usernameChatIdForSecondUsers) {
            chatIdSecondUsernameMap.put(usernameChatId.getChatId(), usernameChatId.getUsername());
        }

        List<Map<String, Object>> chatInfoOfChatsOfUser = new ArrayList<Map<String, Object>>();

        for (Chat chat : chatsOfUser) {
            chatInfoOfChatsOfUser.add(getChatInfoOfChatInMap(chat, chatIdSecondUsernameMap.get(chat.getChatId())));
        }

        return SuccessResponseGenerator.getSuccessResponseForListChats(chatInfoOfChatsOfUser);
    }
}
