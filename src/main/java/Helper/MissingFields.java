package Helper;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class MissingFields {
	public List<String> getMissingFields(Map<String, Object> data, String[] required) {
        List<String> missing = new ArrayList<String>();
        for(int i = 0; i < required.length; i++){
            if(!data.containsKey(required[i])){
                missing.add(required[i]);
            }
        }
        return missing;
    }
}
