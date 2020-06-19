package helper;

import com.google.common.collect.ImmutableList;
import exceptions.UserRequiredFieldMissingException;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;

@Component
public class RequestValidator {

	private ImmutableList<String> getMissingFields(Set<String> data, ImmutableList<String> required) {
        return required.stream()
                .filter(s -> !data.contains(s))
                .collect(toImmutableList());
    }

    public void signupRequestValidator(Set<String> data, String path) {
        ImmutableList<String> required = ImmutableList.of("Username", "EmailID", "Password", "MobileNo");
        ImmutableList<String> missing =  getMissingFields(data, required);
        if(missing.size() > 0) {
            throw new UserRequiredFieldMissingException(path, missing);
        }
    }

    public void loginRequestValidator(Set<String> data, String path) {
        ImmutableList<String> required = ImmutableList.of("Username", "Password");
        ImmutableList<String> missing =  getMissingFields(data, required);
        if(missing.size() > 0) {
            throw new UserRequiredFieldMissingException(path, missing);
        }
    }

}
