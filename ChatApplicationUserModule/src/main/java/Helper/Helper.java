package Helper;

import Exceptions.APIException;

import DBAccesser.User.QueryUser;
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
public class Helper {

    @Autowired
    QueryUser queryUserHelper;
    
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
                    if (queryUserHelper.checkIfUserIDExists(ID)) {continue;} else {return ID;}
            }
        }
    }

    public Map<String, Object> getResponseBody(APIException e) {
        Map<String, Object> response_body = new LinkedHashMap<>();
        response_body.put("timestamp", LocalDateTime.now());
        response_body.put("status", e.getHttpStatus().value());
        response_body.put("error", e.getHttpStatus().toString());
        response_body.put("message", e.getMessage());
        response_body.put("path", e.getPath());
        return response_body;
    }

    public List<String> missingFields(HashMap<String, Object> data, String[] required) {
        List<String> missing = new ArrayList<String>();
        for(int i = 0; i < required.length; i++){
            if(!data.containsKey(required[i])){
                missing.add(required[i]);
            }
        }
        return missing;
    }
}