CREATE TABLE User (
  UserID INT64 NOT NULL,
  CreationTS TIMESTAMP NOT NULL OPTIONS (
    allow_commit_timestamp = true
  ),
  Username STRING(MAX) NOT NULL,
  EmailID STRING(MAX),
  MobileNo STRING(MAX),
  Password STRING(MAX),
  Picture BYTES(MAX),
) PRIMARY KEY(UserID);

CREATE TABLE Chat (
  CreationTS TIMESTAMP NOT NULL OPTIONS (
    allow_commit_timestamp = true
  ),
  ChatID INT64 NOT NULL,
  LastSentMessageID INT64,
) PRIMARY KEY(ChatID);

CREATE TABLE Message (
  CreationTS TIMESTAMP NOT NULL OPTIONS (
    allow_commit_timestamp = true
  ),
  MessageID INT64 NOT NULL,
  ChatID INT64 NOT NULL,
  SenderID INT64 NOT NULL,
  ContentType STRING(MAX) NOT NULL,
  TextContent STRING(MAX),
  ContentID INT64,
  LinkToBlob STRING(MAX),
  SentTS TIMESTAMP NOT NULL OPTIONS (
    allow_commit_timestamp = true
  ),
  ReceivedTS TIMESTAMP,
  CONSTRAINT FK_MessageUser FOREIGN KEY(SenderID) REFERENCES User(UserID),
  CONSTRAINT FK_MessageChat FOREIGN KEY(ChatID) REFERENCES Chat(ChatID),
) PRIMARY KEY(MessageID);

CREATE TABLE UserChat (
  UserID INT64 NOT NULL,
  ChatID INT64 NOT NULL,
  CONSTRAINT FK_Chat FOREIGN KEY(ChatID) REFERENCES Chat(ChatID),
  CONSTRAINT FK_UserChatChat FOREIGN KEY(ChatID) REFERENCES Chat(ChatID),
  CONSTRAINT FK_UserChatUser FOREIGN KEY(UserID) REFERENCES User(UserID),
) PRIMARY KEY(UserID, ChatID)

ALTER TABLE Chat ADD CONSTRAINT FK_Message FOREIGN KEY(LastSentMessageID) REFERENCES Message(MessageID);