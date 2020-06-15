package Controller;

import Entity.Chat;
import DBAccesser.User.UserAccessor;
import DBAccesser.Chat.ChatAccessor;
import DBAccesser.UserChat.UserChatAccessor;
import Helper.SuccessResponseGenerator;
import Exceptions.UserIdDoesNotExistException;
import Exceptions.ChatIdDoesNotExistException;
import Exceptions.UserChatIdDoesNotExistException;
import Exceptions.UserIdMissingFromRequestURLPathException;

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

    @GetMapping("/users/chats/{chatId}")
    public void getChatWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userId}/chats/{chatId}")
    public Map<String, Object> getChat(@PathVariable("userId") String userIdString, @PathVariable("chatId") String chatIdString, HttpServletRequest request) {

        String path = request.getRequestURI();
        Map<String, Object> responseBody;

        long userId = Long.parseLong(userIdString);
        long chatId = Long.parseLong(chatIdString);

        //check if the passed userId is valid
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        
        //check if the passed chatId is valid
        if (!queryChat.checkIfChatIdExists(chatId)) {
            throw new ChatIdDoesNotExistException(path);
        } 

        if (!queryUserChat.checkIfUserChatIdExists(userId, chatId)) {
            throw new UserChatIdDoesNotExistException(path);
        }
        
        Chat chat = queryChat.getChat(chatId);

        return SuccessResponseGenerator.getSuccessResponseForGetChat(chat);
    }
}
