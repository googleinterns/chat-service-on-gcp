package DBAccesser.Message;

import Entity.Message;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

@Component
public class InsertMessage {
    
    @Autowired
    SpannerTemplate spannerTemplate;

    public void insertAllForTextMessage(Message message) {
        spannerTemplate.insert(message);
    } 
}