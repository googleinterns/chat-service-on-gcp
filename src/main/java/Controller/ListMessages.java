package Controller;

import Entity.Message;
import DBAccesser.User.QueryUser;
import DBAccesser.Chat.QueryChat;
import DBAccesser.Message.QueryMessage;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class ListMessages {

    @Autowired 
    QueryUser queryUserHelper;

    @Autowired 
    QueryChat queryChatHelper;
    
    @GetMapping("/users/{userID}/chats/{chatID}/messages")
    public Map<String, Object> listMessages(@PathVariable("chatID") String chatIDString, @PathVariable("userID") String userIDString) {

        String path = "/users/" + userIDString + "/chats/" + chatIDString + "/messages";
        
        //check if the passed userID is valid
        if (queryUserHelper.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        }
        
        //check if the passed chatID is valid
        if (queryChatHelper.checkIfChatIDExists(Long.parseLong(chatIDString)) == false) {
            throw new ChatIDDoesNotExistException(path);
        }
        
        return new LinkedHashMap<String, Object>();
    }
}