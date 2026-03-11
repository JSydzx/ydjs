### 技术栈:
Springboot3
Mybatis
Mysql

### 版本:
Mysql8.0
Java21

### 启动方式
运行.jar文件

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
│   └── NotificationController.java
│
├── service
│   ├── UserService.java
│   ├── TeamService.java
│   ├── JoinRequestService.java
│   └── NotificationService.java
│
├── service/impl
│   ├── UserServiceImpl.java
│   ├── TeamServiceImpl.java
│   ├── JoinRequestServiceImpl.java
│   └── NotificationServiceImpl.java
│
├── mapper
│   ├── UserMapper.java
│   ├── TeamMapper.java
│   ├── TeamMemberMapper.java
│   ├── JoinRequestMapper.java
│   └── NotificationMapper.java
│
├── entity
│   ├── User.java
│   ├── Team.java
│   ├── TeamMember.java
│   ├── JoinRequest.java
│   └── Notification.java
│
├── dto
│   ├── LoginDTO.java
│   ├── TeamCreateDTO.java
│   └── JoinRequestDTO.java
│
├── vo
│   ├── TeamVO.java
│   └── UserVO.java
│
├── common
│   ├── Result.java
│   └── Constants.java
│
└── utils
    └── JwtUtils.java