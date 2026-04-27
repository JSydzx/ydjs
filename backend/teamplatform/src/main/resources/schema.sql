CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    nickname VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    avatar VARCHAR(255),
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
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE INDEX idx_team_creator ON team(creator_id);
CREATE INDEX idx_member_team ON team_member(team_id);
CREATE INDEX idx_member_user ON team_member(user_id);
CREATE INDEX idx_join_team ON join_request(team_id);
CREATE INDEX idx_join_user ON join_request(user_id);
CREATE INDEX idx_invitation_team ON invitation(team_id);
CREATE INDEX idx_invitation_user ON invitation(user_id);
CREATE INDEX idx_notification_user ON notification(user_id);
