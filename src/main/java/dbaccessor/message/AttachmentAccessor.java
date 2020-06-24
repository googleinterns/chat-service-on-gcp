package dbaccessor.message;

import entity.Attachment;

import java.util.List;
import com.google.cloud.spanner.Statement.Builder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

/**
 * Accessor which performs database accesses for the Attachment entity.
 */
@Component
public final class AttachmentAccessor {

    @Autowired
    private SpannerTemplate spannerTemplate;

    /**
     * Checks if an Attachment with the given attachmentId exists.
     */
    public boolean checkIfAttachmentIdExists(long attachmentId) {

        String SQLStatment = "SELECT AttachmentID FROM Attachment WHERE AttachmentID=@attachmentId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("attachmentId").to(attachmentId).build();
        List<Attachment> resultSet = spannerTemplate.query(Attachment.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (!resultSet.isEmpty());
    }
}
