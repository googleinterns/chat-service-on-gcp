package Helper;

import DBAccesser.User.UserAccessor;
import DBAccesser.Chat.ChatAccessor;
import DBAccesser.Message.MessageAccessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;

@Component
public final class UniqueIdGenerator {

    @Autowired
    private UserAccessor queryUser;

    @Autowired
    private ChatAccessor queryChat;

    @Autowired
    private MessageAccessor queryMessage; 
    
    //generates long type unique Id value for the given table and its corres Id attribute
    public long generateUniqueId(String tableName) {

        long id;

        while (true) {
            id = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);

            switch (tableName) {
                case "User": 
                    if (queryUser.checkIfUserIdExists(id)) {
                        continue;
                    } else {
                        return id;
                    }
                case "Chat": 
                    if (queryChat.checkIfChatIdExists(id)) {
                        continue;
                    } else {
                        return id;
                    }
                case "Message": 
                    if (queryMessage.checkIfMessageIdExists(id)) {
                        continue;
                    } else {
                            return id;
                    }
                default: 
                    throw new IllegalArgumentException("Invalid DB Relation Name passed to UniqueIdGenerator");
            }
        }
    }
}
