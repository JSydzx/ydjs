## 1. 前端架构设计 (Frontend Architecture)

### 1.1 技术选型
核心语言: Kotlin 2.0+
UI 框架: Jetpack Compose (声明式 UI)
架构模式: MVVM + Clean Architecture (分层解耦)
依赖注入: Hilt (编译时注入)
异步处理: Kotlin Coroutines + Flow (响应式数据流)
网络库: Retrofit + OkHttp
图片加载: Coil (专为 Compose 优化)
本地存储: Room (关系型数据库) + DataStore (偏好设置)

### 1.2 模块结构 (Multi-Module)
采用按业务功能拆分的多模块架构，实现并行开发与编译加速：
:app (入口壳工程)
:core-ui (通用组件库：按钮、卡片、主题)
:core-network (网络配置与拦截器)
:core-data (本地数据库封装)
:feature-home (首页信息流、公告)
:feature-hall (活动大厅、日程表)
:feature-publish (发布编辑器、图片上传)
:feature-profile (个人中心、队伍管理)
:domain (纯业务逻辑 UseCases 与 Entities，无 Android 依赖)

### 1.3 关键设计策略
单向数据流 (UDF): UI 事件 -> ViewModel -> UseCase -> Repository -> Data Source。状态通过 StateFlow 从 ViewModel 流向 UI，确保状态可预测。
离线优先 (Offline-First): 所有列表数据优先读取 Room 缓存，后台静默同步网络数据，确保弱网体验。
类型安全导航: 使用 sealed class 定义路由，配合 Navigation Compose 实现类型安全的页面跳转及参数传递。
图片优化: 全局配置 Coil 内存/磁盘缓存策略，列表项使用占位图与渐显动画，防止 OOM。

## 2. 后端架构设计 (Backend Architecture)

### 2.1 技术选型
开发语言: Java 17 / Kotlin (推荐 Kotlin 以保持全栈语言统一)
核心框架: Spring Boot 3.x
API 风格: RESTful API (JSON)
认证授权: Spring Security + JWT (无状态认证) + OAuth2 (可选，用于第三方登录)
对象存储: MinIO 或 阿里云 OSS (存储用户头像、帖子图片、视频)
消息队列: RabbitMQ / Kafka (用于异步处理图片压缩、通知推送、日志记录)
缓存中间件: Redis (热点数据缓存、Session 管理、分布式锁)

### 2.2 服务分层
Controller 层: 接收 HTTP 请求，参数校验，统一响应封装。
Service 层: 核心业务逻辑 (事务控制、权限校验、流程编排)。
Repository 层: 数据访问接口，屏蔽底层存储细节。
Infrastructure 层: 第三方服务适配 (OSS, SMS, Push)。

### 2.3 关键设计策略
统一异常处理: 全局 @ControllerAdvice 捕获异常，返回标准化错误码与消息。
接口幂等性: 针对“发布帖子”、“报名活动”等写操作，利用 Redis Token 机制防止重复提交。
读写分离: 针对高频读操作 (首页信息流)，引入 Redis 缓存；低频写操作直接落库。
异步解耦: 用户注册成功后的“发送欢迎邮件/通知”、“初始化用户数据”等操作通过 MQ 异步执行，降低主接口延迟。

## 3. 数据库设计 (Database Design)

### 3.1 技术选型
关系型数据库: MySQL 8.0 / PostgreSQL 15+ (存储核心业务数据)
非关系型数据库: Redis 7.0+ (缓存、计数器、排行榜)
搜索引擎 (可选): Elasticsearch (若帖子搜索需求复杂)

### 3.2 核心 ER 模型 (简化版)
#### A. 用户与认证 (User & Auth)
users: id, username, password_hash, nickname, avatar_url, school_id, status, created_at
user_profiles: user_id, bio, gender, birthday, sports_tags (JSON)
#### B. 内容与社交 (Content & Social)
posts: id, user_id, title, content, category_id, view_count, like_count, comment_count, status, created_at
post_images: id, post_id, image_url, sort_order
comments: id, post_id, user_id, parent_id, content, created_at
likes: user_id, target_type (POST/COMMENT), target_id, created_at (唯一索引防重)
#### C. 活动与日程 (Events & Schedule)
events: id, organizer_id, title, description, start_time, end_time, location, max_capacity, current_count, status
event_participants: event_id, user_id, join_status (PENDING/APPROVED/REJECTED), joined_at
teams: id, name, captain_id, sport_type, description, member_count
team_members: team_id, user_id, role (CAPTAIN/Member)
#### D. 系统基础 (System)
categories: id, name, icon, sort_order (如：篮球、足球、网球)
notifications: id, user_id, type, content, is_read, related_id, created_at

