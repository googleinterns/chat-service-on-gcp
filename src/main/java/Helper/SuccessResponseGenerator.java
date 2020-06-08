package Helper;

import Entity.User;
import Entity.Chat;
import Entity.Message;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

public class SuccessResponseGenerator {

    public static Map<String, String> getSuccessResponseForCreateEntity() {

        Map<String, String> responseBody = new LinkedHashMap<String, String>();

        responseBody.put("message", "Created");

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

    public static Map<String,List<String>> getSuccessResponseForListChats(List<String> usernamesOfChatsOfUser) {

        Map<String,List<String>> responseBody = new LinkedHashMap<String,List<String>>();

        responseBody.put("payload", usernamesOfChatsOfUser);

        return responseBody;
    }

    public static Map<String, List<Map<String, Object>>> getSuccessResponseForListMessages(List<Message> messages) {

        //return messages in ascending order of CreationTS
        List<Map<String, Object>> listOfMessages = new ArrayList<Map<String, Object>>();

        for (int i = messages.size()-1; i >= 0; --i) {
            Message message = messages.get(i);
            Map<String, Object> nextMessage = new LinkedHashMap<String, Object>();

            nextMessage.put("MessageID", message.getMessageID());
            nextMessage.put("CreationTS", message.getCreationTS());
            nextMessage.put("ChatID", message.getChatID());
            nextMessage.put("SenderID", message.getSenderID());
            nextMessage.put("ContentType", message.getContentType());
            nextMessage.put("TextContent", message.getTextContent());
            nextMessage.put("SentTS", message.getSentTS());
            nextMessage.put("ReceivedTS", message.getReceivedTS());

            listOfMessages.add(nextMessage);
        }

        Map<String, List<Map<String, Object>>> responseBody = new LinkedHashMap<String, List<Map<String, Object>>>();
        responseBody.put("payload", listOfMessages);
        
        return responseBody;
    }
}