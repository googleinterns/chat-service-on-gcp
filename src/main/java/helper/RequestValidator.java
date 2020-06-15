package helper;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import exceptions.UserRequiredFieldMissingException;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

	private List<String> getMissingFields(Map<String, Object> data, String[] required) {
        List<String> missing = new ArrayList<String>();
        for(int i = 0; i < required.length; i++){
            if(!data.containsKey(required[i])){
                missing.add(required[i]);
            }
        }
        return missing;
    }

    public void signupRequestValidator(Map<String, Object> data, String path) {
        String[] required = {"Username", "EmailID", "Password", "MobileNo"};
        List<String> missing =  getMissingFields(data, required);
        if(missing.size() > 0) {
            throw new UserRequiredFieldMissingException(path, missing);
        }
    }

    public void loginRequestValidator(Map<String, Object> data, String path) {
        String[] required = {"Username", "Password"};
        List<String> missing =  getMissingFields(data, required);
        if(missing.size() > 0) {
            throw new UserRequiredFieldMissingException(path, missing);
        }
    }

}
