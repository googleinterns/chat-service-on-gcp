package DBAccesser.Chat;

import Entity.Chat;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

@Component
public class InsertChat {
    
    @Autowired
    private SpannerTemplate spannerTemplate;

    public void insertAllExceptLastSentMessageID(Chat chat) {
        //must mention to add CreationTS even if it is set to be automatically committed
        spannerTemplate.upsert(chat, "ChatID", "CreationTS");
    }

    public void insertLastSentMessageID(Chat chat) {
        //must insert PK even in partial update
        spannerTemplate.update(chat, "ChatID", "LastSentMessageID");
    }
}