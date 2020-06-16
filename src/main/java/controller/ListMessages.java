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

/**
 * Controller which responds to client requests to get the list of messages of a chat.
 * Each entry in the response contains:
 * (1)  MessageId
 * (2)  CreationTs
 * (3)  ChatId
 * (4)  SentByCurrentUser
 * (5)  ContentType
 * (6)  TextContent
 * (7)  SentTs
 * (8)  ReceivedTs
 */
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

    /**
     * Responds to requests with missing userId and chatId URL Path Variables.
     * Throws an exception for missing userId URL Path Variable. 
     */
    @GetMapping("/users/chats/messages")
    public void listMessagesWithoutUserIdChatIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to requests with missing chatId URL Path Variable.
     * Throws an exception for the same. 
     */
    @GetMapping("/users/chats/{chatId}/messages")
    public void listMessagesWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to requests with missing chatId URL Path Variable.
     * Throws an exception for the same. 
     */
    @GetMapping("/users/{userId}/chats/messages")
    public void listMessagesWithoutChatIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new ChatIdMissingFromRequestURLPathException(path);
    }
    
    /**
     * Responds to complete requests.
     * Returns the list of message details of the given Chat.
     */
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
        
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        
        if (!queryChat.checkIfChatIdExists(chatId)) {
            throw new ChatIdDoesNotExistException(path);
        }

        if (!queryUserChat.checkIfUserChatIdExists(userId, chatId)) {
            throw new UserChatIdDoesNotExistException(path);
        }

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
            
            if (!queryMessage.checkIfMessageIdExists(startMessageId)) {
                throw new MessageIdDoesNotExistException(path);
            } 
            
            if (!queryMessage.checkIfMessageIdBelongsToChatId(startMessageId, chatId)) {
                throw new MessageIdDoesNotBelongToChatIdException(path);
            } 
            
            startCreationTs = queryMessage.getCreationTsForMessageId(startMessageId);
        } else {
            startCreationTs = null;
        }

        if (endMessageIdString != null) {
            endMessageId = Long.parseLong(endMessageIdString);
            
            if (!queryMessage.checkIfMessageIdExists(endMessageId)) {
                throw new MessageIdDoesNotExistException(path);
            } 
            
            if (!queryMessage.checkIfMessageIdBelongsToChatId(endMessageId, chatId)) {
                throw new MessageIdDoesNotBelongToChatIdException(path);
            }
            
            endCreationTs = queryMessage.getCreationTsForMessageId(endMessageId);
        } else {
            endCreationTs = null;
        }

        if (startCreationTs != null && endCreationTs != null) {
            /*
             * Checks if startCreationTs is after endCreationTs.
             */
            if (startCreationTs.compareTo(endCreationTs) > 0) {
                Timestamp temp = startCreationTs;
                startCreationTs = endCreationTs;
                endCreationTs = temp;
            }
            
            messages = queryMessage.listCountMessagesOfChatIdWithinGivenTime(startCreationTs, endCreationTs, count, chatId);
        } else if (startCreationTs != null) {
            messages = queryMessage.listCountMessagesOfChatIdFromStartTime(startCreationTs, count, chatId);
        } else if (endCreationTs != null) {
            messages = queryMessage.listCountMessagesOfChatIdBeforeEndTime(endCreationTs, count, chatId);
        } else {
            messages = queryMessage.listLatestCountMessagesOfChatId(count, chatId);
        }

        /*
         * Checks ReceivedTs of each message not sent by current user- if it is null set it to the time when listMessages was called.
         */
        for (Message message : messages) {
            if (message.getReceivedTs() == null && message.getSenderId() != userId) {
                message.setReceivedTs(receivedTs);
                insertMessage.updateReceivedTs(message);
            }
        }
        
        return SuccessResponseGenerator.getSuccessResponseForListMessages(userId, messages);
    }
}
