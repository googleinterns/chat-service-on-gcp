package helper;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.google.common.collect.ImmutableList;
import exceptions.UserRequiredFieldMissingException;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

	private List<String> getMissingFields(Map<String, Object> data, String[] required) {
        List<String> missing = new ArrayList<String>();
        for (String s : required) {
            if (!data.containsKey(s)) {
                missing.add(s);
            }
        }
        return missing;
    }

    public void signupRequestValidator(Map<String, Object> data, String path) {
        String[] required = {"Username", "EmailID", "Password", "MobileNo"};
        ImmutableList<String> missing =  ImmutableList.copyOf(getMissingFields(data, required));
        if(missing.size() > 0) {
            throw new UserRequiredFieldMissingException(path, missing);
        }
    }

    public void loginRequestValidator(Map<String, Object> data, String path) {
        String[] required = {"Username", "Password"};
        ImmutableList<String> missing =  ImmutableList.copyOf(getMissingFields(data, required));
        if(missing.size() > 0) {
            throw new UserRequiredFieldMissingException(path, missing);
        }
    }

}
