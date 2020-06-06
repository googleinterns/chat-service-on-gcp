package Controller;

import Entity.Chat;
import DBAccesser.User.QueryUser;
import DBAccesser.Chat.QueryChat;
import DBAccesser.UserChat.QueryUserChat;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;
import Exceptions.UserChatIDDoesNotExistException;

import java.util.Map;
import java.util.LinkedHashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class GetChat {
    
    @Autowired
    QueryUser queryUserHelper;

    @Autowired
    QueryChat queryChatHelper;

    @Autowired
    QueryUserChat queryUserChatHelper;

    @GetMapping("/users/{userID}/chat")
    public Map<String, Object> getChat(@PathVariable("userID") String userIDString, @RequestParam("chatID") String chatIDString) {

        String path = "/users/" + userIDString + "/chat/?chatID="+chatIDString;
        Map<String, Object> responseBody;

        //check if the passed userID is valid
        if (queryUserHelper.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        }
        
        //check if the passed chatID is valid
        if (queryChatHelper.checkIfChatIDExists(Long.parseLong(chatIDString)) == false) {
            throw new ChatIDDoesNotExistException(path);
        } 

        if (queryUserChatHelper.checkIfUserChatIDExists(Long.parseLong(userIDString), Long.parseLong(chatIDString)) == false) {
            throw new UserChatIDDoesNotExistException(path);
        }
        
        Chat chat = queryChatHelper.getChat(Long.parseLong(chatIDString));
        responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("ChatID", chat.getChatID());
        responseBody.put("LastSentMessageID", chat.getLastSentMessageID());
        responseBody.put("CreationTS", chat.getCreationTS());

        return responseBody;
        
        /*
        //test without Spanner
        if (Long.parseLong(chatIDString) == 5) {
            throw new ChatIDDoesNotExistException(path);
        } else {
            Chat chat = queryChatHelper.getChat(Long.parseLong(chatIDString), 2);
            responseBody = new LinkedHashMap<String, Object>();
            responseBody.put("ChatID", user.getChatID());
            responseBody.put("LastSentMessageID", user.getLastSentMessageID());
        }
        
        return responseBody;
        */
    }
}