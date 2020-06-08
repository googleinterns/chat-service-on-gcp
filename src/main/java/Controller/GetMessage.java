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
import Exceptions.MessageIDDoesNotBelongToChatIDException;

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
    public void getMessageWithoutChatIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new ChatIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/chats/{chatID}/messages/{messageID}")
    public void getMessageWithoutUserIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userID}/chats/{chatID}/messages/{messageID}")
    public Map<String, Object> getMessage(@PathVariable("userID") String userIDString, @PathVariable("chatID") String chatIDString, @PathVariable("messageID") String messageIDString, HttpServletRequest request) {

        String path = request.getRequestURI();
        Map<String, Object> responseBody;

        long userID = Long.parseLong(userIDString);
        long chatID = Long.parseLong(chatIDString);
        long messageID = Long.parseLong(messageIDString);

        //check if the passed userID is valid
        if (queryUser.checkIfUserIDExists(userID) == false) {
            throw new UserIDDoesNotExistException(path);
        }
        
        //check if the passed chatID is valid
        if (queryChat.checkIfChatIDExists(chatID) == false) {
            throw new ChatIDDoesNotExistException(path);
        }

        //check if user is part of chat
        if (queryUserChat.checkIfUserChatIDExists(userID, chatID) == false) {
            throw new UserChatIDDoesNotExistException(path);
        }
        
        //check if the passed messageID is valid
        if (queryMessage.checkIfMessageIDExists(messageID) == false) {
            throw new MessageIDDoesNotExistException(path);
        }

        //check if passed message belongs to chat 
        if (queryMessage.checkIfMessageIDBelongsToChatID(messageID, chatID) == false) {
            throw new MessageIDDoesNotBelongToChatIDException(path);
        }

        Message message = queryMessage.getMessage(messageID);
    
        return SuccessResponseGenerator.getSuccessResponseForGetMessage(message);
    }
}