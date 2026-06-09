CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    nickname VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    avatar VARCHAR(255),
    major VARCHAR(100),
    grade VARCHAR(50),
    skills VARCHAR(255),
    bio VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS team (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    tag VARCHAR(255),
    creator_id BIGINT NOT NULL,
    max_members INT DEFAULT 10,
    current_members INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_team_creator FOREIGN KEY (creator_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS team_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) DEFAULT 'MEMBER',
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_team_user UNIQUE (team_id, user_id),
    CONSTRAINT fk_team_member_team FOREIGN KEY (team_id) REFERENCES team(id),
    CONSTRAINT fk_team_member_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS join_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    requested_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME NULL,
    reason VARCHAR(1000),
    CONSTRAINT uk_join_team_user UNIQUE (team_id, user_id),
    CONSTRAINT fk_join_request_team FOREIGN KEY (team_id) REFERENCES team(id),
    CONSTRAINT fk_join_request_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS invitation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invite_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invitation_inviter FOREIGN KEY (invite_id) REFERENCES user(id),
    CONSTRAINT fk_invitation_team FOREIGN KEY (team_id) REFERENCES team(id),
    CONSTRAINT fk_invitation_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(30) DEFAULT 'SYSTEM',
    related_id BIGINT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS chat_conversation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    last_message_id BIGINT,
    last_message_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_chat_conversation UNIQUE (user1_id, user2_id),
    CONSTRAINT fk_chat_conv_user1 FOREIGN KEY (user1_id) REFERENCES user(id),
    CONSTRAINT fk_chat_conv_user2 FOREIGN KEY (user2_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_msg_conv FOREIGN KEY (conversation_id) REFERENCES chat_conversation(id),
    CONSTRAINT fk_chat_msg_sender FOREIGN KEY (sender_id) REFERENCES user(id),
    CONSTRAINT fk_chat_msg_recipient FOREIGN KEY (recipient_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS team_chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_team_chat_team FOREIGN KEY (team_id) REFERENCES team(id),
    CONSTRAINT fk_team_chat_sender FOREIGN KEY (sender_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS team_chat_read (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    last_read_at DATETIME,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_team_chat_read UNIQUE (team_id, user_id),
    CONSTRAINT fk_team_chat_read_team FOREIGN KEY (team_id) REFERENCES team(id),
    CONSTRAINT fk_team_chat_read_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE INDEX idx_team_creator ON team(creator_id);
CREATE INDEX idx_member_team ON team_member(team_id);
CREATE INDEX idx_member_user ON team_member(user_id);
CREATE INDEX idx_join_team ON join_request(team_id);
CREATE INDEX idx_join_user ON join_request(user_id);
CREATE INDEX idx_invitation_team ON invitation(team_id);
CREATE INDEX idx_invitation_user ON invitation(user_id);
CREATE INDEX idx_notification_user ON notification(user_id);
CREATE INDEX idx_chat_conv_user1 ON chat_conversation(user1_id);
CREATE INDEX idx_chat_conv_user2 ON chat_conversation(user2_id);
CREATE INDEX idx_chat_msg_conv ON chat_message(conversation_id);
CREATE INDEX idx_chat_msg_recipient ON chat_message(recipient_id);
CREATE INDEX idx_team_chat_team ON team_chat_message(team_id);
CREATE INDEX idx_team_chat_created ON team_chat_message(created_at);
CREATE INDEX idx_team_chat_read_team ON team_chat_read(team_id);
CREATE INDEX idx_team_chat_read_user ON team_chat_read(user_id);
