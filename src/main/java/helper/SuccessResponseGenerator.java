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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Generator which generates the successful HTTP response for all client requests to APIs.
 */
public final class SuccessResponseGenerator {

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the CreateUser, CreateChat and CreateMessage APIs.
     */
    public static ImmutableMap<String, Object> getSuccessResponseForCreateEntity(String className, long Id) {
        ImmutableMap<String, Object> responseBody = ImmutableMap.<String, Object> builder()
                                                                .put("message", "Success")
                                                                .put(className+"Id", Id)
                                                                .build();

        return responseBody;
    }
  
    public static ImmutableMap<String, Object> getSuccessResponseForLogin(long ID) {
        ImmutableMap<String, Object> responseBody = ImmutableMap.<String, Object> builder()
                                                                .put("message", "Success")
                                                                .put("UserId", ID)
                                                                .build();

        return responseBody;
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the GetUser API.
     */
    public static ImmutableMap<String, Map<String, Object>> getSuccessResponseForGetUser(User user) {
        ImmutableMap<String, Object> responseBody = ImmutableMap.<String, Object> builder()
                                                            .put("UserId", user.getUserId())
                                                            .put("Username", user.getUsername())
                                                            .put("CreationTs", user.getCreationTs())
                                                            .build();

        return ImmutableMap.of("payload", responseBody);
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the GetChat API.
     */
    public static ImmutableMap<String, Map<String, Object>> getSuccessResponseForGetChat(Chat chat) {
        ImmutableMap<String, Object> responseBody = ImmutableMap.<String, Object> builder()
                                                            .put("ChatId", chat.getChatId())
                                                            .put("LastSentMessageId", chat.getLastSentMessageId())
                                                            .put("CreationTs", chat.getCreationTs())
                                                            .build();
        return ImmutableMap.of("payload", responseBody);
    }

    private static Map<String, Object> addAttachmentMetadataToResponseBody(Map<String, Object> responseBody, Attachment attachment) {
        responseBody.put("FileName", attachment.getFileName());
        responseBody.put("FileType", attachment.getFileType());
        responseBody.put("FileSize", Long.toString(attachment.getFileSizeInBytes()) + " B");

        return responseBody;
    }

    private static Map<String, Object> getMessageForResponseBody(long userId, Message message) {
        Map<String, Object> messageForResponseBody = new LinkedHashMap<String, Object>();

        messageForResponseBody.put("MessageId", message.getMessageId());
        messageForResponseBody.put("ChatId", message.getChatId());
        messageForResponseBody.put("SentByCurrentUser", message.getSenderId() == userId);
        messageForResponseBody.put("SentTs", message.getSentTs());
        messageForResponseBody.put("ReceivedTs", message.getReceivedTs());
        messageForResponseBody.put("TextContent", message.getTextContent());

        return messageForResponseBody;
    }

    private static Map<String, Object> getMessageForResponseBody(long userId, Message message, Attachment attachment) {
        Map<String, Object> messageForResponseBody = getMessageForResponseBody(userId, message);

        return addAttachmentMetadataToResponseBody(messageForResponseBody, attachment);
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response 
     * for all client requests to the GetMessage API for messages without attachments.
     */
    public static ImmutableMap<String, Map<String, Object>> getSuccessResponseForGetMessage(Message message, long userId) {
        return ImmutableMap.of("payload", getMessageForResponseBody(userId, message));
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response 
     * for all client requests to the GetMessage API for messages with attachments.
     */
    public static ImmutableMap<String, Map<String, Object>> getSuccessResponseForGetMessage(Message message, Attachment attachment, long userId) {
        return ImmutableMap.of("payload", getMessageForResponseBody(userId, message, attachment));
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the ListChats API.
     */
    public static ImmutableMap<String,ImmutableList<ImmutableMap<String, Object>>> getSuccessResponseForListChats(ImmutableList<ImmutableMap<String, Object>> chatInfoOfChatsOfUser) {
        ImmutableMap<String, ImmutableList<ImmutableMap<String, Object>>> responseBody = ImmutableMap.<String, ImmutableList<ImmutableMap<String, Object>>> builder()
                                                                                                    .put("payload", chatInfoOfChatsOfUser)
                                                                                                    .build();

        return responseBody;
    }

    private static ImmutableMap<String, ImmutableList<Map<String, Object>>> getResponseBodyForListMessages(ImmutableList<Map<String, Object>> listOfMessages) {
        return ImmutableMap.<String, ImmutableList<Map<String, Object>>> builder()
                            .put("payload", listOfMessages)
                            .build();
    }

    private static ImmutableList<Message> sortMessagesByCreationTs(ImmutableList<Message> messages) {
        return messages
                .stream()
                .sorted(Comparator.comparing(Message::getCreationTs))
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the ListMessages API.
     */
    public static ImmutableMap<String, ImmutableList<Map<String, Object>>> getSuccessResponseForListMessages(long userId, ImmutableList<Message> messages) {

        ImmutableList.Builder<Map<String, Object>> listOfMessagesBuilder = ImmutableList.builder();
        
        ImmutableList<Message> copyOfMessages = sortMessagesByCreationTs(messages);
        
        for (Message message : copyOfMessages) {
            listOfMessagesBuilder.add(getMessageForResponseBody(userId, message));
        }

        ImmutableList<Map<String, Object>> listOfMessages = listOfMessagesBuilder.build();
        
        return getResponseBodyForListMessages(listOfMessages);
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the ListMessages API.
     */
    public static ImmutableMap<String, ImmutableList<Map<String, Object>>> getSuccessResponseForListMessages(long userId, ImmutableList<Message> messages, 
    ImmutableMap<Long, Attachment> attachmentIdToAttachment) {

        ImmutableList.Builder<Map<String, Object>> listOfMessagesBuilder = ImmutableList.builder();
        
        ImmutableList<Message> copyOfMessages = sortMessagesByCreationTs(messages);
        
        for (Message message : copyOfMessages) {
            if (message.getAttachmentId().isPresent()) {
                Attachment attachment = attachmentIdToAttachment.get(message.getAttachmentId().getAsLong());
                listOfMessagesBuilder.add(getMessageForResponseBody(userId, message, attachment));
            } else {
                listOfMessagesBuilder.add(getMessageForResponseBody(userId, message));
            }
        }

        ImmutableList<Map<String, Object>> listOfMessages = listOfMessagesBuilder.build();
        
        return getResponseBodyForListMessages(listOfMessages);
    }

    /**
     * Renders the given parameters in a Map to return a successful HTTP response for all client requests to the GetAttachment API.
     */
    public static ImmutableMap<String, Object> getSuccessResponseForGetAttachment(Attachment attachment, byte[] blob) {

        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();

        responseBody.put("Blob", blob);

        return ImmutableMap.<String, Object>builder() 
                            .putAll(addAttachmentMetadataToResponseBody(responseBody, attachment)) 
                            .build(); 
    }
}
