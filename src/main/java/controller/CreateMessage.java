package controller;

import helper.UniqueIdGenerator;
import entity.Chat;
import entity.Message;
import entity.Attachment;
import dbaccessor.user.UserAccessor;
import dbaccessor.chat.ChatAccessor;
import dbaccessor.message.MessageAccessor;
import dbaccessor.userchat.UserChatAccessor;
import helper.SuccessResponseGenerator;
import exceptions.EmptyMessageException;
import exceptions.UserIdDoesNotExistException;
import exceptions.ChatIdDoesNotExistException;
import exceptions.UserChatIdDoesNotExistException;
import exceptions.UserIdMissingFromRequestURLPathException;
import exceptions.ChatIdMissingFromRequestURLPathException;

import java.util.Map;
import java.util.Optional;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
    public Map<String, Object> createMessage(@PathVariable("userId") String userIdString, @PathVariable("chatId") String chatIdString, @RequestParam(value = "textContent", required = false) String textContent, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) throws IOException {

        String path = request.getRequestURI();

        long userId = Long.parseLong(userIdString);
        long chatId = Long.parseLong(chatIdString);
        Message newMessage;
        Attachment attachment = null;
       
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

        if (textContent == null && file == null) {
            throw new EmptyMessageException(path);
        }
        
        if (textContent != null) {
            newMessage = new Message(chatId, userId, textContent);
        } else {
            newMessage = new Message(chatId, userId);
        }

        newMessage.setMessageId(uniqueIdGenerator.generateUniqueId("Message"));

        if (file != null) {
            attachment = new Attachment(uniqueIdGenerator.generateUniqueId("Attachment"), file);
            newMessage.setAttachmentId(attachment.getAttachmentId());
        } 

        if (!insertMessage.createMessageInTransaction(newMessage, new Chat(chatId, newMessage.getMessageId()), Optional.ofNullable(attachment))) {
            throw new IOException("IOException Occurred While Parsing File");
        }
        
        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("Message", newMessage.getMessageId());
    }
}
