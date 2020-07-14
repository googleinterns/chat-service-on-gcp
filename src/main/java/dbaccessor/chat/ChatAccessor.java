package dbaccessor.chat;

import entity.User;
import entity.Chat;
import entity.UserChat;
import controller.ListChats;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;
import org.springframework.cloud.gcp.data.spanner.core.SpannerOperations;

/**
 * Accessor which performs database accesses for the Chat entity.
 */
@Component
public final class ChatAccessor {
    
    @Autowired
    SpannerOperations spannerOperations;

    @Autowired
    private SpannerTemplate spannerTemplate;

    /**
     * Updates the LastSentMessageId attribute in a Chat object .
     */
    public void updateLastSentMessageId(Chat chat) {
        spannerTemplate.update(chat, "ChatID", "LastSentMessageID");
    }

    /**
     * Completes all DB insertions for the CreateChat API in a single transaction.
     */
    public boolean insertForCreateChatTransaction(Chat chat, UserChat userChat1, UserChat userChat2) {
        return spannerOperations.performReadWriteTransaction(
            transactionSpannerOperations -> {
                transactionSpannerOperations.upsert(chat, "ChatID", "CreationTS");
                transactionSpannerOperations.insert(userChat1);
                transactionSpannerOperations.insert(userChat2);

                return true;
            }
        );
    }

    /**
     * Checks if a Chat with the given chatId exists.
     */
    public boolean checkIfChatIdExists(long chatId) {

        String sqlStatment = "SELECT ChatID FROM Chat WHERE ChatID=@chatId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("chatId").to(chatId).build();
        List<Chat> resultSet = spannerTemplate.query(Chat.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return !resultSet.isEmpty();        
    }

    /**
     * Returns details of the Chat with the given ChatId.
     * Details include:
     * <ol>
     * <li> ChatId </li>
     * <li> LastSentMessageId </li>
     * <li> Creation Timestamp </li>
     * </ol>
     */
    public Chat getChat(long chatId) {

        String sqlStatment = "SELECT * FROM Chat WHERE ChatID=@chatId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("chatId").to(chatId).build();
        List<Chat> resultSet = spannerTemplate.query(Chat.class, statement, null);
 
        return resultSet.get(0);
    }

    /**
     * Returns details of all Chats which the given UserId engaged in.
     * Details include:
     * <ol>
     * <li> ChatId </li>
     * <li> Creation Timestamp of Chat </li>
     * <li> LastSentMessageId </li>
     * <li> Creation Timestamp of LastSentMessage </li>
     * <li> Username of Second User in Chat </li>
     * <li> MobileNo of Second User in Chat </li>
     * </ol>
     */
    public List<ListChats.AllInfoForListChats> getAllInfoForListChats(long userId) {
        String sqlStatment = "SELECT "
                                + "WithUserID.ChatID AS ChatID, " 
                                + "WithUserID.ChatCreationTS AS ChatCreationTS, " 
                                + "WithUserID.LastSentMessageID AS LastSentMessageID, " 
                                + "WithUserID.LastSentMessageCreationTS AS LastSentMessageCreationTS, " 
                                + "User.Username AS Username, " 
                                + "User.MobileNo AS MobileNo " 
                            + "FROM (" 
                                + "SELECT " 
                                    + "WithLastSentMessageCreationTS.ChatID AS ChatID, " 
                                    + "WithLastSentMessageCreationTS.ChatCreationTS AS ChatCreationTS, " 
                                    + "WithLastSentMessageCreationTS.LastSentMessageID AS LastSentMessageID, " 
                                    + "WithLastSentMessageCreationTS.LastSentMessageCreationTS AS LastSentMessageCreationTS, " 
                                    + "UserChat.UserID AS UserID " 
                                + "FROM (" 
                                    + "SELECT " 
                                            + "UserChat.ChatID AS ChatID, " 
                                            + "Chat.CreationTS AS ChatCreationTS, " 
                                            + "Chat.LastSentMessageID AS LastSentMessageID, " 
                                            + "Message.CreationTS AS LastSentMessageCreationTS " 
                                        + "FROM UserChat@{FORCE_INDEX=UserChatByUserID} " 
                                        + "INNER JOIN Chat " 
                                        + "ON UserChat.ChatID = Chat.ChatID " 
                                        + "LEFT OUTER JOIN Message " 
                                        + "ON Chat.LastSentMessageID = Message.MessageID " 
                                        + "WHERE UserChat.UserID = @userId" 
                                        + ") AS WithLastSentMessageCreationTS " 
                                + "INNER JOIN UserChat@{FORCE_INDEX=UserChatByChatID} " 
                                + "ON WithLastSentMessageCreationTS.ChatID = UserChat.ChatID " 
                                + "WHERE UserChat.UserID != @userId" 
                                + ") AS WithUserID " 
                            + "INNER JOIN User " 
                            + "ON WithUserID.UserID = User.UserID";

        Statement statement = Statement.newBuilder(sqlStatment).bind("userId").to(userId).build();
        return spannerTemplate.query(ListChats.AllInfoForListChats.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
    }
}
