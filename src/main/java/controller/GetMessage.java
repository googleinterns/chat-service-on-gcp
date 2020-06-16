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

    @GetMapping("/users/{userId}/chats/messages/{messageId}")
    public void getMessageWithoutChatIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new ChatIdMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/chats/{chatId}/messages/{messageId}")
    public void getMessageWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userId}/chats/{chatId}/messages/{messageId}")
    public Map<String, Object> getMessage(@PathVariable("userId") String userIdString, @PathVariable("chatId") String chatIdString, @PathVariable("messageId") String messageIdString, HttpServletRequest request) {

        String path = request.getRequestURI();
        Map<String, Object> responseBody;

        long userId = Long.parseLong(userIdString);
        long chatId = Long.parseLong(chatIdString);
        long messageId = Long.parseLong(messageIdString);

        /*
         * Checks if the passed userId is valid
         */
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        
        /*
         * Checks if the passed chatId is valid
         */
        if (!queryChat.checkIfChatIdExists(chatId)) {
            throw new ChatIdDoesNotExistException(path);
        }

        /*
         * Checks if the user is part of chat
         */
        if (!queryUserChat.checkIfUserChatIdExists(userId, chatId)) {
            throw new UserChatIdDoesNotExistException(path);
        }
        
        /*
         * Checks if the passed messageId is valid
         */
        if (!queryMessage.checkIfMessageIdExists(messageId)) {
            throw new MessageIdDoesNotExistException(path);
        }

        /*
         * Checks if the passed message belongs to chat 
         */
        if (!queryMessage.checkIfMessageIdBelongsToChatId(messageId, chatId)) {
            throw new MessageIdDoesNotBelongToChatIdException(path);
        }

        Message message = queryMessage.getMessage(messageId);
    
        return SuccessResponseGenerator.getSuccessResponseForGetMessage(message);
    }
}