### 3.3 索引与优化策略
高频查询索引: posts(category_id, created_at) 用于首页列表分页；events(start_time) 用于日程筛选。
覆盖索引: 针对 likes 表的统计查询，建立联合索引。
分表策略: 若 comments 或 notifications 数据量过大，按 user_id 或 post_id 进行哈希分表。

## 4. 系统交互流程设计 (System Interaction Flows)

## 4.1 核心业务流程图例说明
Client: Android App (Compose)
Gateway: Nginx / API Gateway
Server: Spring Boot Service
Cache: Redis
DB: MySQL

## 4.2 场景一：首页信息流加载 (Offline-First 策略)
sequenceDiagram
    participant User
    participant App as Android App
    participant Repo as Repository
    participant LocalDB as Room (Local)
    participant Server as Backend API
    participant Cache as Redis

    User->>App: 打开首页
    App->>Repo: 请求首页帖子流
    
    rect rgb(240, 248, 255)
        note right of App: 第一阶段：立即返回本地缓存
        Repo->>LocalDB: 查询最近缓存的帖子
        LocalDB-->>Repo: 返回旧数据 (若有)
        Repo-->>App: 发射 StateFlow (Loading -> Success[OldData])
        App->>User: 渲染旧数据 (无闪烁)
    end

    rect rgb(255, 250, 240)
        note right of App: 第二阶段：后台静默刷新
        App->>Server: 异步请求最新数据 (携带 last_timestamp)
        Server->>Cache: 检查热点数据
        Cache-->>Server: (可选) 命中缓存
        Server->>LocalDB: 若无缓存则查 DB
        LocalDB-->>Server: 返回数据
        Server-->>App: 返回最新 JSON
        
        App->>LocalDB: 更新本地缓存 (Insert/Update)
        App->>Repo: 重新请求/更新数据源
        Repo-->>App: 发射 StateFlow (Success[NewData])
        App->>User: 平滑更新列表 (DiffUtil)
    end

## 4.3 场景二：用户发布动态 (含图片上传)
sequenceDiagram
    participant User
    participant App as Android App
    participant Server as Backend API
    participant OSS as 对象存储
    participant MQ as 消息队列
    participant DB as MySQL

    User->>App: 输入内容，选择图片，点击发布
    App->>App: 本地压缩图片，表单校验
    
    par 并行处理
        App->>OSS: 上传图片文件 (Multipart)
        OSS-->>App: 返回图片 URL 列表
    and
        note right of App: 等待图片上传完成
    end

    App->>Server: 提交帖子数据 (文本 + 图片 URLs)
    
    Server->>Server: 鉴权 & 敏感词过滤
    Server->>DB: 插入帖子记录 (事务)
    DB-->>Server: 返回 Post ID
    
    Server->>MQ: 发送 "PostCreated" 事件
    MQ-->>Server: 确认接收
    Server-->>App: 返回发布成功
    
    App->>User: 提示成功，跳转至详情页/首页
    
    note right of MQ: 异步消费者处理
    MQ->>Server(Consumer): 消费事件
    Server(Consumer)->>DB: 增加用户积分
    Server(Consumer)->>Server: 推送通知给粉丝

## 4.4 场景三：活动报名与名额扣减 (防超卖)
sequenceDiagram
    participant User
    participant App as Android App
    participant Server as Backend API
    participant Redis as Redis (Lock/Count)
    participant DB as MySQL

    User->>App: 点击“立即报名”
    App->>Server: 发起报名请求 (EventID, UserID)
    
    Server->>Redis: 尝试获取分布式锁 (Key: event_lock_{id})
    alt 获取锁失败
        Server-->>App: 返回“系统繁忙，请稍后”
    else 获取锁成功
        Server->>Redis: 检查剩余名额 (decr)
        alt 名额 <= 0
            Server->>Redis: 释放锁
            Server-->>App: 返回“名额已满”
        else 名额充足
            Server->>DB: 开启事务
            Server->>DB: 插入报名记录
            Server->>DB: 更新活动当前人数
            Server->>DB: 提交事务
            
            Server->>Redis: 释放锁
            Server-->>App: 返回“报名成功”
        end
    end

## 4.5 交互协议规范
通信协议: HTTPS (TLS 1.3)
数据格式: JSON (UTF-8)
状态码规范:
HTTP 200: 业务成功 (code: 200, data: {...})
HTTP 401: 未登录/Token 过期 (code: 401, msg: "Unauthorized")
HTTP 403: 权限不足
HTTP 400: 参数错误
HTTP 500: 服务器内部错误
版本控制: URL 路径包含版本号，如 /api/v1/posts。