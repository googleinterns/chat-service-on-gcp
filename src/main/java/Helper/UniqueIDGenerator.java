package Helper;

import Exceptions.APIException;

import DBAccesser.User.UserAccessor;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class UniqueIDGenerator {

    @Autowired
    UserAccessor queryUser;
    
    // Generates long type unique ID value for the given table and its corres ID attribute
    public long generateUniqueID(String tableName) {
        long ID;
        while (true) {
            // Generates ID in range [1, Long.MAX_VALUE)
            ID = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
            switch (tableName) {
                case "User": 
                    if (queryUser.checkIfUserIDExists(ID)) {continue;} else {return ID;}
            }
        }
    }
}
