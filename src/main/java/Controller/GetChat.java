package Controller;

import Entity.Chat;
import DBAccesser.User.QueryUser;
import DBAccesser.Chat.QueryChat;
import DBAccesser.UserChat.QueryUserChat;
import Helper.SuccessResponseGenerator;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;
import Exceptions.UserChatIDDoesNotExistException;
import Exceptions.UserIDMissingFromRequestURLPathException;
import Exceptions.ChatIDMissingFromRequestURLParameterException;

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

    @GetMapping("/users/chat")
    public void getChatWithoutUserIDPathVariable() {

        String path = "/users/chat";

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userID}/chat")
    public Map<String, Object> getChat(@PathVariable("userID") String userIDString, @RequestParam(value = "chatID", required = false) String chatIDString) {

        String path = "/users/" + userIDString + "/chat/?chatID="+chatIDString;
        Map<String, Object> responseBody;

        //check if URL parameter has been provided
        if (chatIDString == null) {
            throw new ChatIDMissingFromRequestURLParameterException(path);
        }

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

        return SuccessResponseGenerator.getSuccessResponseForGetChat(chat);
    }
}