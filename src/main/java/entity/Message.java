package entity;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

@Table(name = "Message")
public final class Message {
    
    @Column(name = "CreationTS", spannerCommitTimestamp = true) 
    private Timestamp creationTs;

    @PrimaryKey
    @Column(name = "MessageID")
    private long messageId;

    @Column(name = "ChatID")
    private long chatId;

    @Column(name = "SenderID")
    private long senderId;

    @Column(name = "TextContent")
    private String textContent;

    @Column(name = "AttachmentID")
    private Long attachmentId;
    
    @Column(name = "SentTS", spannerCommitTimestamp = true) 
    private Timestamp sentTs;

    @Column(name = "ReceivedTS") 
    private Timestamp receivedTs;

    public Message() {}

    public Message(long messageId, long chatId, long senderId) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
    }

    public Message(long chatId, long senderId) {
        this.chatId = chatId;
        this.senderId = senderId;
    }

    public Message(long messageId, long chatId, long senderId, String textContent) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.textContent = textContent;
    }

    public Message(long chatId, long senderId, String textContent) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.textContent = textContent;
    }

    public Message(long messageId, long chatId, long senderId, Long attachmentId) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.attachmentId = attachmentId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public void setReceivedTs(Timestamp receivedTs) {
        this.receivedTs = receivedTs;
    }

    public Timestamp getCreationTs() {
        return creationTs;
    }

    public long getMessageId() {
        return messageId;
    }

    public long getChatId() {
        return chatId;
    }

    public long getSenderId() {
        return senderId;
    }

    public String getTextContent() {
        return textContent;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public Timestamp getSentTs() {
        return sentTs;
    }

    public Timestamp getReceivedTs() {
        return receivedTs;
    }
}
