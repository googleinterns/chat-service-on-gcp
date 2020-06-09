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

@Component
public class UniqueIDGenerator {

    @Autowired
    UserAccessor queryUser;
    
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
                case "User": 
                    if (queryUser.checkIfUserIDExists(ID)) {continue;} else {return ID;}
            }
        }
    }
}
