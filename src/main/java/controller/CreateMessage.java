package controller;

import helper.UniqueIdGenerator;
import entity.Chat;
import entity.Message;
import dbaccessor.user.UserAccessor;
import dbaccessor.chat.ChatAccessor;
import dbaccessor.message.MessageAccessor;
import dbaccessor.userchat.UserChatAccessor;
import helper.SuccessResponseGenerator;
import exceptions.UserIdDoesNotExistException;
import exceptions.ChatIdDoesNotExistException;
import exceptions.UserChatIdDoesNotExistException;
import exceptions.UserIdMissingFromRequestURLPathException;
import exceptions.ChatIdMissingFromRequestURLPathException;
import exceptions.ContentTypeMissingFromRequestBodyException;
import exceptions.TextContentMissingFromRequestBodyException;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller which responds to client requests to create (and send) a message in a chat.
 * Sends the MessageId of the newly created message in response.
 */
@RestController
public final class CreateMessage {

    @Autowired
    private UserAccessor queryUser;

    @Autowired
    private ChatAccessor queryChat;

    @Autowired
    private UserChatAccessor queryUserChat;

    @Autowired
    private MessageAccessor insertMessage;

    @Autowired
    private ChatAccessor insertChat;

    @Autowired
    private UniqueIdGenerator uniqueIdGenerator; 

    /**
     * Responds to requests with missing userId and chatId URL Path Variables.
     * Throws an exception for missing userId URL Path Variable. 
     */
    @PostMapping("/users/chats/messages")
    public void createMessageWithoutUserIdChatIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to requests with missing userId URL Path Variable.
     * Throws an exception for the same. 
     */
    @PostMapping("/users/chats/{chatId}/messages")
    public void createMessageWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to requests with missing chatId URL Path Variable.
     * Throws an exception for the same. 
     */
    @PostMapping("/users/{userId}/chats/messages")
    public void createMessageWithoutChatIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();
        throw new ChatIdMissingFromRequestURLPathException(path);
    }
    
    /**
     * Responds to complete requests.
     * Creates and sends a Message from current User in the given Chat.
     * Returns MessageId of the sent Message.
     */
    @PostMapping("/users/{userId}/chats/{chatId}/messages")
    public Map<String, Object> createMessage(@PathVariable("userId") String userIdString, @PathVariable("chatId") String chatIdString, @RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        
        String path = request.getRequestURI();

        long userId = Long.parseLong(userIdString);
        long chatId = Long.parseLong(chatIdString);

        if (!requestBody.containsKey("contentType")) {
            throw new ContentTypeMissingFromRequestBodyException(path);
        }

        String contentType = requestBody.get("contentType");

        if (!requestBody.containsKey("textContent")) {
            throw new TextContentMissingFromRequestBodyException(path);
        }

        String textContent = requestBody.get("textContent");
       
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        
        if (!queryChat.checkIfChatIdExists(chatId)) {
            throw new ChatIdDoesNotExistException(path);
        }

        //Checks if user is part of chat.
        if (!queryUserChat.checkIfUserChatIdExists(userId, chatId)) {
            throw new UserChatIdDoesNotExistException(path);
        }
        
        Message newMessage = new Message(chatId, userId, contentType, textContent);

        newMessage.setMessageId(uniqueIdGenerator.generateUniqueId("Message"));

        insertMessage.insertForCreateMessageTransaction(newMessage, new Chat(chatId, newMessage.getMessageId()));
        
        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("Message", newMessage.getMessageId());
    }
}
