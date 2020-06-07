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
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;
import Exceptions.MessageIDDoesNotExistException;
import Exceptions.UserChatIDDoesNotExistException;

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
public class CreateMessage {

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
    
    @PostMapping("/users/{userID}/chats/{chatID}/messages")
    public Map<String, String> createMessage(@PathVariable("chatID") String chatIDString, @PathVariable("userID") String userIDString, @RequestBody Map<String, String> requestBody) {
        
        String path = "/users/" + userIDString + "/chats/" + chatIDString + "/messages";
        Map<String, String> responseBody;
        
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
        
        responseBody = new LinkedHashMap<String, String>();
        responseBody.put("message", "Created");

        return responseBody;
        
        /*
        //test without Spanner - change the return type of the method to Map<String, Object>
        Message newMessage = new Message(Long.parseLong(chatIDString), Long.parseLong(userIDString), requestBody.get("contentType"), requestBody.get("textContent"));

        newMessage.setMessageID(1);

        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("message", "Created");
        responseBody.put("messageID", newMessage.getMessageID());
        responseBody.put("getChatID", newMessage.getChatID());
        responseBody.put("getSenderID", newMessage.getSenderID());
        responseBody.put("getContentType", newMessage.getContentType());
        responseBody.put("getTextContent", newMessage.getTextContent());

        return responseBody;
        */
    }
}