package helper;

import entity.User;
import entity.Chat;
import entity.Message;
import entity.Attachment;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * Generator which generates the successful HTTP response for all client requests to APIs.
 */
public final class SuccessResponseGenerator {

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the CreateUser, CreateChat and CreateMessage APIs.
     */
    public static Map<String, Object> getSuccessResponseForCreateEntity(String className, long Id) {

        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();

        responseBody.put("message", "Success");
        responseBody.put(className+"Id", Id);

        return responseBody;
    }
  
    public static Map<String, Object> getSuccessResponseForLogin(long ID) {
        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("message", "Success");
        responseBody.put("UserId", ID);
        return responseBody;
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the GetUser API.
     */
    public static Map<String, Object> getSuccessResponseForGetUser(User user) {

        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("UserId", user.getUserId());
        responseBody.put("Username", user.getUsername());
        responseBody.put("CreationTs", user.getCreationTs());

        return responseBody;
        
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the GetChat API.
     */
    public static Map<String, Object> getSuccessResponseForGetChat(Chat chat) {

        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("ChatId", chat.getChatId());
        responseBody.put("LastSentMessageId", chat.getLastSentMessageId());
        responseBody.put("CreationTs", chat.getCreationTs());

        return responseBody;
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response 
     * for all client requests to the GetMessage API for messages without attachments.
     */
    public static Map<String, Object> getSuccessResponseForGetMessage(Message message, long userId) {

        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("MessageId", message.getMessageId());
        responseBody.put("ChatId", message.getChatId());
        responseBody.put("SentByCurrentUser", message.getSenderId() == userId);
        responseBody.put("SentTs", message.getSentTs());
        responseBody.put("ReceivedTs", message.getReceivedTs());
        responseBody.put("CreationTs", message.getCreationTs());
        responseBody.put("TextContent", message.getTextContent());

        return responseBody;
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response 
     * for all client requests to the GetMessage API for messages with attachments.
     */
    public static Map<String, Object> getSuccessResponseForGetMessage(Message message, Attachment attachment, long userId) {

        Map<String, Object> responseBody = getSuccessResponseForGetMessage(Message message, long userId);
        responseBody.put("FileName", attachment.getFileName());
        responseBody.put("FileType", attachment.getFileType());
        responseBody.put("FileSize", Long.toString(attachment.getFileSize()) + " B");

        return responseBody;
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the ListChats API.
     */
    public static Map<String,List<Map<String, Object>>> getSuccessResponseForListChats(List<Map<String, Object>> chatInfoOfChatsOfUser) {

        Map<String, List<Map<String, Object>>> responseBody = new LinkedHashMap<String, List<Map<String, Object>>>();

        responseBody.put("payload", chatInfoOfChatsOfUser);

        return responseBody;
    }

    private static Map<String, Object> getMessageForResponseBody(long userId, Message message) {
    
        Map<String, Object> messageForResponseBody = new LinkedHashMap<String, Object>();

        messageForResponseBody.put("MessageId", message.getMessageId());
        messageForResponseBody.put("CreationTs", message.getCreationTs());
        messageForResponseBody.put("ChatId", message.getChatId());
        messageForResponseBody.put("SentByCurrentUser", message.getSenderId() == userId);
        messageForResponseBody.put("TextContent", message.getTextContent());
        messageForResponseBody.put("SentTs", message.getSentTs());
        messageForResponseBody.put("ReceivedTs", message.getReceivedTs());

        return messageForResponseBody;
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the ListMessages API.
     */
    public static Map<String, List<Map<String, Object>>> getSuccessResponseForListMessages(long userId, List<Message> messages) {

        List<Map<String, Object>> listOfMessages = new ArrayList<Map<String, Object>>();
        
        //Sorts the messages in  ascending order of Creation Timestamp
        Collections.sort(messages, Comparator.comparing(Message::getCreationTs));
        
        for (int i = 0; i < messages.size(); ++i) {
            listOfMessages.add(getMessageForResponseBody(userId, messages.get(i)));
        }

        Map<String, List<Map<String, Object>>> responseBody = new LinkedHashMap<String, List<Map<String, Object>>>();
        responseBody.put("payload", listOfMessages);
       
        return responseBody;
    }
}
