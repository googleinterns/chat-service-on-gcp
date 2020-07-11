package controller;

import helper.UniqueIdGenerator;
import entity.Chat;
import entity.UserChat;
import dbaccessor.user.UserAccessor;
import dbaccessor.chat.ChatAccessor;
import dbaccessor.userchat.UserChatAccessor;
import helper.SuccessResponseGenerator;
import exceptions.UserIdDoesNotExistException;
import exceptions.UsernameDoesNotExistException;
import exceptions.UserIdMissingFromRequestURLPathException;
import exceptions.UsernameMissingFromRequestBodyException;

import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;

/**
 * Controller which responds to client requests to create a Chat between two Users.
 * Sends the ChatId of the newly created Chat (or of the already existing Chat between the two Users) in response.
 */
@RestController
public final class CreateChat {
    
    @Autowired
    private UserAccessor queryUser;

    @Autowired
    private ChatAccessor insertChat;

    @Autowired
    private UserChatAccessor queryUserChat;

    @Autowired
    private UserChatAccessor insertUserChat;

    @Autowired
    private UniqueIdGenerator uniqueIdGenerator;

    /**
     * Encapsulation for the following fields related to a Chat for one of the Users engaged in that Chat. 
     * <ol>
     * <li> ChatId </li>
     * <li> UserID of Second User in Chat </li>
     * </ol>
     */
    public static final class ChatIdWithUserIds {

        @Column(name = "ChatID")
        private long chatId;

        @Column(name = "UserID1")
        private long userId1;

        @Column(name = "UserID2")
        private long userId2;

        public ChatIdWithUserIds() {}

        public ChatIdWithUserIds(long chatId, long userId2) {
            this.chatId = chatId;
            this.userId2 = userId2;
        }

        public void setChatId(long chatId) {
            this.chatId = chatId;
        }

        public void setUserId1(long userId1) {
            this.userId1 = userId1;
        }

        public void setUserId2(long userId2) {
            this.userId2 = userId2;
        }

        public long getChatId() {
            return chatId;
        }

        public long getUserId1() {
            return userId1;
        }

        public long getUserId2() {
            return userId2;
        }
    }

    /**
     * Responds to requests with missing userId URL Path Variable.
     * Throws an exception for the same. 
     */
    @PostMapping("/users/chats")
    public void createChatWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to complete requests.
     * Creates a Chat between the two Users, if does not already exist.
     * Returns ChatId of the Chat between the two Users.
     */
    @PostMapping("/users/{userId}/chats")
    public ImmutableMap<String, Object> createChat(@PathVariable("userId") String userIdString, 
    @RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        
        String path = request.getRequestURI();

        long userId = Long.parseLong(userIdString);

        if (!requestBody.containsKey("username")) {
            throw new UsernameMissingFromRequestBodyException(path);
        }

        String username = requestBody.get("username");

        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }

        if (!queryUser.checkIfUsernameExists(username)) {
            throw new UsernameDoesNotExistException(path);
        } 

        ChatIdWithUserIds chatIdWithUserIds = queryUserChat.getChatIdWithUserIdsIfExistsBetweenUsers(userId, username);

        long chatId = chatIdWithUserIds.getChatId();
        if (chatId != 0) {
            return SuccessResponseGenerator.getSuccessResponseForCreateEntity("Chat", chatId);
        }

        Chat newChat = new Chat();
        newChat.setChatId(uniqueIdGenerator.generateUniqueId("Chat"));

        chatId = newChat.getChatId();

        UserChat newUserChat1 = new UserChat(userId, chatId);
        UserChat newUserChat2 = new UserChat(chatIdWithUserIds.getUserId2(), chatId);

        insertChat.insertForCreateChatTransaction(newChat, newUserChat1, newUserChat2);

        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("Chat", chatId);
    }
}
