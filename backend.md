
### 技术栈:
Spring Boot 3.5.9
MyBatis
MySQL

### 版本:
MySQL 8.0
Java 17

### 启动方式
build后运行.jar文件

### 目录结构（初步）
src/main/java/com/example/teamplatform
│
├── TeamPlatformApplication.java
│
├── config
│   └── WebConfig.java
│
├── controller
│   ├── UserController.java
│   ├── TeamController.java
│   ├── JoinRequestController.java
│   ├── NotificationController.java
│   └── InvitationController.java
│
├── service
│   ├── UserService.java
│   ├── TeamService.java
│   ├── JoinRequestService.java
│   ├── NotificationService.java
│   └── InvitationService.java
│
├── service/impl
│   ├── UserServiceImpl.java
│   ├── TeamServiceImpl.java
│   ├── JoinRequestServiceImpl.java
│   ├── NotificationServiceImpl.java
│   └── InvitationServiceImpl.java
│
├── mapper
│   ├── UserMapper.java
│   ├── TeamMapper.java
│   ├── TeamMemberMapper.java
│   ├── JoinRequestMapper.java
│   ├── NotificationMapper.java
│   └── InvitationMapper.java
│
├── entity
│   ├── User.java
│   ├── Team.java
│   ├── TeamMember.java
│   ├── JoinRequest.java
│   ├── Notification.java
│   └── Invitation.java
│
├── dto
│   ├── LoginDTO.java
│   ├── TeamCreateDTO.java
│   ├── JoinRequestDTO.java
│   └── InvitationDTO.java
│
├── vo
│   ├── TeamVO.java
│   ├── UserVO.java
│   └── InvitationVO.java
│
├── common
│   ├── Result.java
│   └── Constants.java
│
└── utils
    └── JwtUtils.java

## 数据库设计

