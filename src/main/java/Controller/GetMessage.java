package Controller;

import Entity.Message;
import DBAccesser.User.UserAccessor;
import DBAccesser.Chat.ChatAccessor;
import DBAccesser.Message.MessageAccessor;
import DBAccesser.UserChat.UserChatAccessor;
import Helper.SuccessResponseGenerator;
import Exceptions.UserIdDoesNotExistException;
import Exceptions.ChatIdDoesNotExistException;
import Exceptions.MessageIdDoesNotExistException;
import Exceptions.UserChatIdDoesNotExistException;
import Exceptions.UserIdMissingFromRequestURLPathException;
import Exceptions.ChatIdMissingFromRequestURLPathException;
import Exceptions.MessageIdMissingFromRequestURLParameterException;
import Exceptions.MessageIdDoesNotBelongToChatIdException;

import java.util.Map;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
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
        
        //check if the passed messageId is valid
        if (!queryMessage.checkIfMessageIdExists(messageId)) {
            throw new MessageIdDoesNotExistException(path);
        }

        //check if passed message belongs to chat 
        if (!queryMessage.checkIfMessageIdBelongsToChatId(messageId, chatId)) {
            throw new MessageIdDoesNotBelongToChatIdException(path);
        }

        Message message = queryMessage.getMessage(messageId);
    
        return SuccessResponseGenerator.getSuccessResponseForGetMessage(message);
    }
}
