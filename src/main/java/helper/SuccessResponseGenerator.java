package helper;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

public class SuccessResponseGenerator {

    public static Map<String, Object> getSuccessResponseForCreateEntity(String className, long ID) {
        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("message", "Success");
        responseBody.put(className+"ID", ID);
        return responseBody;
    }

    public static Map<String, Object> getSuccessResponseForLogin(long ID) {
        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("message", "Success");
        responseBody.put("UserID", ID);
        return responseBody;
    }
}
