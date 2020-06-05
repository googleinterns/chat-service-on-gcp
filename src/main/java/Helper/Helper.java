package Helper;

import DBAccesser.User.QueryUser;
import DBAccesser.Chat.QueryChat;
import DBAccesser.Message.QueryMessage;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Helper {

    @Autowired
    QueryUser queryUserHelper;

    @Autowired
    QueryChat queryChatHelper;

    @Autowired
    QueryMessage queryMessageHelper; 
    
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
                case "User": if (queryUserHelper.checkIfUserIDExists(ID)) {continue;} else {return ID;}
                case "Chat": if (queryChatHelper.checkIfChatIDExists(ID)) {continue;} else {return ID;}
                case "Message": if (queryMessageHelper.checkIfMessageIDExists(ID)) {continue;} else {return ID;}
            }
        }
    }
}