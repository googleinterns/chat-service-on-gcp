package Helper;

import Entity.User;
import Entity.Chat;
import Entity.Message;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public final class SuccessResponseGenerator {

    public static Map<String, Object> getSuccessResponseForCreateEntity(String className, long ID) {

        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();

        responseBody.put("message", "Success");
        responseBody.put(className+"ID", ID);

        return responseBody;
    }

    public static Map<String, Object> getSuccessResponseForGetUser(User user) {

        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("UserID", user.getUserID());
        responseBody.put("Username", user.getUsername());
        responseBody.put("CreationTS", user.getCreationTS());

        return responseBody;
        
    }

    public static Map<String, Object> getSuccessResponseForGetChat(Chat chat) {

        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("ChatID", chat.getChatID());
        responseBody.put("LastSentMessageID", chat.getLastSentMessageID());
        responseBody.put("CreationTS", chat.getCreationTS());

        return responseBody;
    }

    public static Map<String, Object> getSuccessResponseForGetMessage(Message message) {

        Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("MessageID", message.getMessageID());
        responseBody.put("ChatID", message.getChatID());
        responseBody.put("SenderID", message.getSenderID());
        responseBody.put("ContentType", message.getContentType());
        responseBody.put("TextContent", message.getTextContent());
        responseBody.put("SentTS", message.getSentTS());
        responseBody.put("ReceivedTS", message.getReceivedTS());
        responseBody.put("CreationTS", message.getCreationTS());

        return responseBody;
    }

    public static Map<String,List<Map<String, Object>>> getSuccessResponseForListChats(List<Map<String, Object>> chatInfoOfChatsOfUser) {

        Map<String, List<Map<String, Object>>> responseBody = new LinkedHashMap<String, List<Map<String, Object>>>();

        responseBody.put("payload", chatInfoOfChatsOfUser);

        return responseBody;
    }

    private static Map<String, Object> getMessageForResponseBody(Message message) {
    
        Map<String, Object> messageForResponseBody = new LinkedHashMap<String, Object>();

        messageForResponseBody.put("MessageID", message.getMessageID());
        messageForResponseBody.put("CreationTS", message.getCreationTS());
        messageForResponseBody.put("ChatID", message.getChatID());
        messageForResponseBody.put("SenderID", message.getSenderID());
        messageForResponseBody.put("ContentType", message.getContentType());
        messageForResponseBody.put("TextContent", message.getTextContent());
        messageForResponseBody.put("SentTS", message.getSentTS());
        messageForResponseBody.put("ReceivedTS", message.getReceivedTS());

        return messageForResponseBody;
    }

    public static Map<String, List<Map<String, Object>>> getSuccessResponseForListMessages(List<Message> messages) {

        List<Map<String, Object>> listOfMessages = new ArrayList<Map<String, Object>>();

        //sort the messages in  ascending order of CreationTS
        Collections.sort(messages, Comparator.comparing(Message::getCreationTS));
        
        for (int i = 0; i < messages.size(); ++i) {
            listOfMessages.add(getMessageForResponseBody(messages.get(i)));
        }

        Map<String, List<Map<String, Object>>> responseBody = new LinkedHashMap<String, List<Map<String, Object>>>();
        responseBody.put("payload", listOfMessages);
        
        return responseBody;
    }
}
