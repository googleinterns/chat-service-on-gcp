package DBAccesser.UserChat;

import Entity.Class.UserChat;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

@Component
public class InsertUserChat {
    
    @Autowired
    SpannerTemplate spannerTemplate;

    public void insertAll(UserChat userChat) {
        spannerTemplate.insert(userChat);
    }
}