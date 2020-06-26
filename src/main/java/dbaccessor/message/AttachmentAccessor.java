package dbaccessor.message;

import entity.Attachment;
import main.ApplicationVariable;

import java.util.List;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
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
        String sqlStatment = "SELECT AttachmentID FROM Attachment WHERE AttachmentID=@attachmentId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("attachmentId").to(attachmentId).build();
        List<Attachment> resultSet = spannerTemplate.query(Attachment.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return !resultSet.isEmpty();
    }

    /**
     * Returns details of Attachment with the given AttachmentId.
     * Details include:
     * <ol>
     * <li> AttachmentId </li>
     * <li> FileName </li>
     * <li> FileType </li>
     * <li> FileSize </li>
     * </ol>
     */
    public Attachment getAttachment(long attachmentId) {
        String sqlStatment = "SELECT * FROM Attachment WHERE AttachmentID=@attachmentId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("attachmentId").to(attachmentId).build();
        List<Attachment> resultSet = spannerTemplate.query(Attachment.class, statement, null);
 
        return resultSet.get(0);
    }

    /**
     * Returns details of Attachments for the given list of AttachmentIds.
     * Details include:
     * <ol>
     * <li> AttachmentId </li>
     * <li> FileName </li>
     * <li> FileType </li>
     * <li> FileSize </li>
     * </ol>
     */
    public List<Attachment> getAttachments(List<Long> attachmentIdList) {
        String sqlStatment = "SELECT * FROM Attachment WHERE AttachmentID IN UNNEST (@attachmentIdList)";
        Statement statement = Statement.newBuilder(sqlStatment).bind("attachmentIdList").toInt64Array(attachmentIdList).build();
        List<Attachment> resultSet = spannerTemplate.query(Attachment.class, statement, null);
 
        return resultSet;
    }

    /**
     * Returns Blob of Attachment for the given AttachmentId.
     */
    public byte[] getBlob(long attachmentId) {
        Storage storage = StorageOptions.newBuilder().setProjectId(ApplicationVariable.GCP_PROJECT).build().getService();
        Blob blob = storage.get(BlobId.of(ApplicationVariable.GCS_BUCKET, Long.toString(attachmentId)));

        return blob.getContent();
    }
}
