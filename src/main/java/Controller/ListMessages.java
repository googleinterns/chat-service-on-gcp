package Controller;

import Entity.Message;
import DBAccesser.User.QueryUser;
import DBAccesser.Chat.QueryChat;
import DBAccesser.UserChat.QueryUserChat;
import DBAccesser.Message.QueryMessage;
import DBAccesser.Message.InsertMessage;
import Helper.SuccessResponseGenerator;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;
import Exceptions.UserChatIDDoesNotExistException;
import Exceptions.MessageIDDoesNotExistException;
import Exceptions.MessageIDDoesNotBelongToChatIDException;
import Exceptions.UserIDMissingFromRequestURLPathException;
import Exceptions.ChatIDMissingFromRequestURLPathException;

import java.util.Map;
import java.util.LinkedHashMap;
import com.google.cloud.Timestamp;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public final class ListMessages {

    @Autowired 
    private QueryUser queryUser;

    @Autowired 
    private QueryChat queryChat;

    @Autowired 
    private QueryMessage queryMessage;

    @Autowired 
    private QueryUserChat queryUserChat;

    @Autowired 
    private InsertMessage insertMessage;

    @GetMapping("/users/chats/messages")
    public void listMessagesWithoutUserIDChatIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestUri();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/chats/{chatID}/messages")
    public void listMessagesWithoutUserIDPathVariable(@PathVariable("chatID") String chatIDString, HttpServletRequest request) {

        String path = request.getRequestUri();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userID}/chats/messages")
    public void listMessagesWithoutChatIDPathVariable(@PathVariable("userID") String userIDString) {

        String path = request.getRequestUri();

        throw new ChatIDMissingFromRequestURLPathException(path);
    }
    
    @GetMapping("/users/{userID}/chats/{chatID}/messages")
    public Map<String, List<Map<String, Object>>> listMessages(@PathVariable("userID") String userIDString, @PathVariable("chatID") String chatIDString, @RequestParam(value = "startMessageID", required = false) String startMessageIDString, @RequestParam(value = "endMessageID", required = false) String endMessageIDString, @RequestParam(value = "count", required = false) String countString) {

        Timestamp receivedTS = Timestamp.now();
        String path = request.getRequestUri();
        Map<String, List<Map<String, Object>>> responseBody;

        int uppLimitOfMessageCountToReturn = 1000;
        int lowLimitOfMessageCountToReturn = 50;
        int count;

        Timestamp startCreationTS;
        Timestamp endCreationTS;

        List<Message> messages;
        
        //check if the passed userID is valid
        if (queryUser.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        }
        
        //check if the passed chatID is valid
        if (queryChat.checkIfChatIDExists(Long.parseLong(chatIDString)) == false) {
            throw new ChatIDDoesNotExistException(path);
        }

        //check if user is part of chat
        if (queryUserChat.checkIfUserChatIDExists(Long.parseLong(userIDString), Long.parseLong(chatIDString)) == false) {
            throw new UserChatIDDoesNotExistException(path);
        }

        //configure the value of count
        if (countString == null) {
            count = lowLimitOfMessageCountToReturn;
        } else {
            count = Math.abs(Integer.parseInt(countString));
            count = Math.min(count, uppLimitOfMessageCountToReturn);
            count = Math.max(count, lowLimitOfMessageCountToReturn);
        }

        if (startMessageIDString != null) {
            //check if startMessageID is valid
            if (queryMessage.checkIfMessageIDExists(Long.parseLong(startMessageIDString)) == false) {
                throw new MessageIDDoesNotExistException(path);
            } 

            //check if startMessageID is part of this chat
            if (queryMessage.checkIfMessageIDBelongsToChatID(Long.parseLong(startMessageIDString), Long.parseLong(chatIDString)) == false) {
                throw new MessageIDDoesNotBelongToChatIDException(path);
            } 

            //get CreationTS of startMessageID 
            startCreationTS = queryMessage.getCreationTSForMessageID(Long.parseLong(startMessageIDString));
        } else {
            startCreationTS = null;
        }

        if (endMessageIDString != null) {
            //check if endMessageID is valid
            if (queryMessage.checkIfMessageIDExists(Long.parseLong(endMessageIDString)) == false) {
                throw new MessageIDDoesNotExistException(path);
            } 

            //check if endMessageID is part of this chat
            if (queryMessage.checkIfMessageIDBelongsToChatID(Long.parseLong(endMessageIDString), Long.parseLong(chatIDString)) == false) {
                throw new MessageIDDoesNotBelongToChatIDException(path);
            }

            //get CreationTS of endCreationTS 
            endCreationTS = queryMessage.getCreationTSForMessageID(Long.parseLong(endMessageIDString));
        } else {
            endCreationTS = null;
        }

        if (startCreationTS != null && endCreationTS != null) {
            //check if startMessageID is after endMessageID
            if (startCreationTS.compareTo(endCreationTS) > 0) {
                Timestamp temp = startCreationTS;
                startCreationTS = endCreationTS;
                endCreationTS = temp;
            }
            //get messages within the required time frame
            messages = queryMessage.listCountMessagesOfChatIDWithinGivenTime(startCreationTS, endCreationTS, count, Long.parseLong(chatIDString));
        } else if (startCreationTS != null) {
            //get messages beginning at the start time
            messages = queryMessage.listCountMessagesOfChatIDFromStartTime(startCreationTS, count, Long.parseLong(chatIDString));
        } else if (endCreationTS != null) {
            //get messages before the end time
            messages = queryMessage.listCountMessagesOfChatIDBeforeEndTime(endCreationTS, count, Long.parseLong(chatIDString));
        } else {
            //get latest messages 
            messages = queryMessage.listLatestCountMessagesOfChatID(count, Long.parseLong(chatIDString));
        }

        //check ReceivedTS of each message not sent by current user- if it is null set it to the time when listMessages was called
        for (Message message : messages) {
            if (message.getReceivedTS() == null && message.getSenderID() != Long.parseLong(userIDString)) {
                message.setReceivedTS(receivedTS);
                insertMessage.insertReceivedTS(message);
            }
        }
        
        return SuccessResponseGenerator.getSuccessResponseForListMessages(messages);
    }
}