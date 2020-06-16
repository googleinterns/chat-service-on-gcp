package helper;

import dbaccessor.user.UserAccessor;
import dbaccessor.chat.ChatAccessor;
import dbaccessor.message.MessageAccessor;

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
    
    /**
     * Generates long type unique Id value for the given table and its corres Id attribute
     */
    public long generateUniqueId(String tableName) {

        long id;

        while (true) {
            id = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);

            switch (tableName) {
                case "User": 
                    if (!queryUser.checkIfUserIdExists(id)) {
                        return id;
                    } 
                    break;

                case "Chat": 
                    if (!queryChat.checkIfChatIdExists(id)) {
                        return id;
                    } 
                    break;

                case "Message": 
                    if (!queryMessage.checkIfMessageIdExists(id)) {
                        return id;
                    }
                    break;
                
                default: 
                    throw new IllegalArgumentException("Invalid DB Relation Name passed to UniqueIdGenerator");
            }
        }
    }
}
