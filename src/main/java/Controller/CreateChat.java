package Controller;

import Helper.UniqueIDGenerator;
import Entity.Chat;
import Entity.UserChat;
import DBAccesser.User.UserAccessor;
import DBAccesser.Chat.ChatAccessor;
import DBAccesser.UserChat.UserChatAccessor;
import Helper.SuccessResponseGenerator;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;
import Exceptions.UsernameDoesNotExistException;
import Exceptions.ChatAlreadyExistsException;
import Exceptions.UserChatIDDoesNotExistException;
import Exceptions.UserIDMissingFromRequestURLPathException;
import Exceptions.UsernameMissingFromRequestBodyException;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public final class CreateChat {
    
    @Autowired
    private UserAccessor queryUser;

    @Autowired
    private ChatAccessor insertChat;

    @Autowired
    private UserChatAccessor queryUserChat;

    @Autowired
    private UserChatAccessor insertUserChat;

    @Autowired
    private UniqueIDGenerator uniqueIDGenerator;

    @PostMapping("/users/chats")
    public void createChatWithoutUserIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @PostMapping("/users/{userID}/chats")
    public Map<String, Object> createChat(@PathVariable("userID") String userIDString, 
    @RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        
        String path = request.getRequestURI();

        long userID = Long.parseLong(userIDString);

        //check if request body is as required
        if (!requestBody.containsKey("username")) {
            throw new UsernameMissingFromRequestBodyException(path);
        }

        String username = requestBody.get("username");

        //check if the passed userID is valid
        if (!queryUser.checkIfUserIDExists(userID)) {
            throw new UserIDDoesNotExistException(path);
        }

        //check if username of second user exists - error if does not
        if (!queryUser.checkIfUsernameExists(username)) {
            throw new UsernameDoesNotExistException(path);
        } 

        Chat newChat = new Chat();
        UserChat newUserChat1 = UserChat.newUserChatWithUserID(userID);
        UserChat newUserChat2 = UserChat.newUserChatWithUserID(queryUser.getUserIDFromUsername(username));

        //check if chat between the users already exists
        List<UserChat> resultSet = queryUserChat.getChatIDIfChatExistsBetweenUserIDs(newUserChat1.getUserID(), newUserChat2.getUserID());
        if (!resultSet.isEmpty()) {
            return SuccessResponseGenerator.getSuccessResponseForCreateEntity("Chat", resultSet.get(0).getChatID());
        } 
        
        //generate unique chatID
        newChat.setChatID(uniqueIDGenerator.generateUniqueID("Chat", false, false));

        newUserChat1.setChatID(newChat.getChatID());
        newUserChat2.setChatID(newChat.getChatID());

        insertChat.insertForCreateChatTransaction(newChat, newUserChat1, newUserChat2);

        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("Chat", newChat.getChatID());
    }
}