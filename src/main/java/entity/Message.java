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

    @Column(name = "ContentType")
    private String contentType;

    @Column(name = "TextContent")
    private String textContent;
    
    @Column(name = "ContentID")
    private long contentId;

    @Column(name = "LinkToBlob")
    private String linkToBlob;

    @Column(name = "SentTS", spannerCommitTimestamp = true) 
    private Timestamp sentTs;

    @Column(name = "ReceivedTS") 
    private Timestamp receivedTs;

    public Message() {}

    public Message(long messageId, long chatId, long senderId, String contentType) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.contentType = contentType;
    }

    public Message(long chatId, long senderId, String contentType) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.contentType = contentType;
    }

    public Message(long messageId, long chatId, long senderId, String contentType, String textContent) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.contentType = contentType;
        this.textContent = textContent;
    }

    public Message(long chatId, long senderId, String contentType, String textContent) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.contentType = contentType;
        this.textContent = textContent;
    }

    public Message(long messageId, long chatId, long senderId, String contentType, long contentId, String linkToBlob) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.contentType = contentType;
        this.contentId = contentId;
        this.linkToBlob = linkToBlob;
    }

    public Message(long chatId, long senderId, String contentType, long contentId, String linkToBlob) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.contentType = contentType;
        this.contentId = contentId;
        this.linkToBlob = linkToBlob;
    }

    public void setMessageId (long messageId) {
        this.messageId = messageId;
    }

    public void setChatId (long chatId) {
        this.chatId = chatId;
    }

    public void setSenderId (long senderId) {
        this.senderId = senderId;
    }

    public void setContentType (String contentType) {
        this.contentType = contentType;
    }

    public void setTextContent (String textContent) {
        this.textContent = textContent;
    }

    public void setContentId (long contentId) {
        this.contentId = contentId;
    }

    public void setLinkToBlob (String linkToBlob) {
        this.linkToBlob = linkToBlob;
    }

    public void setReceivedTs (Timestamp receivedTs) {
        this.receivedTs = receivedTs;
    }

    public Timestamp getCreationTs () {
        return creationTs;
    }

    public long getMessageId () {
        return messageId;
    }

    public long getChatId () {
        return chatId;
    }

    public long getSenderId () {
        return senderId;
    }

    public String getContentType () {
        return contentType;
    }

    public String getTextContent () {
        return textContent;
    }

    public long getContentId () {
        return contentId;
    }

    public String getLinkToBlob () {
        return linkToBlob;
    }

    public Timestamp getSentTs () {
        return sentTs;
    }

    public Timestamp getReceivedTs () {
        return receivedTs;
    }
}
