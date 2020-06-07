package Controller;

import Helper.Helper;
import Entity.Chat;
import Entity.UserChat;
import DBAccesser.User.QueryUser;
import DBAccesser.Chat.QueryChat;
import DBAccesser.UserChat.QueryUserChat;
import DBAccesser.Chat.InsertChat;
import DBAccesser.UserChat.InsertUserChat;
import Helper.SuccessResponseGenerator;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;
import Exceptions.UsernameDoesNotExistException;
import Exceptions.ChatAlreadyExistsException;
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
public class CreateChat {
    
    @Autowired
    QueryUser queryUserHelper;

    @Autowired
    QueryChat queryChatHelper;

    @Autowired
    InsertChat insertChatHelper;

    @Autowired
    QueryUserChat queryUserChatHelper;

    @Autowired
    InsertUserChat insertUserChatHelper;

    @Autowired
    Helper helper;

    @PostMapping("/users/{userID}/chats")
    public Map<String, String> createChat(@PathVariable("userID") String userIDString, @RequestBody Map<String, String> requestBody) {
        
        String path = "/users/" + userIDString + "/chats";
        
        Map<String, String> responseBody;
        
        //check if the passed userID is valid
        if (queryUserHelper.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        }

        //check if username of second user exists - error if does not
        if (queryUserHelper.checkIfUsernameExists(requestBody.get("username")) == false) {
            throw new UsernameDoesNotExistException(path);
        } 

        Chat newChat = new Chat();
        UserChat newUserChat1 = new UserChat(Long.parseLong(userIDString), "UserID");
        UserChat newUserChat2 = new UserChat(queryUserHelper.getUserIDFromUsername(requestBody.get("username")), "UserID");

        //check if chat between the users already exists - return error if it does
        if (queryUserChatHelper.checkIfChatExistsBetweenUserIDs(newUserChat1.userID, newUserChat2.userID)) {
            throw new ChatAlreadyExistsException(path);
        } 
        
        //generate unique chatID
        newChat.setChatID(helper.generateUniqueID("Chat", false, false));

        newUserChat1.setChatID(newChat.getChatID());
        newUserChat2.setChatID(newChat.getChatID());

        //insert new chat entry into Chat
        insertChatHelper.insertAllExceptLastSentMessageID(newChat);

        //insert two new user-chat entries into UserChat
        insertUserChatHelper.insertAll(newUserChat1);
        insertUserChatHelper.insertAll(newUserChat2);

        return SuccessResponseGenerator.getSuccessResponseForCreateEntity();
    }
}