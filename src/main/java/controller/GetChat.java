package controller;

import entity.Chat;
import dbaccessor.user.UserAccessor;
import dbaccessor.chat.ChatAccessor;
import dbaccessor.userchat.UserChatAccessor;
import helper.SuccessResponseGenerator;
import exceptions.UserIdDoesNotExistException;
import exceptions.ChatIdDoesNotExistException;
import exceptions.UserChatIdDoesNotExistException;
import exceptions.UserIdMissingFromRequestURLPathException;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller which responds to client requests to get the details of a chat.
 * The response contains:
 * <ol>
 * <li> ChatId </li>
 * <li> LastSentMessageId </li>
 * <li> Creation Timestamp </li>
 * </ol>
 */
@RestController
public final class GetChat {
    
    @Autowired
    private UserAccessor queryUser;

    @Autowired
    private ChatAccessor queryChat;

    @Autowired
    private UserChatAccessor queryUserChat;

    /**
     * Responds to requests with missing chatId URL Path Variable.
     * Throws an exception for the same. 
     */
    @GetMapping("/users/chats/{chatId}")
    public void getChatWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to complete requests.
     * Returns details of the requested Chat.
     */
    @GetMapping("/users/{userId}/chats/{chatId}")
    public ImmutableMap<String, Object> getChat(@PathVariable("userId") String userIdString, @PathVariable("chatId") String chatIdString, HttpServletRequest request) {

        String path = request.getRequestURI();
        Map<String, Object> responseBody;

        long userId = Long.parseLong(userIdString);
        long chatId = Long.parseLong(chatIdString);

        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        
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
