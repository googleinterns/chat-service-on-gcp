package DBAccesser.User;

import Entity.Class.User;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;

import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

import java.util.List;

@Component
public class InsertUser {
    
    @Autowired
    SpannerTemplate spannerTemplate;

    public void insertAll(User user) {
        spannerTemplate.insert(user);
    }
}