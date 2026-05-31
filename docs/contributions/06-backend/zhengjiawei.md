# 后端开发贡献说明

姓名：郑嘉玮
学号：2312190119
日期：2026-04-14

## 我完成的工作

### 1. 项目架构搭建

- 初始化 Spring Boot 3.5.12 + MyBatis 3.0.5 项目骨架（Java 17）
- 设计分层架构：controller → service → mapper → entity，遵循经典三层架构
- 配置 CORS 跨域支持（`WebConfig.java`），允许前后端分离开发
- 统一项目包结构：`com.zjgsu.teamplatform`

### 2. 统一响应与错误处理

- 设计通用响应体 `Result<T>`，统一接口返回格式（`code` + `message` + `data`）
- 定义 `ErrorCode` 枚举：`SUCCESS(200)` / `BAD_REQUEST(400)` / `UNAUTHORIZED(401)` / `NOT_FOUND(404)` / `CONFLICT(409)` / `INTERNAL_ERROR(500)`
- 实现 `GlobalExceptionHandler` 全局异常处理：
  - 业务异常 `BizException` 统一捕获
  - `@Valid` 参数校验异常（`MethodArgumentNotValidException` 等）统一处理
  - JSON 反序列化异常兜底
  - 未捕获异常 `Exception` 兜底
- 定义 `Constants` 系统常量类，管理请求头 Key、状态枚举值等

### 3. 用户模块

#### API 接口
- `POST /api/user/register` — 用户注册，支持 username、password、nickname、email
- `POST /api/user/login` — 用户登录，通过 `X-User-Id` 请求头传递身份
- `GET /api/user/profile` — 获取当前用户资料
- `PUT /api/user/profile` — 更新用户资料（nickname、avatar 等）

#### 实现细节
- `RegisterRequest` / `LoginRequest` / `UserProfileUpdateRequest` DTO，使用 `@Valid` 校验
- `UserVO` 返回视图，屏蔽敏感字段（password）
- `UserService` + `UserServiceImpl` 实现业务逻辑
- `UserMapper` MyBatis 映射

### 4. 团队模块

#### API 接口
- `POST /api/team/create` — 创建团队（创建者自动成为第一个成员，角色 CREATOR）
- `GET /api/team/list` — 查看所有活跃团队列表
- `GET /api/team/{id}` — 查看团队详情
- `GET /api/team/my` — 查看我的团队列表
- `PUT /api/team/{id}` — 更新团队信息（仅管理员）
- `DELETE /api/team/{id}` — 删除团队（仅创建者）
- `GET /api/team/{id}/members` — 查看团队成员列表
- `POST /api/team/{id}/members` — 添加团队成员（管理员权限）
- `DELETE /api/team/{id}/members/{userId}` — 移除团队成员（管理员权限）

#### 实现细节
- `TeamCreateRequest` / `TeamUpdateRequest` / `TeamMemberAddRequest` DTO
- `TeamVO` 视图对象，包含创建者名称、成员数量等聚合信息
- 业务规则：创建者自动加入团队、人数上限校验、权限分级（CREATOR → ADMIN → MEMBER）
- `TeamService` + `TeamServiceImpl` 完整 CRUD + 成员管理

### 5. 加入请求模块

- `POST /api/join/request` — 发送加入团队请求（含理由 reason）
- `GET /api/join/requests` — 查看我的加入请求
- `GET /api/team/{id}/join-requests` — 查看团队收到的加入请求（管理员权限）
- `POST /api/join/{requestId}/approve` — 批准加入请求
- `POST /api/join/{requestId}/reject` — 拒绝加入请求
- 状态流转：`PENDING` → `APPROVED` / `REJECTED`
- 唯一约束：同一用户对同一团队只能有一条待处理请求

### 6. 邀请模块

- `POST /api/invitation/send` — 团队管理员发送邀请
- `GET /api/invitation/list` — 查看我收到的邀请
- `GET /api/team/{id}/invitations` — 查看团队发出的邀请（管理员权限）
- `POST /api/invitation/{id}/accept` — 接受邀请
- `POST /api/invitation/{id}/reject` — 拒绝邀请
- 状态流转：`PENDING` → `ACCEPTED` / `REJECTED`

### 7. 通知，聊天模块（14周新增，下同）

- `GET /api/notification/list` — 查看我的通知列表（按类型分类）
- `POST /api/notification/{id}/read` — 标记通知为已读
- `DELETE /api/notification/{id}` — 删除通知

- `GET /api/chat/list` — 获取私聊会话列表
- `GET /api/chat/conversation/{otherUserId}` — 查询与指定用户的聊天记录（支持分页）
- `POST /api/chat/send` — 发送私聊消息
- `POST /api/chat/conversation/{otherUserId}/read` — 标记消息已读

- `GET /api/team-chat/list` — 获取团队聊天室列表
- `GET /api/team-chat/team/{teamId}` — 查询团队聊天记录（支持分页）
- `POST /api/team-chat/send` — 发送团队聊天消息
- `POST /api/team-chat/team/{teamId}/read` — 标记团队聊天已读

### 10. 数据模型与数据库

#### 数据库表设计
| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `user` | 用户表 | id, username, nickname, password, email, avatar, created_at, updated_at |
| `team` | 团队表 | id, name, description, tag, creator_id, max_members, current_members, status |
| `team_member` | 团队成员表 | id, team_id, user_id, role(CREATOR/ADMIN/MEMBER), joined_at |
| `join_request` | 加入请求表 | id, team_id, user_id, status(PENDING/APPROVED/REJECTED), reason, requested_at, processed_at |
| `invitation` | 邀请表 | id, invite_id, team_id, user_id, status(PENDING/ACCEPTED/REJECTED), created_at |
| `notification` | 通知表 | id, user_id, message, type(JOIN_REQUEST/SYSTEM/TEAM_UPDATE), is_read, created_at |
| `chat_conversation` | 私聊会话表 | id, user_id, other_user_id, last_message, last_time, unread_count |
| `chat_message` | 私聊消息表 | id, conversation_id, sender_id, receiver_id, content, is_read, created_at |
| `team_chat_message` | 团队聊天消息表 | id, team_id, sender_id, content, created_at |
| `team_chat_read` | 团队聊天已读表 | id, team_id, user_id, last_read_at |

#### ORM 映射
- 使用 MyBatis 注解 + XML 混合方式定义 SQL
- `@Mapper` 注解标注所有 Mapper 接口
- 实体类使用 Lombok `@Data` 简化代码
