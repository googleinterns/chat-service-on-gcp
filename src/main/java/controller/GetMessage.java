package controller;

import entity.Message;
import dbaccessor.user.UserAccessor;
import dbaccessor.chat.ChatAccessor;
import dbaccessor.message.MessageAccessor;
import dbaccessor.userchat.UserChatAccessor;
import helper.SuccessResponseGenerator;
import exceptions.UserIdDoesNotExistException;
import exceptions.ChatIdDoesNotExistException;
import exceptions.MessageIdDoesNotExistException;
import exceptions.UserChatIdDoesNotExistException;
import exceptions.UserIdMissingFromRequestURLPathException;
import exceptions.ChatIdMissingFromRequestURLPathException;
import exceptions.MessageIdDoesNotBelongToChatIdException;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller which responds to client requests to get the details of a message.
 * The response contains:
 * (1)  MessageId
 * (2)  ChatId
 * (3)  SentByCurrentUser
 * (4)  ContentType
 * (5)  TextContent
 * (6)  Sent Timestamp
 * (7)  Received Timestamp
 * (8)  Creation Timestamp
 */
@RestController
public final class GetMessage {

    @Autowired
    private UserAccessor queryUser;

    @Autowired
    private ChatAccessor queryChat;

    @Autowired
    private MessageAccessor queryMessage;

    @Autowired
    private UserChatAccessor queryUserChat;

    /**
     * Responds to requests with missing userId URL Path Variable.
     * Throws an exception for the same. 
     */
    @GetMapping("/users/chats/{chatId}/messages/{messageId}")
    public void getMessageWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to requests with missing chatId URL Path Variable.
     * Throws an exception for the same. 
     */
    @GetMapping("/users/{userId}/chats/messages/{messageId}")
    public void getMessageWithoutChatIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new ChatIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to complete requests.
     * Returns details of the requested Message.
     */
    @GetMapping("/users/{userId}/chats/{chatId}/messages/{messageId}")
    public Map<String, Object> getMessage(@PathVariable("userId") String userIdString, @PathVariable("chatId") String chatIdString, @PathVariable("messageId") String messageIdString, HttpServletRequest request) {

        String path = request.getRequestURI();
        Map<String, Object> responseBody;

        long userId = Long.parseLong(userIdString);
        long chatId = Long.parseLong(chatIdString);
        long messageId = Long.parseLong(messageIdString);

        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        
        if (!queryChat.checkIfChatIdExists(chatId)) {
            throw new ChatIdDoesNotExistException(path);
        }

        //Checks if the user is part of chat.
        if (!queryUserChat.checkIfUserChatIdExists(userId, chatId)) {
            throw new UserChatIdDoesNotExistException(path);
        }
        
        if (!queryMessage.checkIfMessageIdExists(messageId)) {
            throw new MessageIdDoesNotExistException(path);
        }

        if (!queryMessage.checkIfMessageIdBelongsToChatId(messageId, chatId)) {
            throw new MessageIdDoesNotBelongToChatIdException(path);
        }

        Message message = queryMessage.getMessage(messageId);
    
        return SuccessResponseGenerator.getSuccessResponseForGetMessage(message);
    }
}