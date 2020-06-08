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
    QueryUser queryUserHelper;

    @Autowired
    QueryChat queryChatHelper;

    @Autowired
    QueryMessage queryMessageHelper;

    @Autowired
    QueryUserChat queryUserChatHelper;

    @Autowired
    InsertMessage insertMessageHelper;

    @Autowired
    InsertChat insertChatHelper;

    @Autowired
    Helper helper; 

    @PostMapping("/users/chats/messages")
    public void createMessageWithoutUserIDChatIDPathVariable() {

        String path = "/users/chats/messages";

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @PostMapping("/users/chats/{chatID}/messages")
    public void createMessageWithoutUserIDPathVariable(@PathVariable("chatID") String chatIDString) {

        String path = "/users/chats/" + chatIDString + "/messages";

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @PostMapping("/users/{userID}/chats/messages")
    public void createMessageWithoutChatIDPathVariable(@PathVariable("userID") String userIDString) {

        String path = "/users/" + userIDString + "/chats/messages";

        throw new ChatIDMissingFromRequestURLPathException(path);
    }
    
    @PostMapping("/users/{userID}/chats/{chatID}/messages")
    public Map<String, String> createMessage(@PathVariable("userID") String userIDString, @PathVariable("chatID") String chatIDString, @RequestBody Map<String, String> requestBody) {
        
        String path = "/users/" + userIDString + "/chats/" + chatIDString + "/messages";
        Map<String, String> responseBody;

        //check if request body is as required
        if (requestBody.containsKey("contentType") == false) {
            throw new ContentTypeMissingFromRequestBodyException(path);
        }

        //check if request body is as required
        if (requestBody.containsKey("textContent") == false) {
            throw new TextContentMissingFromRequestBodyException(path);
        }
        
        //check if the passed userID is valid
        if (queryUserHelper.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        }
        
        //check if the passed chatID is valid
        if (queryChatHelper.checkIfChatIDExists(Long.parseLong(chatIDString)) == false) {
            throw new ChatIDDoesNotExistException(path);
        }

        //check if user is part of chat
        if (queryUserChatHelper.checkIfUserChatIDExists(Long.parseLong(userIDString), Long.parseLong(chatIDString)) == false) {
            throw new UserChatIDDoesNotExistException(path);
        }
        
        Message newMessage = new Message(Long.parseLong(chatIDString), Long.parseLong(userIDString), requestBody.get("contentType"), requestBody.get("textContent"));

        newMessage.setMessageID(helper.generateUniqueID("Message", false, false));

        insertMessageHelper.insertAllForTextMessage(newMessage);

        insertChatHelper.insertLastSentMessageID(new Chat(Long.parseLong(chatIDString), newMessage.getMessageID()));
        
        return SuccessResponseGenerator.getSuccessResponseForCreateEntity();
    }
}