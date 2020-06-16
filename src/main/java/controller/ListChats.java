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

/**
 * Controller which responds to client requests to get the list of chats which the user is engaged in.
 * Each entry in the response contains:
 * (1)  ChatId
 * (2)  Username of the other user
 * (3)  LastSentMessageId
 */
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

    /**
     * Encapsulation for ChatId and Username of one of the Users engaged in that Chat. 
     */
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

    /**
     * Returns details of given Chat in a Map.
     * Details include:
     * (1)  ChatId
     * (2)  Username (of the other user)
     * (3)  LastSentMessageId
     */
    Map<String, Object> getChatInfoOfChatInMap(Chat chat, String usernameOfSecondUser) {
        
        Map<String, Object> chatInfoOfChatInMap = new LinkedHashMap<String, Object>();
        chatInfoOfChatInMap.put("ChatId", chat.getChatId());
        chatInfoOfChatInMap.put("Username", usernameOfSecondUser);
        chatInfoOfChatInMap.put("LastSentMessageId", chat.getLastSentMessageId());

        return chatInfoOfChatInMap;
    }

    /**
     * Responds to requests with missing userId URL Path Variable.
     * Throws an exception for the same. 
     */
    @GetMapping("/users/chats")
    public void listChatsWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to complete requests.
     * Returns the list of chats which the User id engaged in.
     */
    @GetMapping("/users/{userId}/chats")
    public Map<String,List<Map<String, Object>>> listChats(@PathVariable("userId") String userIdString, HttpServletRequest request) {
        
        String path = request.getRequestURI();

        long userId = Long.parseLong(userIdString);
        
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        
        User user = new User(userId);
        List<Chat> chatsOfUser = queryChat.getChatsForUser(user);
        List<Message> listOfChatIdCreationTsOfLastSentMessageId = queryMessage.getCreationTsOfLastSentMessageIdForChatsOfUser(userId);

        /*
         * Stores time of the last Message sent in each ChatId of the User against ChatId.
         */
        Map<Long, Timestamp> chatIdCreationTsOflastSentMessageIdMap = new LinkedHashMap<Long, Timestamp>();
        for (Message chatIdCreationTsOfLastSentMessageId : listOfChatIdCreationTsOfLastSentMessageId) {
            chatIdCreationTsOflastSentMessageIdMap.put(chatIdCreationTsOfLastSentMessageId.getChatId(), chatIdCreationTsOfLastSentMessageId.getCreationTs());
        }
        
        /*
         * Sets time of the last Message sent for each Chat of the User.
         */
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

        /*
         * Stores username of the other User against ChatId for each Chat of the User.
         */
        Map<Long, String> chatIdSecondUsernameMap = new LinkedHashMap<Long, String>();

        for (UsernameChatId usernameChatId : usernameChatIdForSecondUsers) {
            chatIdSecondUsernameMap.put(usernameChatId.getChatId(), usernameChatId.getUsername());
        }

        /*
         * Stores list of all details of each Chat of the User.
         */
        List<Map<String, Object>> chatInfoOfChatsOfUser = new ArrayList<Map<String, Object>>();

        for (Chat chat : chatsOfUser) {
            chatInfoOfChatsOfUser.add(getChatInfoOfChatInMap(chat, chatIdSecondUsernameMap.get(chat.getChatId())));
        }

        return SuccessResponseGenerator.getSuccessResponseForListChats(chatInfoOfChatsOfUser);
    }
}
