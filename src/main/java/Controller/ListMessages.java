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

        String path = request.getRequestURI();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/chats/{chatID}/messages")
    public void listMessagesWithoutUserIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userID}/chats/messages")
    public void listMessagesWithoutChatIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new ChatIDMissingFromRequestURLPathException(path);
    }
    
    @GetMapping("/users/{userID}/chats/{chatID}/messages")
    public Map<String, List<Map<String, Object>>> listMessages(@PathVariable("userID") String userIDString, @PathVariable("chatID") String chatIDString, @RequestParam(value = "startMessageID", required = false) String startMessageIDString, @RequestParam(value = "endMessageID", required = false) String endMessageIDString, @RequestParam(value = "count", required = false) String countString, HttpServletRequest request) {

        Timestamp receivedTS = Timestamp.now();
        String path = request.getRequestURI();
        Map<String, List<Map<String, Object>>> responseBody;

        long userID = Long.parseLong(userIDString);
        long chatID = Long.parseLong(chatIDString);
        long startMessageID;
        long endMessageID;

        int uppLimitOfMessageCountToReturn = 1000;
        int lowLimitOfMessageCountToReturn = 50;
        int count;

        Timestamp startCreationTS;
        Timestamp endCreationTS;

        List<Message> messages;
        
        //check if the passed userID is valid
        if (queryUser.checkIfUserIDExists(userID) == false) {
            throw new UserIDDoesNotExistException(path);
        }
        
        //check if the passed chatID is valid
        if (queryChat.checkIfChatIDExists(chatID) == false) {
            throw new ChatIDDoesNotExistException(path);
        }

        //check if user is part of chat
        if (queryUserChat.checkIfUserChatIDExists(userID, chatID) == false) {
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
            startMessageID = Long.parseLong(startMessageIDString);
            //check if startMessageID is valid
            if (queryMessage.checkIfMessageIDExists(startMessageID) == false) {
                throw new MessageIDDoesNotExistException(path);
            } 

            //check if startMessageID is part of this chat
            if (queryMessage.checkIfMessageIDBelongsToChatID(startMessageID, chatID) == false) {
                throw new MessageIDDoesNotBelongToChatIDException(path);
            } 

            //get CreationTS of startMessageID 
            startCreationTS = queryMessage.getCreationTSForMessageID(startMessageID);
        } else {
            startCreationTS = null;
        }

        if (endMessageIDString != null) {
            endMessageID = Long.parseLong(endMessageIDString);
            //check if endMessageID is valid
            if (queryMessage.checkIfMessageIDExists(endMessageID) == false) {
                throw new MessageIDDoesNotExistException(path);
            } 

            //check if endMessageID is part of this chat
            if (queryMessage.checkIfMessageIDBelongsToChatID(endMessageID, chatID) == false) {
                throw new MessageIDDoesNotBelongToChatIDException(path);
            }

            //get CreationTS of endCreationTS 
            endCreationTS = queryMessage.getCreationTSForMessageID(endMessageID);
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
            messages = queryMessage.listCountMessagesOfChatIDWithinGivenTime(startCreationTS, endCreationTS, count, chatID);
        } else if (startCreationTS != null) {
            //get messages beginning at the start time
            messages = queryMessage.listCountMessagesOfChatIDFromStartTime(startCreationTS, count, chatID);
        } else if (endCreationTS != null) {
            //get messages before the end time
            messages = queryMessage.listCountMessagesOfChatIDBeforeEndTime(endCreationTS, count, chatID);
        } else {
            //get latest messages 
            messages = queryMessage.listLatestCountMessagesOfChatID(count, chatID);
        }

        //check ReceivedTS of each message not sent by current user- if it is null set it to the time when listMessages was called
        for (Message message : messages) {
            if (message.getReceivedTS() == null && message.getSenderID() != userID) {
                message.setReceivedTS(receivedTS);
                insertMessage.insertReceivedTS(message);
            }
        }
        
        return SuccessResponseGenerator.getSuccessResponseForListMessages(messages);
    }
}