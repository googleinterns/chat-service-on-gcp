package controller;

import com.google.common.collect.ImmutableList;
import entity.User;
import entity.Chat;
import entity.Message;
import helper.UniqueIdGenerator;
import dbaccessor.user.UserAccessor;
import dbaccessor.chat.ChatAccessor;
import dbaccessor.message.MessageAccessor;
import helper.SuccessResponseGenerator;
import exceptions.UserIdDoesNotExistException;
import exceptions.UserIdMissingFromRequestURLPathException;

import java.util.Map;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import com.google.cloud.Timestamp;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.cloud.gcp.data.spanner.core.mapping.NotMapped;

/**
 * Controller which responds to client requests to get the list of chats which the user is engaged in.
 * Each entry in the response contains:
 * <ol>
 * <li> ChatId </li>
 * <li> Username of the other user </li>
 * <li> LastSentMessageId </li>
 * </ol>
 */
@RestController
public final class ListChats {
    
    @Autowired 
    private UserAccessor queryUser;

    @Autowired 
    private ChatAccessor queryChat;

    /**
     * Encapsulation for the following fields related to a Chat for one of the Users engaged in that Chat. 
     * <ol>
     * <li> ChatId </li>
     * <li> Creation Timestamp of Chat </li>
     * <li> LastSentMessageId </li>
     * <li> Creation Timestamp of LastSentMessage </li>
     * <li> Username of Second User in Chat </li>
     * <li> MobileNo of Second User in Chat </li>
     * </ol>
     */
    public static final class AllInfoForListChats {

        @Column(name = "ChatID")
        private long chatId;

        @Column(name = "ChatCreationTS")
        private Timestamp chatCreationTs;

        @Column(name = "LastSentMessageID")
        private long lastSentMessageId;

        @Column(name = "LastSentMessageCreationTS")
        private Timestamp lastSentMessageCreationTs;

        @Column(name = "Username")
        private String username;
        
        @Column(name = "MobileNo")
        private String mobileNo;

        @NotMapped
        private Timestamp lastSentTime;

        public AllInfoForListChats() {}

        public AllInfoForListChats(
            long chatId, 
            Timestamp chatCreationTs, 
            long lastSentMessageId, 
            Timestamp lastSentMessageCreationTs, 
            String username, 
            String mobileNo
            ) {
                this.chatId = chatId;
                this.chatCreationTs = chatCreationTs;
                this.lastSentMessageId = lastSentMessageId;
                this.lastSentMessageCreationTs = lastSentMessageCreationTs;
                this.username = username;
                this.mobileNo = mobileNo;
            }

        public void setChatId(long chatId) {
            this.chatId = chatId;
        }

        public void setChatCreationTs(Timestamp chatCreationTs) {
            this.chatCreationTs = chatCreationTs;
        }

        public void setLastSentMessageId(long lastSentMessageId) {
            this.lastSentMessageId = lastSentMessageId;
        }

        public void setLastSentMessageCreationTs(Timestamp lastSentMessageCreationTs) {
            this.lastSentMessageCreationTs = lastSentMessageCreationTs;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public void setLastSentTime(Timestamp lastSentTime) {
            this.lastSentTime = lastSentTime;
        }

        public long getChatId() {
            return chatId;
        }

        public Timestamp getChatCreationTs() {
            return chatCreationTs;
        }

        public long getLastSentMessageId() {
            return lastSentMessageId;
        }

        public Timestamp getLastSentMessageCreationTs() {
            return lastSentMessageCreationTs;
        }

        public String getUsername() {
            return username;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public Timestamp getLastSentTime() {
            return lastSentTime;
        }
    }

    /**
     * Returns details of given Chat in a Map.
     * Details include:
     * <ol>
     * <li> ChatId </li>
     * <li> Username (of the other user) </li>
     * <li> MobileNo (if any, of the other user) </li>
     * <li> LastSentMessageId </li>
     * </ol>
     */
    ImmutableMap<String, Object> getChatInfoOfChatInMap(AllInfoForListChats infoObject) {
        ImmutableMap.Builder<String, Object> chatInfoOfChatInMapBuilder = ImmutableMap.builder();

        chatInfoOfChatInMapBuilder.put("ChatId", infoObject.getChatId());
        chatInfoOfChatInMapBuilder.put("Username", infoObject.getUsername());

        String mobileNo = infoObject.getMobileNo();
        if (mobileNo != null) {
            chatInfoOfChatInMapBuilder.put("MobileNo", mobileNo);
        }
                                                                        
        chatInfoOfChatInMapBuilder.put("LastSentMessageId", infoObject.getLastSentMessageId());

        ImmutableMap<String, Object> chatInfoOfChatInMap = chatInfoOfChatInMapBuilder.build();

        return chatInfoOfChatInMap;
    }

    /**
     * Responds to requests with missing userId URL Path Variable.
     * Throws an exception for the same. 
     */
    @GetMapping("/users/chats")
    public void listChatsWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to complete requests.
     * Returns the list of chats which the User id engaged in.
     */
    @GetMapping("/users/{userId}/chats")
    public ImmutableMap<String, ImmutableList<ImmutableMap<String, Object>>> listChats(@PathVariable("userId") String userIdString, HttpServletRequest request) {
        
        String path = request.getRequestURI();

        long userId = Long.parseLong(userIdString);
        
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }

        List<AllInfoForListChats> allInfoForListChats = queryChat.getAllInfoForListChats(userId);

        for (AllInfoForListChats infoObject : allInfoForListChats) {
            if (infoObject.getLastSentMessageCreationTs() != null) {
                infoObject.setLastSentTime(infoObject.getLastSentMessageCreationTs());
            } else {
                infoObject.setLastSentTime(infoObject.getChatCreationTs());
            }
        }

        Collections.sort(allInfoForListChats, Comparator.comparing(ListChats.AllInfoForListChats::getLastSentTime).reversed());

        //Stores list of all details of each Chat of the User.
        ImmutableList.Builder<ImmutableMap<String, Object>> chatInfoOfChatsOfUserBuilder = ImmutableList.builder();

        for (AllInfoForListChats infoObject : allInfoForListChats) {
            chatInfoOfChatsOfUserBuilder.add(getChatInfoOfChatInMap(infoObject));
        }

        ImmutableList<ImmutableMap<String, Object>> chatInfoOfChatsOfUser = chatInfoOfChatsOfUserBuilder.build();

        return SuccessResponseGenerator.getSuccessResponseForListChats(chatInfoOfChatsOfUser);
    }
}
