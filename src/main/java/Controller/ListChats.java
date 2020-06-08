package Controller;

import Entity.User;
import Entity.Chat;
import Helper.Helper;
import DBAccesser.User.QueryUser;
import DBAccesser.Chat.QueryChat;
import DBAccesser.Message.QueryMessage;
import Helper.SuccessResponseGenerator;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;
import Exceptions.UserIDMissingFromRequestURLPathException;

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
public final class ListChats {
    
    @Autowired 
    private QueryUser queryUser;

    @Autowired 
    private QueryChat queryChat;

    @Autowired
    private QueryMessage queryMessage;

    @Autowired
    private Helper helper;

    @GetMapping("/users/chats")
    public void listChatsWithoutUserIDPathVariable() {

        String path = "/users/chats";

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userID}/chats")
    public Map<String, List<String>> listChats(@PathVariable("userID") String userIDString) {
        
        String path = "/users/" + userIDString + "/chats";
        Map<String, List<String>> responseBody;
        
        //check if the passed userID is valid
        if (queryUser.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        }
        
        User user = new User(Long.parseLong(userIDString));
        List<Chat> chatsOfUser = queryChat.getChatsForUser(user);
        
        for (Chat chat : chatsOfUser) {
            if (chat.getLastSentMessageID() == 0) { 
                chat.setLastSentTime(chat.getCreationTS());
            } else {
                chat.setLastSentTime(queryMessage.getCreationTSForMessageID(chat.getLastSentMessageID()));
            }
        }
        
        Collections.sort(chatsOfUser, Chat.LastSentTimeDescComparator);

        List<String> usernamesOfChatsOfUser = new ArrayList<String>();
        
        for (Chat chat : chatsOfUser) {

            usernamesOfChatsOfUser.add(queryUser.getSecondUserForChat(user, chat).get(0).getUsername());
        }

        return SuccessResponseGenerator.getSuccessResponseForListChats(usernamesOfChatsOfUser);
    }
}