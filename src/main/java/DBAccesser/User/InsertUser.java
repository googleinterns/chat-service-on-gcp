package DBAccesser.User;

import Entity.User;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

@Component
public class InsertUser {
    
    @Autowired
    private SpannerTemplate spannerTemplate;

    public void insertAll(User user) {
        spannerTemplate.insert(user);
    } 
}