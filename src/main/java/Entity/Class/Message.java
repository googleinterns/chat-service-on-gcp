package Entity.Class;

import java.sql.Timestamp;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

@Table(name = "Message")
public class Message {
    
    @Column(name = "CreationTS", spannerCommitTimestamp = true) 
    Timestamp creationTS;

    @PrimaryKey
    @Column(name = "MessageID")
    long messageID;

    @Column(name = "ChatID")
    long chatID;

    @Column(name = "SenderID")
    long senderID;

    @Column(name = "ContentType")
    String contentType;

    @Column(name = "TextContent")
    String textContent;
    
    @Column(name = "ContentID")
    long contentID;

    @Column(name = "LinkToBlob")
    String linkToBlob;

    @Column(name = "SentTS", spannerCommitTimestamp = true) 
    Timestamp sentTS;

    @Column(name = "ReceivedTS") 
    Timestamp receivedTS;

    public Message() {

    }

    public Message(long messageID, long chatID, long senderID, String contentType) {
        this.messageID = messageID;
        this.chatID = chatID;
        this.senderID = senderID;
        this.contentType = contentType;
    }

    public Message(long chatID, long senderID, String contentType) {
        this.chatID = chatID;
        this.senderID = senderID;
        this.contentType = contentType;
    }

    public Message(long messageID, long chatID, long senderID, String contentType, String textContent) {
        this.messageID = messageID;
        this.chatID = chatID;
        this.senderID = senderID;
        this.contentType = contentType;
        this.textContent = textContent;
    }

    public Message(long chatID, long senderID, String contentType, String textContent) {
        this.chatID = chatID;
        this.senderID = senderID;
        this.contentType = contentType;
        this.textContent = textContent;
    }

    public Message(long messageID, long chatID, long senderID, String contentType, long contentID, String linkToBlob) {
        this.messageID = messageID;
        this.chatID = chatID;
        this.senderID = senderID;
        this.contentType = contentType;
        this.contentID = contentID;
        this.linkToBlob = linkToBlob;
    }

    public Message(long chatID, long senderID, String contentType, long contentID, String linkToBlob) {
        this.chatID = chatID;
        this.senderID = senderID;
        this.contentType = contentType;
        this.contentID = contentID;
        this.linkToBlob = linkToBlob;
    }

    public void setMessageID (long messageID) {
        this.messageID = messageID;
    }

    public void setChatID (long chatID) {
        this.chatID = chatID;
    }

    public void setSenderID (long senderID) {
        this.senderID = senderID;
    }

    public void setContentType (String contentType) {
        this.contentType = contentType;
    }

    public void setTextContent (String textContent) {
        this.textContent = textContent;
    }

    public void setContentID (long contentID) {
        this.contentID = contentID;
    }

    public void setLinkToBlob (String linkToBlob) {
        this.linkToBlob = linkToBlob;
    }

    public void setReceivedTS (Timestamp receivedTS) {
        this.receivedTS = receivedTS;
    }

    public Timestamp getCreationTS () {
        return creationTS;
    }

    public long getMessageID () {
        return messageID;
    }

    public long getChatID () {
        return chatID;
    }

    public long getSenderID () {
        return senderID;
    }

    public String getContentType () {
        return contentType;
    }

    public String getTextContent () {
        return textContent;
    }

    public long getContentID () {
        return contentID;
    }

    public String getLinkToBlob () {
        return linkToBlob;
    }

    public Timestamp getSentTS () {
        return sentTS;
    }

    public Timestamp getReceivedTS () {
        return receivedTS;
    }
}