package Entity;

import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

@Table(name = "MessageChat")
public class MessageChat {
    
    @PrimaryKey(keyOrder = 1)
    @Column(name = "MessageID")
    Long messageID;

    @PrimaryKey(keyOrder = 2)
    @Column(name = "ChatID")
    Long chatID;
}