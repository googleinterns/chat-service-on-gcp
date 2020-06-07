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

import java.util.Map;
import java.util.LinkedHashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class GetMessage {

    @Autowired
    QueryUser queryUserHelper;

    @Autowired
    QueryChat queryChatHelper;

    @Autowired
    QueryMessage queryMessageHelper;

    @Autowired
    QueryUserChat queryUserChatHelper;

    @GetMapping("/users/{userID}/chats/{chatID}/message")
    public Map<String, Object> getMessage(@PathVariable("userID") String userIDString, @PathVariable("chatID") String chatIDString, @RequestParam("messageID") String messageIDString) {

        String path = "/users/" + userIDString + "/chat/" + chatIDString + "/message/?messageID=" + messageIDString;
        Map<String, Object> responseBody;
        
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
        
        //check if the passed messageID is valid
        if (queryMessageHelper.checkIfMessageIDExists(Long.parseLong(messageIDString)) == false) {
            throw new MessageIDDoesNotExistException(path);
        } 

        Message message = queryMessageHelper.getMessage(Long.parseLong(messageIDString));
    
        return SuccessResponseGenerator.getSuccessResponseForGetMessage(message);
    }
}