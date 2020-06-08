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

import java.util.Map;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public final class GetChat {
    
    @Autowired
    private QueryUser queryUser;

    @Autowired
    private QueryChat queryChat;

    @Autowired
    private QueryUserChat queryUserChat;

    @GetMapping("/users/chats/{chatID}")
    public void getChatWithoutUserIDPathVariable(@PathVariable("chatID") String chatIDString, HttpServletRequest request) {

        String path = request.getRequestUri();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userID}/chats/{chatID}")
    public Map<String, Object> getChat(@PathVariable("userID") String userIDString, @PathVariable("chatID") String chatIDString, HttpServletRequest request) {

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

        if (queryUserChat.checkIfUserChatIDExists(Long.parseLong(userIDString), Long.parseLong(chatIDString)) == false) {
            throw new UserChatIDDoesNotExistException(path);
        }
        
        Chat chat = queryChat.getChat(Long.parseLong(chatIDString));

        return SuccessResponseGenerator.getSuccessResponseForGetChat(chat);
    }
}