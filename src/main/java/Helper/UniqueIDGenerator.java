package Helper;

import DBAccesser.User.UserAccessor;
import DBAccesser.Chat.ChatAccessor;
import DBAccesser.Message.MessageAccessor;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class UniqueIDGenerator {

    @Autowired
    private UserAccessor queryUser;

    @Autowired
    private ChatAccessor queryChat;

    @Autowired
    private MessageAccessor queryMessage; 
    
    //generates long type unique ID value for the given table and its corres ID attribute
    public long generateUniqueID(String tableName, boolean zeroAllowed, boolean negativeAllowed) {

        long id;
        Random random = new Random();

        while (true) {
            id = random.nextLong();

            if (zeroAllowed == false && id == 0) {
                continue;
            }

            if (negativeAllowed == false && id < 0) {
                continue;
            }

            switch (tableName) {
                case "User": 
                    if (queryUser.checkIfUserIDExists(id)) {
                        continue;
                    } else {
                        return id;
                    }
                case "Chat": 
                    if (queryChat.checkIfChatIDExists(id)) {
                        continue;
                    } else {
                        return id;
                    }
                case "Message": 
                    if (queryMessage.checkIfMessageIDExists(id)) {
                        continue;
                    } else {
                            return id;
                    }
                default: 
                    throw new IllegalArgumentException("Invalid DB Relation Name passed to UniqueIDGenerator");
            }
        }
    }
}
