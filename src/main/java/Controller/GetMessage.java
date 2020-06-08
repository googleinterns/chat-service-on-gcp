package Controller;

import Entity.Message;
import DBAccesser.User.QueryUser;
import DBAccesser.Chat.QueryChat;
import DBAccesser.Message.QueryMessage;
import DBAccesser.UserChat.QueryUserChat;
import Helper.SuccessResponseGenerator;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;
import Exceptions.MessageIDDoesNotExistException;
import Exceptions.UserChatIDDoesNotExistException;
import Exceptions.UserIDMissingFromRequestURLPathException;
import Exceptions.ChatIDMissingFromRequestURLPathException;
import Exceptions.MessageIDMissingFromRequestURLParameterException;

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
    private QueryUser queryUser;

    @Autowired
    private QueryChat queryChat;

    @Autowired
    private QueryMessage queryMessage;

    @Autowired
    private QueryUserChat queryUserChat;

    @GetMapping("/users/{userID}/chats/messages/{messageID}")
    public void getMessageWithoutChatIDPathVariable(@PathVariable("userID") String userIDString, @PathVariable("messageID") String messageIDString, HttpServletRequest request) {

        String path = request.getRequestUri();

        throw new ChatIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/chats/{chatID}/messages/{messageID}")
    public void getMessageWithoutUserIDPathVariable(@PathVariable("chatID") String chatIDString, @PathVariable("messageID") String messageIDString, HttpServletRequest request) {

        String path = request.getRequestUri();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userID}/chats/{chatID}/messages/{messageID}")
    public Map<String, Object> getMessage(@PathVariable("userID") String userIDString, @PathVariable("chatID") String chatIDString, @PathVariable("messageID") String messageIDString, HttpServletRequest request) {

        String path = request.getRequestUri();
        Map<String, Object> responseBody;

        //check if the passed userID is valid
        if (queryUser.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        }
        
        //check if the passed chatID is valid
        if (queryChat.checkIfChatIDExists(Long.parseLong(chatIDString)) == false) {
            throw new ChatIDDoesNotExistException(path);
        }

        //check if user is part of chat
        if (queryUserChat.checkIfUserChatIDExists(Long.parseLong(userIDString), Long.parseLong(chatIDString)) == false) {
            throw new UserChatIDDoesNotExistException(path);
        }
        
        //check if the passed messageID is valid
        if (queryMessage.checkIfMessageIDExists(Long.parseLong(messageIDString)) == false) {
            throw new MessageIDDoesNotExistException(path);
        } 

        Message message = queryMessage.getMessage(Long.parseLong(messageIDString));
    
        return SuccessResponseGenerator.getSuccessResponseForGetMessage(message);
    }
}