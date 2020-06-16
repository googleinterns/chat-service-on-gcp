package helper;

import dbaccessor.user.UserAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class UniqueIdGenerator {

    @Autowired
    UserAccessor queryUser;
    
    /* Generates long type unique ID value for the given table and its corres ID attribute */
    public long generateUniqueID(String tableName) {
        long id;
        while (true) {
            // Generates ID in range [1, Long.MAX_VALUE)
            id = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
            switch (tableName) {
                case "User": 
                    if (queryUser.checkIfUserIDExists(id)) {continue;} else {return id;}
            }
        }
    }
}
