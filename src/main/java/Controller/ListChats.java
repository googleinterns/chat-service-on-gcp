package Controller;

import Entity.User;
import Entity.Chat;
import DBAccesser.User.QueryUser;
import DBAccesser.Chat.QueryChat;
import DBAccesser.Message.QueryMessage;
import Helper.Helper;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class ListChats {
    
    @Autowired 
    QueryUser queryUserHelper;

    @Autowired 
    QueryChat queryChatHelper;

    @Autowired
    QueryMessage queryMessageHelper;

    @Autowired
    Helper helper;

    @GetMapping("/users/{userID}/chats")
    public Map<String, List<String>> listChats(@PathVariable("userID") String userIDString) {
        
        String path = "/users/" + userIDString + "/chats";
        Map<String, List<String>> responseBody;
        
        //check if the passed userID is valid
        if (queryUserHelper.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        }
        
        User user = new User(Long.parseLong(userIDString));
        List<Chat> chatsOfUser = queryChatHelper.getChatsForUser(user);
        
        for (Chat chat : chatsOfUser) {
            System.out.println(chat.getLastSentMessageID());
            if (chat.getLastSentMessageID() == 0) { //confirm what is stored in the JAVA equivalent of a null containing column
                chat.setLastSentTime(chat.getCreationTS());
            } else {
                chat.setLastSentTime(queryMessageHelper.getCreationTSForMessageID(chat.getLastSentMessageID()));
            }
        }

        Collections.sort(chatsOfUser, Chat.LastSentTimeDescComparator);

        List<String> usernamesOfChatsOfUser = new ArrayList<String>();
        
        for (Chat chat : chatsOfUser) {

            usernamesOfChatsOfUser.add(queryUserHelper.getSecondUserForChat(user, chat).get(0).getUsername());
        }

        responseBody = new LinkedHashMap<String,List<String>>();
        responseBody.put("payload", usernamesOfChatsOfUser);

        return responseBody;
        
        /*
        //test without Spanner 
        responseBody = new LinkedHashMap<String,List<String>>();
        responseBody.put("payload", new ArrayList<String>(){{add(userIDString);}});

        return responseBody;
        */
    }
}