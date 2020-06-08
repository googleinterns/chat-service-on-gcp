package Controller;

import Entity.Chat;
import DBAccesser.User.UserAccessor;
import DBAccesser.Chat.ChatAccessor;
import DBAccesser.UserChat.UserChatAccessor;
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
    private UserAccessor queryUser;

    @Autowired
    private ChatAccessor queryChat;

    @Autowired
    private UserChatAccessor queryUserChat;

    @GetMapping("/users/chats/{chatID}")
    public void getChatWithoutUserIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userID}/chats/{chatID}")
    public Map<String, Object> getChat(@PathVariable("userID") String userIDString, @PathVariable("chatID") String chatIDString, HttpServletRequest request) {

        String path = request.getRequestURI();
        Map<String, Object> responseBody;

        long userID = Long.parseLong(userIDString);
        long chatID = Long.parseLong(chatIDString);

        //check if the passed userID is valid
        if (!queryUser.checkIfUserIDExists(userID)) {
            throw new UserIDDoesNotExistException(path);
        }
        
        //check if the passed chatID is valid
        if (!queryChat.checkIfChatIDExists(chatID)) {
            throw new ChatIDDoesNotExistException(path);
        } 

        if (!queryUserChat.checkIfUserChatIDExists(userID, chatID)) {
            throw new UserChatIDDoesNotExistException(path);
        }
        
        Chat chat = queryChat.getChat(chatID);

        return SuccessResponseGenerator.getSuccessResponseForGetChat(chat);
    }
}