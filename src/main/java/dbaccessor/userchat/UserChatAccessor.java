package dbaccessor.userchat;

import entity.UserChat;
import controller.CreateChat;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

/**
 * Accessor which performs database accesses for the UserChat entity.
 */
@Component
public final class UserChatAccessor {
    
    @Autowired
    private SpannerTemplate spannerTemplate;

    /**
     * Inserts all attributes of the given UserChat in the DB.
     */
    public void insertAll(UserChat userChat) {
        spannerTemplate.insert(userChat);
    }
    
    /**
     * Returns ChatId of the Chat between the given Users.
     * Returns an empty List if Chat does not exist between the given Users. 
     */
    public ImmutableList<UserChat> getChatIdIfChatExistsBetweenUserIds(long userId1, long userId2) {

        String sqlStatment = "SELECT ChatID FROM UserChat WHERE UserID=@userId2 AND ChatID IN (SELECT ChatID FROM UserChat WHERE UserID=@userId1)";
        Statement statement = Statement.newBuilder(sqlStatment).bind("userId2").to(userId2).bind("userId1").to(userId1).build();
        ImmutableList<UserChat> resultSet = ImmutableList.copyOf(spannerTemplate.query(UserChat.class, statement, new SpannerQueryOptions().setAllowPartialRead(true)));
    
        return resultSet;
    }

    /**
     * Checks if a UserChat entry for the given userId and chatId exists.
     */
    public boolean checkIfUserChatIdExists(long userId, long chatId) {

        String sqlStatment = "SELECT * FROM UserChat WHERE UserID=@userId AND ChatID=@chatId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("userId").to(userId).bind("chatId").to(chatId).build();
        List<UserChat> resultSet = spannerTemplate.query(UserChat.class, statement, null);
    
        return !resultSet.isEmpty();
    }

    /**
     * Finds the UserId corresponding to the given username.
     * If a Chat exists between the two Users, returns its ChatId and the UserId found above.
     * Otherwise, returns a zero ChatId along with the UserId found above. 
     */
    public CreateChat.ChatIdWithUserIds getChatIdWithUserIdsIfExistsBetweenUsers(long userId, String username) {
        String sqlStatment = "SELECT " 
                                + "ChatsOfUserID2.ChatID AS ChatID, " 
                                + "UserChat.UserID AS UserID1, "
                                + "ChatsOfUserID2.UserID2 AS UserID2, "
                            + "FROM (" 
                                + "SELECT " 
                                    + "SecondUserID.UserID2 AS UserID2, " 
                                    + "UserChat.ChatID AS ChatID " 
                                + "FROM (" 
                                    + "SELECT UserID AS UserID2 " 
                                    + "FROM User " 
                                    + "WHERE Username = @username" 
                                    + ") AS SecondUserID " 
                                + "LEFT OUTER JOIN UserChat " 
                                + "ON SecondUserID.UserID2 = UserChat.UserID" 
                                + ") AS ChatsOfUserID2 " 
                            + "LEFT OUTER JOIN UserChat " 
                            + "ON ChatsOfUserID2.ChatID = UserChat.ChatID";

        Statement statement = Statement.newBuilder(sqlStatment).bind("username").to(username).bind("userId").to(userId).build();
        List<CreateChat.ChatIdWithUserIds> resultSet = spannerTemplate.query(
                                                        CreateChat.ChatIdWithUserIds.class, 
                                                        statement, 
                                                        new SpannerQueryOptions().setAllowPartialRead(true)
                                                        );

        for (CreateChat.ChatIdWithUserIds result : resultSet) {
            if (result.getUserId1() == userId) {
                return result;
            }
        }

        resultSet.get(0).setChatId(0);
        resultSet.get(0).setUserId1(0);

        return resultSet.get(0);
    }
}
