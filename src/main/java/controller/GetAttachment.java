package controller;

import entity.Message;
import entity.Attachment;
import dbaccessor.user.UserAccessor;
import dbaccessor.chat.ChatAccessor;
import dbaccessor.message.MessageAccessor;
import dbaccessor.message.AttachmentAccessor;
import dbaccessor.userchat.UserChatAccessor;
import helper.SuccessResponseGenerator;
import exceptions.UserIdDoesNotExistException;
import exceptions.ChatIdDoesNotExistException;
import exceptions.MessageIdDoesNotExistException;
import exceptions.UserChatIdDoesNotExistException;
import exceptions.UserIdMissingFromRequestURLPathException;
import exceptions.ChatIdMissingFromRequestURLPathException;
import exceptions.MessageIdDoesNotBelongToChatIdException;
import exceptions.MessageDoesNotContainAttachmentException;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller which responds to client requests to get the details and Blob of an Attachment.
 * The response contains:
 * <ol>
 * <li> FileName </li>
 * <li> FileType </li>
 * <li> FileSize </li>
 * <li> Blob </li>
 * </ol>
 */
@RestController
public final class GetAttachment {

    @Autowired
    private UserAccessor queryUser;

    @Autowired
    private ChatAccessor queryChat;

    @Autowired
    private MessageAccessor queryMessage;

    @Autowired
    private AttachmentAccessor queryAttachment;

    @Autowired
    private UserChatAccessor queryUserChat;

    /**
     * Responds to requests with missing userId URL Path Variable.
     * Throws an exception for the same. 
     */
    @GetMapping("/users/chats/{chatId}/messages/{messageId}/attachments")
    public void getAttachmentWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to requests with missing chatId URL Path Variable.
     * Throws an exception for the same. 
     */
    @GetMapping("/users/{userId}/chats/messages/{messageId}/attachments")
    public void getAttachmentWithoutChatIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new ChatIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to complete requests.
     * Returns details and Blob of the requested Attachment.
     */
    @GetMapping("/users/{userId}/chats/{chatId}/messages/{messageId}/attachments")
    public Map<String, Object> getAttachment(@PathVariable("userId") String userIdString, @PathVariable("chatId") String chatIdString, @PathVariable("messageId") String messageIdString, HttpServletRequest request) {

        String path = request.getRequestURI();

        long userId = Long.parseLong(userIdString);
        long chatId = Long.parseLong(chatIdString);
        long messageId = Long.parseLong(messageIdString);

        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        
        if (!queryChat.checkIfChatIdExists(chatId)) {
            throw new ChatIdDoesNotExistException(path);
        }

        //Checks if the user is part of chat.
        if (!queryUserChat.checkIfUserChatIdExists(userId, chatId)) {
            throw new UserChatIdDoesNotExistException(path);
        }
        
        if (!queryMessage.checkIfMessageIdExists(messageId)) {
            throw new MessageIdDoesNotExistException(path);
        }

        if (!queryMessage.checkIfMessageIdBelongsToChatId(messageId, chatId)) {
            throw new MessageIdDoesNotBelongToChatIdException(path);
        }

        Attachment attachment = queryMessage.getAttachmentFromMessageId(messageId);
        
        if (attachment.getAttachmentId() == 0) {
            throw new MessageDoesNotContainAttachmentException(path);
        }

        byte[] blob = queryAttachment.getBlob(attachment.getAttachmentId());
        
        return SuccessResponseGenerator.getSuccessResponseForGetAttachment(attachment, blob);
    }
}
