package controller;

import entity.Message;
import dbaccessor.user.UserAccessor;
import dbaccessor.chat.ChatAccessor;
import dbaccessor.message.MessageAccessor;
import dbaccessor.userchat.UserChatAccessor;
import helper.SuccessResponseGenerator;
import exceptions.UserIdDoesNotExistException;
import exceptions.ChatIdDoesNotExistException;
import exceptions.UserChatIdDoesNotExistException;
import exceptions.MessageIdDoesNotExistException;
import exceptions.MessageIdDoesNotBelongToChatIdException;
import exceptions.UserIdMissingFromRequestURLPathException;
import exceptions.ChatIdMissingFromRequestURLPathException;
import exceptions.InvalidCountValueInRequestURLParameterException;

import java.util.Map;

import com.google.cloud.Timestamp;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public final class ListMessages {

    @Autowired 
    private UserAccessor queryUser;

    @Autowired 
    private ChatAccessor queryChat;

    @Autowired 
    private MessageAccessor queryMessage;

    @Autowired 
    private UserChatAccessor queryUserChat;

    @Autowired 
    private MessageAccessor insertMessage;

    private static final int UPPER_LIMIT_OF_MESSAGE_COUNT_TO_RETURN = 1000;
    
    private static final int LOWER_LIMIT_OF_MESSAGE_COUNT_TO_RETURN = 50;

    @GetMapping("/users/chats/messages")
    public void listMessagesWithoutUserIdChatIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/chats/{chatId}/messages")
    public void listMessagesWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userId}/chats/messages")
    public void listMessagesWithoutChatIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new ChatIdMissingFromRequestURLPathException(path);
    }
    
    @GetMapping("/users/{userId}/chats/{chatId}/messages")
    public Map<String, List<Map<String, Object>>> listMessages(@PathVariable("userId") String userIdString, @PathVariable("chatId") String chatIdString, @RequestParam(value = "startMessageId", required = false) String startMessageIdString, @RequestParam(value = "endMessageId", required = false) String endMessageIdString, @RequestParam(value = "count", required = false) String countString, HttpServletRequest request) {

        Timestamp receivedTs = Timestamp.now();
        String path = request.getRequestURI();
        Map<String, List<Map<String, Object>>> responseBody;

        long userId = Long.parseLong(userIdString);
        long chatId = Long.parseLong(chatIdString);
        long startMessageId;
        long endMessageId;

        int count;

        Timestamp startCreationTs;
        Timestamp endCreationTs;

        List<Message> messages;
        
        //check if the passed userId is valid
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        
        //check if the passed chatId is valid
        if (!queryChat.checkIfChatIdExists(chatId)) {
            throw new ChatIdDoesNotExistException(path);
        }

        //check if user is part of chat
        if (!queryUserChat.checkIfUserChatIdExists(userId, chatId)) {
            throw new UserChatIdDoesNotExistException(path);
        }

        //configure the value of count
        if (countString == null) {
            count = LOWER_LIMIT_OF_MESSAGE_COUNT_TO_RETURN;
        } else {
            count = Integer.parseInt(countString);

            if (count < 0) {
                throw new InvalidCountValueInRequestURLParameterException(path);
            }
            count = Math.min(count, UPPER_LIMIT_OF_MESSAGE_COUNT_TO_RETURN);
            count = Math.max(count, LOWER_LIMIT_OF_MESSAGE_COUNT_TO_RETURN);
        }

        if (startMessageIdString != null) {
            startMessageId = Long.parseLong(startMessageIdString);
            //check if startMessageId is valid
            if (!queryMessage.checkIfMessageIdExists(startMessageId)) {
                throw new MessageIdDoesNotExistException(path);
            } 

            //check if startMessageId is part of this chat
            if (!queryMessage.checkIfMessageIdBelongsToChatId(startMessageId, chatId)) {
                throw new MessageIdDoesNotBelongToChatIdException(path);
            } 

            //get CreationTs of startMessageId 
            startCreationTs = queryMessage.getCreationTsForMessageId(startMessageId);
        } else {
            startCreationTs = null;
        }

        if (endMessageIdString != null) {
            endMessageId = Long.parseLong(endMessageIdString);
            //check if endMessageId is valid
            if (!queryMessage.checkIfMessageIdExists(endMessageId)) {
                throw new MessageIdDoesNotExistException(path);
            } 

            //check if endMessageId is part of this chat
            if (!queryMessage.checkIfMessageIdBelongsToChatId(endMessageId, chatId)) {
                throw new MessageIdDoesNotBelongToChatIdException(path);
            }

            //get CreationTs of endCreationTs 
            endCreationTs = queryMessage.getCreationTsForMessageId(endMessageId);
        } else {
            endCreationTs = null;
        }

        if (startCreationTs != null && endCreationTs != null) {
            //check if startMessageId is after endMessageId
            if (startCreationTs.compareTo(endCreationTs) > 0) {
                Timestamp temp = startCreationTs;
                startCreationTs = endCreationTs;
                endCreationTs = temp;
            }
            //get messages within the required time frame
            messages = queryMessage.listCountMessagesOfChatIdWithinGivenTime(startCreationTs, endCreationTs, count, chatId);
        } else if (startCreationTs != null) {
            //get messages beginning at the start time
            messages = queryMessage.listCountMessagesOfChatIdFromStartTime(startCreationTs, count, chatId);
        } else if (endCreationTs != null) {
            //get messages before the end time
            messages = queryMessage.listCountMessagesOfChatIdBeforeEndTime(endCreationTs, count, chatId);
        } else {
            //get latest messages 
            messages = queryMessage.listLatestCountMessagesOfChatId(count, chatId);
        }

        //check ReceivedTs of each message not sent by current user- if it is null set it to the time when listMessages was called
        for (Message message : messages) {
            if (message.getReceivedTs() == null && message.getSenderId() != userId) {
                message.setReceivedTs(receivedTs);
                insertMessage.insertReceivedTs(message);
            }
        }
        
        return SuccessResponseGenerator.getSuccessResponseForListMessages(userId, messages);
    }
}
