package Controller;

import Helper.Helper;
import Entity.Chat;
import Entity.Message;
import DBAccesser.User.QueryUser;
import DBAccesser.Chat.QueryChat;
import DBAccesser.Message.QueryMessage;
import DBAccesser.UserChat.QueryUserChat;
import DBAccesser.Message.InsertMessage;
import DBAccesser.Chat.InsertChat;
import Helper.SuccessResponseGenerator;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;
import Exceptions.MessageIDDoesNotExistException;
import Exceptions.UserChatIDDoesNotExistException;
import Exceptions.UserIDMissingFromRequestURLPathException;
import Exceptions.ChatIDMissingFromRequestURLPathException;
import Exceptions.ContentTypeMissingFromRequestBodyException;
import Exceptions.TextContentMissingFromRequestBodyException;

import java.util.Map;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public final class CreateMessage {

    @Autowired
    private QueryUser queryUser;

    @Autowired
    private QueryChat queryChat;

    @Autowired
    private QueryUserChat queryUserChat;

    @Autowired
    private InsertMessage insertMessage;

    @Autowired
    private InsertChat insertChat;

    @Autowired
    private Helper helper; 

    @PostMapping("/users/chats/messages")
    public void createMessageWithoutUserIDChatIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @PostMapping("/users/chats/{chatID}/messages")
    public void createMessageWithoutUserIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @PostMapping("/users/{userID}/chats/messages")
    public void createMessageWithoutChatIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();
        throw new ChatIDMissingFromRequestURLPathException(path);
    }
    
    @PostMapping("/users/{userID}/chats/{chatID}/messages")
    public Map<String, Object> createMessage(@PathVariable("userID") String userIDString, @PathVariable("chatID") String chatIDString, @RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        
        String path = request.getRequestURI();

        long userID = Long.parseLong(userIDString);
        long chatID = Long.parseLong(chatIDString);

        //check if request body is as required
        if (requestBody.containsKey("contentType") == false) {
            throw new ContentTypeMissingFromRequestBodyException(path);
        }

        //check if request body is as required
        if (requestBody.containsKey("textContent") == false) {
            throw new TextContentMissingFromRequestBodyException(path);
        }
        
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
        
        Message newMessage = new Message(chatID, userID, requestBody.get("contentType"), requestBody.get("textContent"));

        newMessage.setMessageID(helper.generateUniqueID("Message", false, false));

        insertMessage.insertAllForTextMessage(newMessage);

        insertChat.insertLastSentMessageID(new Chat(chatID, newMessage.getMessageID()));
        
        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("Message", newMessage.getMessageID());
    }
}