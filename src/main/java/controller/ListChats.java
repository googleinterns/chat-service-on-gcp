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
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import com.google.cloud.Timestamp;
import com.google.common.collect.ImmutableMap;

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

    @Autowired
    private MessageAccessor queryMessage;

    @Autowired
    private UniqueIdGenerator uniqueIdGenerator;

    /**
     * Encapsulation for ChatId, Username and MobileNo of one of the Users engaged in that Chat.
     */
    public static final class UsernameMobileNoChatId {

        @Column(name = "Username")
        private String username;

        @Column(name = "ChatID")
        private long chatId;

        @Column(name = "MobileNo")
        private String mobileNo;

        public UsernameMobileNoChatId() {}

        public UsernameMobileNoChatId(String username, String mobileNo, long chatId) {
            this.mobileNo = mobileNo;
            this.username = username;
            this.chatId = chatId;
        }

        public String getMobileNo() {
            return this.mobileNo;
        }

        public String getUsername() {
            return username;
        }

        public long getChatId() {
            return chatId;
        }
    }

    /**
     * Returns details of given Chat in a Map.
     * Details include:
     * <ol>
     * <li> ChatId </li>
     * <li> Username (of the other user) </li>
     * <li> LastSentMessageId </li>
     * </ol>
     */
    ImmutableMap<String, Object> getChatInfoOfChatInMap(Chat chat, Pair<String, String> secondUser) {
        
        ImmutableMap<String, Object> chatInfoOfChatInMap = ImmutableMap.<String, Object> builder()
                                                                        .put("ChatId", chat.getChatId())
                                                                        .put("Username", secondUser.getFirst())
                                                                        .put("MobileNo", secondUser.getSecond())
                                                                        .put("LastSentMessageId", chat.getLastSentMessageId())
                                                                        .build();

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

        User user = User.newBuilder().userId(userId).build();
        List<Chat> chatsOfUser = queryChat.getChatsForUser(user);
        ImmutableList.Builder<Long> listOfChatIdBuilder = ImmutableList.builder();

        for (Chat chat : chatsOfUser) {
            listOfChatIdBuilder.add(chat.getChatId());
        }

        ImmutableList<Long> listOfChatId = listOfChatIdBuilder.build();
        ImmutableList<Message> listOfChatIdCreationTsOfLastSentMessageId = queryMessage.getCreationTsOfLastSentMessageIdForChatsOfUser(userId, listOfChatId);

        //Stores time of the last Message sent in each ChatId of the User against ChatId.
        Map<Long, Timestamp> chatIdCreationTsOflastSentMessageIdMap = new LinkedHashMap<Long, Timestamp>();

        for (Message chatIdCreationTsOfLastSentMessageId : listOfChatIdCreationTsOfLastSentMessageId) {
            chatIdCreationTsOflastSentMessageIdMap.put(chatIdCreationTsOfLastSentMessageId.getChatId(), chatIdCreationTsOfLastSentMessageId.getCreationTs());
        }
        
        //Sets time of the last Message sent for each Chat of the User.
        for (Chat chat : chatsOfUser) {
            if (chat.getLastSentMessageId() == 0) { 
                chat.setLastSentTime(chat.getCreationTs());
            } else {
                chat.setLastSentTime(chatIdCreationTsOflastSentMessageIdMap.get(chat.getChatId()));
            }
        }
        
        Collections.sort(chatsOfUser, Comparator.comparing(Chat::getLastSentTime).reversed());

        ImmutableList<UsernameMobileNoChatId> usernameMobileNoChatIdForSecondUsers = queryUser.getUsernameMobileNoChatIdForSecondUsers(userId, listOfChatId);

        //Stores username of the other User against ChatId for each Chat of the User.
        Map<Long, Pair<String, String>> chatIdSecondUsernameMap = new LinkedHashMap<Long, Pair<String, String>>();

        for (UsernameMobileNoChatId usernameMobileNoChatId : usernameMobileNoChatIdForSecondUsers) {
            chatIdSecondUsernameMap.put(usernameMobileNoChatId.getChatId(), Pair.of(usernameMobileNoChatId.getUsername(), usernameMobileNoChatId.getMobileNo()));
        }

        //Stores list of all details of each Chat of the User.
        ImmutableList.Builder<ImmutableMap<String, Object>> chatInfoOfChatsOfUserBuilder = ImmutableList.builder();

        for (Chat chat : chatsOfUser) {
            chatInfoOfChatsOfUserBuilder.add(getChatInfoOfChatInMap(chat, chatIdSecondUsernameMap.get(chat.getChatId())));
        }

        ImmutableList<ImmutableMap<String, Object>> chatInfoOfChatsOfUser = chatInfoOfChatsOfUserBuilder.build();

        return SuccessResponseGenerator.getSuccessResponseForListChats(chatInfoOfChatsOfUser);
    }
}
