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

    @PostMapping("/users/chats/messages")
    public void createMessageWithoutUserIdChatIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    @PostMapping("/users/chats/{chatId}/messages")
    public void createMessageWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    @PostMapping("/users/{userId}/chats/messages")
    public void createMessageWithoutChatIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();
        throw new ChatIdMissingFromRequestURLPathException(path);
    }
    
    @PostMapping("/users/{userId}/chats/{chatId}/messages")
    public Map<String, Object> createMessage(@PathVariable("userId") String userIdString, @PathVariable("chatId") String chatIdString, @RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        
        String path = request.getRequestURI();

        long userId = Long.parseLong(userIdString);
        long chatId = Long.parseLong(chatIdString);

        //check if request body is as required
        if (!requestBody.containsKey("contentType")) {
            throw new ContentTypeMissingFromRequestBodyException(path);
        }

        String contentType = requestBody.get("contentType");

        //check if request body is as required
        if (!requestBody.containsKey("textContent")) {
            throw new TextContentMissingFromRequestBodyException(path);
        }

        String textContent = requestBody.get("textContent");
        
        //check if the passed userId is valid
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        
        //check if the passed chatId is valid
        if (!queryChat.checkIfChatIdExists(chatId)) {
            throw new ChatIdDoesNotExistException(path);
        }

        //check if user is part of chat
        if (!queryUserChat.checkIfUserChatIdExists(userId, chatId)) {
            throw new UserChatIdDoesNotExistException(path);
        }
        
        Message newMessage = new Message(chatId, userId, contentType, textContent);

        newMessage.setMessageId(uniqueIdGenerator.generateUniqueId("Message"));

        insertMessage.insertAllForTextMessage(newMessage);

        insertChat.insertLastSentMessageId(new Chat(chatId, newMessage.getMessageId()));
        
        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("Message", newMessage.getMessageId());
    }
}
