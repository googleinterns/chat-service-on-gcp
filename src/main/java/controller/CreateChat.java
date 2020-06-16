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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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

    @PostMapping("/users/chats")
    public void createChatWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    @PostMapping("/users/{userId}/chats")
    public Map<String, Object> createChat(@PathVariable("userId") String userIdString, 
    @RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        
        String path = request.getRequestURI();

        long userId = Long.parseLong(userIdString);

        /*
         * Checks if request body is as required
         */
        if (!requestBody.containsKey("username")) {
            throw new UsernameMissingFromRequestBodyException(path);
        }

        String username = requestBody.get("username");

        /*
         * Checks if the passed userId is valid
         */
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }

        /*
         * Checks if username of second user exists
         */
        if (!queryUser.checkIfUsernameExists(username)) {
            throw new UsernameDoesNotExistException(path);
        } 

        Chat newChat = new Chat();
        UserChat newUserChat1 = UserChat.newUserChatWithUserId(userId);
        UserChat newUserChat2 = UserChat.newUserChatWithUserId(queryUser.getUserIdFromUsername(username));

        /*
         * Checks if chat between the users already exists
         */
        List<UserChat> resultSet = queryUserChat.getChatIdIfChatExistsBetweenUserIds(newUserChat1.getUserId(), newUserChat2.getUserId());
        if (!resultSet.isEmpty()) {
            return SuccessResponseGenerator.getSuccessResponseForCreateEntity("Chat", resultSet.get(0).getChatId());
        } 
        
        /*
         * Generates unique chatId
         */
        newChat.setChatId(uniqueIdGenerator.generateUniqueId("Chat"));

        newUserChat1.setChatId(newChat.getChatId());
        newUserChat2.setChatId(newChat.getChatId());

        insertChat.insertForCreateChatTransaction(newChat, newUserChat1, newUserChat2);

        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("Chat", newChat.getChatId());
    }
}
