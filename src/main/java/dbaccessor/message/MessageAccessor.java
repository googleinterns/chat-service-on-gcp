package dbaccessor.message;

import entity.Chat;
import entity.Message;
import entity.Attachment;
import main.ApplicationVariable;

import java.util.List;
import java.util.Optional;
import java.io.IOException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.ImmutableList;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Statement.Builder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;
import org.springframework.cloud.gcp.data.spanner.core.SpannerOperations;

/**
 * Accessor which performs database accesses for the Message entity.
 */
@Component
public final class MessageAccessor {
    
    @Autowired
    SpannerOperations spannerOperations;

    @Autowired
    private SpannerTemplate spannerTemplate;

    /**
     * Inserts all attributes of a Message in the DB.
     */
    public void insertAllForTextMessage(Message message) {
        spannerTemplate.insert(message);
    } 

    /**
     * Updates the Received Timestamp of a Message.
     */
    public void updateReceivedTs(Message message) {
        spannerTemplate.update(message, "MessageID", "ReceivedTS");
    }

    /**
     * Completes all DB insertions for the CreateMessage API for a Message without Attachment in a single transaction.
     * Returns true in case of Success.
     */
    public boolean createMessageInTransaction(Message message, Chat chat) {
        return spannerOperations.performReadWriteTransaction(
            transactionSpannerOperations -> {
                transactionSpannerOperations.insert(message);
                transactionSpannerOperations.update(chat, "ChatID", "LastSentMessageID");

                return true;
            }
        );
    }

    /**
     * Completes all DB insertions for the CreateMessage API for a Message with Attachment in a single transaction.
     * Returns true in case of Success and false if an IO Exception occurs
     */
    public boolean createMessageInTransaction(Message message, Chat chat, Attachment attachment) {
        return spannerOperations.performReadWriteTransaction(
            transactionSpannerOperations -> {
                Storage storage = StorageOptions.newBuilder().setProjectId(ApplicationVariable.GCP_PROJECT).build().getService();
                BlobId blobId = BlobId.of(ApplicationVariable.GCS_BUCKET, Long.toString(attachment.getAttachmentId()));

                try {
                    storage.create(BlobInfo.newBuilder(blobId).build(), attachment.getFile().getBytes());
                } catch (IOException e) {
                    return false;    
                }
                
                transactionSpannerOperations.insert(attachment);
                transactionSpannerOperations.insert(message);
                transactionSpannerOperations.update(chat, "ChatID", "LastSentMessageID");

                return true;
            }
        );
    }

    /**
     * Completes all DB insertions for the ListMessages API in a single transaction.
     */
    public boolean updateReceivedTsForMessages(List<Message> messageList) {
        return spannerOperations.performReadWriteTransaction(
            transactionSpannerOperations -> {

                for (Message message : messageList) {
                    transactionSpannerOperations.update(message, "MessageID", "ReceivedTS");
                }

                return true;
            }
        );
    }

    /**
     * Checks if a Message with the given messageId exists.
     */
    public boolean checkIfMessageIdExists(long messageId) {

        String sqlStatment = "SELECT MessageID FROM Message WHERE MessageID=@messageId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("messageId").to(messageId).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return !resultSet.isEmpty();
    }

    /**
     * Returns details of Message with the given MessageId.
     * Details include:
     * <ol>
     * <li> MessageId </li>
     * <li> ChatId </li>
     * <li> SenderId </li>
     * <li> TextContent </li>
     * <li> Sent Timestamp </li>
     * <li> Received Timestamp </li>
     * <li> Creation Timestamp </li>
     * </ol>
     */
    public Message getMessage(long messageId) {

        String sqlStatment = "SELECT * FROM Message WHERE MessageID=@messageId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("messageId").to(messageId).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement, null);
 
        return resultSet.get(0);
    }

    /**
     * Returns the Creation Timestamp field of the Message with the given messageId.
     */
    public Timestamp getCreationTsForMessageId(long messageId) {

        String sqlStatment = "SELECT CreationTS FROM Message WHERE MessageID=@messageId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("messageId").to(messageId).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet.get(0).getCreationTs();
    }

