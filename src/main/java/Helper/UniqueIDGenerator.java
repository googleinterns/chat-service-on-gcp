package Helper;

import DBAccesser.User.UserAccessor;
import DBAccesser.Chat.ChatAccessor;
import DBAccesser.Message.MessageAccessor;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueIDGenerator {

    @Autowired
    private UserAccessor queryUser;

    @Autowired
    private ChatAccessor queryChat;

    @Autowired
    private MessageAccessor queryMessage; 
    
    //generates long type unique ID value for the given table and its corres ID attribute
    public long generateUniqueID(String tableName, boolean zeroAllowed, boolean negativeAllowed) {

        long ID;
        Random random = new Random();

        while (true) {
            ID = random.nextLong();

            if (zeroAllowed == false && ID == 0) {
                continue;
            }

            if (negativeAllowed == false && ID < 0) {
                continue;
            }

            switch (tableName) {
                case "User": if (queryUser.checkIfUserIDExists(ID)) {continue;} else {return ID;}
                case "Chat": if (queryChat.checkIfChatIDExists(ID)) {continue;} else {return ID;}
                case "Message": if (queryMessage.checkIfMessageIDExists(ID)) {continue;} else {return ID;}
            }
        }
    }
}