### 用户表 (user)
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- username (VARCHAR(50), UNIQUE, NOT NULL)
- nickname(VARCHAR(50))
- password (VARCHAR(255), NOT NULL)
- email (VARCHAR(100), UNIQUE, NOT NULL)
- avatar (VARCHAR(255))
- created_at (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- updated_at (DATETIME, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)

### 团队表 (team)
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- name (VARCHAR(100), NOT NULL)
- description (TEXT)
- tag(VARCHAR(255))
- creator_id (BIGINT, NOT NULL, FOREIGN KEY -> user.id)
- max_members (INT, DEFAULT 10)
- current_members (INT, DEFAULT 1)
- status (ENUM('ACTIVE', 'CLOSED'), DEFAULT 'ACTIVE')
- created_at (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- updated_at (DATETIME, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)

### 团队成员表 (team_member)
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- team_id (BIGINT, NOT NULL, FOREIGN KEY -> team.id)
- user_id (BIGINT, NOT NULL, FOREIGN KEY -> user.id)
- role (ENUM('CREATOR', 'ADMIN', 'MEMBER'), DEFAULT 'MEMBER')
- joined_at (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- UNIQUE(team_id, user_id)

### 加入请求表 (join_request)
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- team_id (BIGINT, NOT NULL, FOREIGN KEY -> team.id)
- user_id (BIGINT, NOT NULL, FOREIGN KEY -> user.id)
- status (ENUM('PENDING', 'APPROVED', 'REJECTED'), DEFAULT 'PENDING')
- requested_at (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- processed_at (DATETIME)
- reason(VARCHAR(1000))
- UNIQUE(team_id, user_id)

### 通知表 (notification)
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- user_id (BIGINT, NOT NULL, FOREIGN KEY -> user.id)
- message (TEXT, NOT NULL)
- type ENUM('JOIN_REQUEST','SYSTEM','TEAM_UPDATE')
- is_read (BOOLEAN, DEFAULT FALSE)
- created_at (DATETIME, DEFAULT CURRENT_TIMESTAMP)

### 邀请表(invitation)

- id
- invite_id 
- team_id
- user_id
- status ENUM('PENDING','ACCEPTED','REJECTED')
- created_at

## 实体类 (Entity)

### User.java
```java
public class User {
    private Long id;
    private String username;
    private String nickname;
    private String password;
    private String email;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // getters and setters
}
```

### Team.java
```java
public class Team {
    private Long id;
    private String name;
    private String description;
    private String tag;
    private Long creatorId;
    private Integer maxMembers;
    private Integer currentMembers;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // getters and setters
}
```

### TeamMember.java
```java
public class TeamMember {
    private Long id;
    private Long teamId;
    private Long userId;
    private String role;
    private LocalDateTime joinedAt;
    // getters and setters
}
```

### JoinRequest.java
```java
public class JoinRequest {
    private Long id;
    private Long teamId;
    private Long userId;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String reason;
    // getters and setters
}
```

### Notification.java
```java
public class Notification {
    private Long id;
    private Long userId;
    private String message;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    // getters and setters
}
```

### Invitation.java
```java
public class Invitation {
    private Long id;
    private Long inviteId;
    private Long teamId;
    private Long userId;
    private String status;
    private LocalDateTime createdAt;
    // getters and setters
}
```

## DTO 和 VO

### LoginDTO.java
```java
public class LoginDTO {
    private String username;
    private String password;
    // getters and setters
}
```

### TeamCreateDTO.java
```java
public class TeamCreateDTO {
    private String name;
    private String description;
    private String tag;
    private Integer maxMembers;
    // getters and setters
}
```

### JoinRequestDTO.java
```java
public class JoinRequestDTO {
    private Long teamId;
    private String reason;
    // getters and setters
}
```

### InvitationDTO.java
```java
public class InvitationDTO {
    private Long teamId;
    private Long userId;
    // getters and setters
}
```

### InvitationVO.java
```java
public class InvitationVO {
    private Long id;
    private String teamName;
    private String inviterName;
    private String status;
    private LocalDateTime createdAt;
    // getters and setters
}
```

### TeamVO.java
```java
public class TeamVO {
    private Long id;
    private String name;
    private String description;
    private String tag;
    private String creatorName;
    private Integer currentMembers;
    private Integer maxMembers;
    private String status;
    private LocalDateTime createdAt;
    // getters and setters
}
```

### UserVO.java
```java
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    // getters and setters
}
```

## API 接口

### 用户相关
- POST /api/user/register - 用户注册 (包括 nickname)
- POST /api/user/login - 用户登录
- GET /api/user/profile - 获取用户信息 (包括 nickname, avatar)
- PUT /api/user/profile - 更新用户信息

### 团队相关
- POST /api/team/create - 发起组队 (创建团队，包括 tag)
- GET /api/team/list - 查看组队列表
- GET /api/team/{id} - 查看组队详情 (包括 tag)
- GET /api/team/my - 查看我的团队
- PUT /api/team/{id} - 更新团队信息
- DELETE /api/team/{id} - 删除团队 (仅创建者)

### 团队成员相关
- GET /api/team/{id}/members - 查看团队成员列表
- POST /api/team/{id}/members - 添加成员 (通过批准加入请求)
- DELETE /api/team/{id}/members/{userId} - 移除成员 (管理员权限)

### 加入请求相关
- POST /api/join/request - 加入组队 (发送加入请求，包括 reason)
- GET /api/join/requests - 查看我的加入请求
- GET /api/team/{id}/join-requests - 查看团队的加入请求 (管理员权限)
- POST /api/join/{requestId}/approve - 批准加入请求
- POST /api/join/{requestId}/reject - 拒绝加入请求

### 邀请相关
- POST /api/invitation/send - 发送邀请
- GET /api/invitation/list - 查看我的邀请
- GET /api/team/{id}/invitations - 查看团队的邀请 (管理员权限)
- POST /api/invitation/{id}/accept - 接受邀请
- POST /api/invitation/{id}/reject - 拒绝邀请

### 通知相关
- GET /api/notification/list - 查看通知列表 (包括 type)
- POST /api/notification/{id}/read - 标记通知为已读
- DELETE /api/notification/{id} - 删除通知

## 依赖配置 (pom.xml 示例)
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.3</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.9.1</version>
    </dependency>
</dependencies>
```

## 配置文件 (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/teamplatform?useSSL=false&serverTimezone=UTC
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.example.teamplatform.entity

jwt:
  secret: your-secret-key
  expiration: 86400000
```

## 安全配置
使用JWT进行身份验证。JwtUtils用于生成和验证token。

## 业务逻辑说明
- 发起组队：用户创建团队，成为创建者和第一个成员。
- 加入组队：用户发送加入请求，团队创建者可以批准或拒绝。
- 查看组队：用户可以浏览所有活跃团队，查看详情，查看自己的团队。
- 邀请成员：团队管理员可以发送邀请给用户，用户可以接受或拒绝邀请。
- 通知：系统发送通知，包括加入请求、系统消息、团队更新等。

此设计涵盖了基本功能，适合课程大作业水平。可以根据需要扩展更多功能，如团队聊天、文件共享等。