    /**
     * Checks if the given Message belongs to the given Chat.
     */
    public boolean checkIfMessageIdBelongsToChatId(long messageId, long chatId) {
        
        String sqlStatment = "SELECT MessageID FROM Message WHERE MessageID=@messageId and ChatID=@chatId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("messageId").to(messageId).bind("chatId").to(chatId).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return !resultSet.isEmpty(); 
    }

    private List<Message> listCountMessagesOfChatId(Timestamp startCreationTs, Timestamp endCreationTs, int count, long chatId) {

        String sqlStatment = "SELECT * FROM Message WHERE ChatID = @chatId";

        if (startCreationTs != null) {
            sqlStatment += " AND CreationTS >= @startCreationTs";
        }

        if (endCreationTs != null) {
            sqlStatment += " AND CreationTS <= @endCreationTs";
        }
        
        sqlStatment += " ORDER BY CreationTS DESC LIMIT @count";

        Builder builder = Statement.newBuilder(sqlStatment).bind("chatId").to(chatId);

        if (startCreationTs != null) {
            builder.bind("startCreationTs").to(startCreationTs);
        }

        if (endCreationTs != null) {
            builder.bind("endCreationTs").to(endCreationTs);
        }

        Statement statement = builder.bind("count").to(count).build();

        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  null);
 
        return resultSet;
    }

    /**
     * Returns details of <b>count</b> number of Messages of the given Chat within the required time frame.
     */
    public List<Message> listCountMessagesOfChatIdWithinGivenTime(Timestamp startCreationTs, Timestamp endCreationTs, int count, long chatId) {

        return listCountMessagesOfChatId(startCreationTs, endCreationTs, count, chatId);
    }

    /**
     * Returns details of <b>count</b> number of Messages of the given Chat beginning at the start time.
     */
    public List<Message> listCountMessagesOfChatIdFromStartTime(Timestamp startCreationTs, int count, long chatId) {

        return listCountMessagesOfChatId(startCreationTs, null, count, chatId); 
    }

    /**
     * Returns details of <b>count</b> number of Messages of the given Chat before the end time.
     */
    public List<Message> listCountMessagesOfChatIdBeforeEndTime(Timestamp endCreationTs, int count, long chatId) {

        return listCountMessagesOfChatId(null, endCreationTs, count, chatId);
    }

    /**
     * Returns details of <b>count</b> number of latest Messages.
     */
    public List<Message> listLatestCountMessagesOfChatId(int count, long chatId) {

        return listCountMessagesOfChatId(null, null, count, chatId);
    }

    /**
     * Returns details of Messages which were sent last in the Chats which the given User is engaged in.
     * Details include:
     * <ol>
     * <li> ChatId </li>
     * <li> Creation Timestamp (of the Message) </li>
     * </ol>
     */
    public List<Message> getCreationTsOfLastSentMessageIdForChatsOfUser(long userId) {

        String sqlStatment = "SELECT ChatID, CreationTS FROM Message WHERE MessageID IN (SELECT LastSentMessageID FROM Chat WHERE ChatID IN (SELECT ChatID FROM UserChat WHERE UserID = @userId) AND LastSentMessageID IS NOT NULL)";
        Statement statement = Statement.newBuilder(sqlStatment).bind("userId").to(userId).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet;
    }

    /**
     * Returns details of Attachment corresponding to the Message with the given MessageId.
     * Details include:
     * <ol>
     * <li> AttachmentId </li>
     * <li> FileName </li>
     * <li> FileType </li>
     * <li> FileSize </li>
     * </ol>
     */
    public Attachment getAttachmentFromMessageId(long messageId) {
        String sqlStatment = "SELECT Attachment.AttachmentID as AttachmentID, Attachment.FileName as FileName, Attachment.FileType as FileType, Attachment.FileSize as FileSize FROM Message LEFT OUTER JOIN Attachment ON Message.AttachmentID = Attachment.AttachmentID WHERE Message.MessageID = @messageId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("messageId").to(messageId).build();
        List<Attachment> resultSet = spannerTemplate.query(Attachment.class, statement, new SpannerQueryOptions().setAllowPartialRead(true));
        
        return resultSet.get(0);
    }
}
