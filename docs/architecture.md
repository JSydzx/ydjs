## 1. 前端架构设计 (Frontend Architecture)

### 1.1 技术选型
- 核心框架	Vue 3.4+ (Composition API)	所有页面的响应式逻辑基础
- 构建工具	Vite 5.x	极速启动，支持热更新 (HMR)
- UI 组件库	Vant 4.x	核心 UI 来源 (见下文映射表)
- 状态管理	Pinia	用户信息、全局配置、帖子列表缓存
- 路由管理	Vue Router 4	底部 Tab 切换、页面跳转
- 网络请求	Axios	获取帖子、公告、用户数据
- 本地存储	Pinia Persisted + Dexie.js	登录 Token、离线帖子缓存、草稿箱
- 富文本/编辑器	WangEditor 或 Quill	对应图 4 的“填写正文”区域
- 日期处理	Day.js	对应图 5 的日程时间格式化
- 图标库	Vant Icons / Iconfont	底部导航栏、功能入口图标

| 设计图 | 关键 UI 元素 | 推荐 Vant 组件 |
| :--- | :--- | :--- |
| 图 1: 帖子 | 顶部标签切换、列表项、图片占位、点赞评论 | `van-tabs`, `van-cell`, `van-image` (lazy-load), `van-icon` |
| 图 2: 公告 | 轮播图、标签筛选、列表流 | `van-swipe` (轮播), `van-tag`, `van-list` (滚动加载) |
| 图 3: 个人 | 头像、数据统计、菜单列表 | `van-cell-group`, `van-grid` (可选), `van-image` |
| 图 4: 发布 | 输入框、底部工具栏、话题选择 | `van-field`, `van-action-sheet`, `van-uploader` (图片上传) |
| 图 5: 日程 | 周视图表格、时间段划分 | `van-calendar` (辅助), 自定义 `div/table` 布局 (因需展示上午/下午/晚上) |

### 1.2 模块结构 (Multi-Module)

src/
├── App.vue                      # 根组件 (包含 <router-view> 和 <van-tabbar>)
├── main.ts                      # 入口文件
│
├── features/                    # 【核心业务层】对应底部 5 个 Tab 及主要功能
│   │
│   ├── home/                    # [图 1 & 图 2] 首页与公告
│   │   ├── components/          # 特有组件：PostCard.vue, AnnouncementSwipe.vue
│   │   ├── views/               # 页面：HomeView.vue (含 Tabs 切换)
│   │   ├── store.ts             # 状态：postList, activeTab
│   │   └── api.ts               # 接口：getPostList(), getAnnouncements()
│   │
│   ├── hall/                    # [图 5] 活动大厅与日程
│   │   ├── components/          # 特有组件：ScheduleGrid.vue (自定义周视图)
│   │   ├── views/               # 页面：HallView.vue, ScheduleView.vue
│   │   └── api.ts               # 接口：getSchedule()
│   │
│   ├── publish/                 # [图 4] 发布编辑器
│   │   ├── components/          # 特有组件：TopicSelector.vue, ImageUploader.vue
│   │   ├── views/               # 页面：PublishView.vue
│   │   └── store.ts             # 状态：draftContent, selectedTags
│   │
│   ├── team/                    # [隐含] 队伍管理 (对应底部"队伍"Tab)
│   │   └── ...
│   │
│   └── profile/                 # [图 3] 个人中心
│       ├── components/          # 特有组件：UserStats.vue, MenuList.vue
│       ├── views/               # 页面：ProfileView.vue, SettingsView.vue
│       └── api.ts               # 接口：getUserInfo(), updateProfile()
│
├── core/                        # 【核心基础设施层】
│   ├── ui/                      # 全局通用组件 (二次封装 Vant)
│   │   ├── Layout/              # 页面容器 (处理安全区、背景色)
│   │   └── Empty/               # 统一空状态页 (无数据时显示)
│   ├── router/                  # 路由配置 (routes.ts)
│   ├── store/                   # 全局 Pinia Store (user, appConfig)
│   ├── utils/                   # 工具函数 (formatDate, validate)
│   └── styles/                  # 全局样式 (variables.scss, theme.css)
│
├── domain/                      # 【领域层】纯业务实体 (可选，小型项目可简化)
│   ├── entities/                # User.ts, Post.ts, Team.ts
│   └── repositories/            # 接口定义 (IUserRepository)
│
└── data/                        # 【数据层】
    ├── api/                     # Axios 实例封装 (interceptors.ts)
    └── db/                      # IndexedDB 封装 (用于离线缓存帖子)

### 1.3 关键设计策略
#### 1. 沉浸式布局与适配 (Responsive Layout)
- 策略: 利用 Vant 的 ConfigProvider 配置主题色，使用 viewport 单位 (vw/vh) 或 rem 适配不同屏幕。
- 安全区: 在 App.vue 或 Layout 组件中使用 CSS env(safe-area-inset-bottom) 处理 iPhone X+ 底部黑条，确保底部 TabBar 不被遮挡。
- 暗黑模式: 预留 CSS 变量，未来可一键切换深色模式。
#### 2. 高性能列表渲染 (Performance List)
- 场景: 图 1 和 图 2 的长列表。
- 实现:
  - 使用 Vant 的 <van-list> 组件实现下拉刷新和上拉加载更多。
  - 图片懒加载: <van-image lazy-load> 配合占位图，防止长列表滑动卡顿。
  - 虚拟滚动 (可选): 如果单页帖子超过 100 条，引入 vue-virtual-scroller 优化 DOM 节点数量。
#### 3. 表单与发布体验优化 (Publishing UX)
- 场景: 图 4 发布页面。
- 草稿箱: 利用 Pinia Persisted 监听 publish/store.ts 中的内容变化，自动保存到 LocalStorage。用户意外退出后重新进入可恢复草稿。
- 图片上传: 使用 <van-uploader>，在上传前进行本地压缩 (使用 browser-image-compression)，减少流量消耗。
- 防抖提交: 点击“发布”按钮后，立即禁用按钮并显示 Loading 状态，防止重复提交。
#### 4. 复杂的日程视图定制 (Custom Schedule View)
- 场景: 图 5 日程表。
- 挑战: Vant 没有现成的“周 x 上午/下午/晚上”网格组件。
- 方案:
  - 使用 CSS Grid 或 Flex 布局手写一个 ScheduleGrid 组件。
  - 数据结构设计为二维数组：[ { day: 'Mon', slots: [{time: 'AM', events: []}, {time: 'PM', events: []}] } ]。
  - 点击单元格弹出 van-popup 显示具体活动详情。
#### 5. 统一交互反馈 (Unified Feedback)
- 加载状态: 所有异步操作（如拉取帖子、提交表单）必须展示 van-loading 或骨架屏 (van-skeleton)。
- 空状态: 当列表为空时（如新用户的个人中心），统一使用 van-empty 组件，并提供“去发布”或“去浏览”的操作按钮。
- 消息提示: 成功/失败操作统一使用 showToast (轻提示) 或 showNotify (顶部通知)。

### 1.4 开发路线图 (Roadmap)
#### Phase 1: 骨架搭建
- 初始化 Vite + Vue 3 + TS 项目。
- 配置 Vant 按需加载、Router、Pinia。
- 实现底部 van-tabbar 和 5 个基础空白页面。
#### Phase 2: 核心功能开发
- Home: 完成 Tabs 切换、PostCard 组件、列表无限滚动。
- Profile: 完成用户信息展示、菜单列表跳转。
- Publish: 完成表单输入、图片上传逻辑。
#### Phase 3: 业务逻辑对接
- 封装 Axios，对接后端 API。
- 实现登录鉴权 (Token 拦截器)。
- 接入 Dexie.js 实现离线缓存。
#### Phase 4: 细节打磨
- 完成日程表 (Hall) 的自定义布局。
- 添加骨架屏、空状态、加载动画。
- 真机调试，优化触摸体验和键盘弹起问题。


## 2. 后端架构设计 (Backend Architecture)

### 2.1 技术选型
- 开发语言: Java 17 / Kotlin (推荐 Kotlin 以保持全栈语言统一)
- 核心框架: Spring Boot 3.x
- API 风格: RESTful API (JSON)
- 认证授权: Spring Security + JWT (无状态认证) + OAuth2 (可选，用于第三方登录)
- 对象存储: MinIO 或 阿里云 OSS (存储用户头像、帖子图片、视频)
- 消息队列: RabbitMQ / Kafka (用于异步处理图片压缩、通知推送、日志记录)
- 缓存中间件: Redis (热点数据缓存、Session 管理、分布式锁)

### 2.2 服务分层
- Controller 层: 接收 HTTP 请求，参数校验，统一响应封装。
- Service 层: 核心业务逻辑 (事务控制、权限校验、流程编排)。
- Repository 层: 数据访问接口，屏蔽底层存储细节。
- Infrastructure 层: 第三方服务适配 (OSS, SMS, Push)。

### 2.3 关键设计策略
- 统一异常处理: 全局 @ControllerAdvice 捕获异常，返回标准化错误码与消息。
- 接口幂等性: 针对“发布帖子”、“报名活动”等写操作，利用 Redis Token 机制防止重复提交。
- 读写分离: 针对高频读操作 (首页信息流)，引入 Redis 缓存；低频写操作直接落库。
- 异步解耦: 用户注册成功后的“发送欢迎邮件/通知”、“初始化用户数据”等操作通过 MQ 异步执行，降低主接口延迟。

## 3. 数据库设计 (Database Design)

### 3.1 技术选型
- 关系型数据库: MySQL 8.0 / PostgreSQL 15+ (存储核心业务数据)
- 非关系型数据库: Redis 7.0+ (缓存、计数器、排行榜)
- 搜索引擎 (可选): Elasticsearch (若帖子搜索需求复杂)

### 3.2 核心 ER 模型 (简化版)
#### A. 用户与认证 (User & Auth)
- users: id, username, password_hash, nickname, avatar_url, school_id, status, created_at
- user_profiles: user_id, bio, gender, birthday, sports_tags (JSON)
#### B. 内容与社交 (Content & Social)
- posts: id, user_id, title, content, category_id, view_count, like_count, comment_count, status, created_at
- post_images: id, post_id, image_url, sort_order
- comments: id, post_id, user_id, parent_id, content, created_at
- likes: user_id, target_type (POST/COMMENT), target_id, created_at (唯一索引防重)
#### C. 活动与日程 (Events & Schedule)
- events: id, organizer_id, title, description, start_time, end_time, location, max_capacity, current_count, status
- event_participants: event_id, user_id, join_status (PENDING/APPROVED/REJECTED), joined_at
- teams: id, name, captain_id, sport_type, description, member_count
- team_members: team_id, user_id, role (CAPTAIN/Member)
#### D. 系统基础 (System)
- categories: id, name, icon, sort_order (如：篮球、足球、网球)
- notifications: id, user_id, type, content, is_read, related_id, created_at

### 3.3 索引与优化策略
- 高频查询索引: posts(category_id, created_at) 用于首页列表分页；events(start_time) 用于日程筛选。
- 覆盖索引: 针对 likes 表的统计查询，建立联合索引。
- 分表策略: 若 comments 或 notifications 数据量过大，按 user_id 或 post_id 进行哈希分表。

## 4. 系统交互流程设计 (System Interaction Flows)

## 4.1 核心业务流程图例说明
- Client: Android App (Compose)
- Gateway: Nginx / API Gateway
- Server: Spring Boot Service
- Cache: Redis
- DB: MySQL

## 4.2 场景一：首页信息流加载 (Offline-First 策略)
<img width="3578" height="1506" alt="tongyi-mermaid-2026-03-23-150815" src="https://github.com/user-attachments/assets/5046be00-2756-4405-bb77-65f3ded4be1b" />

## 4.3 场景二：用户发布动态 (含图片上传)
<img width="3578" height="1506" alt="tongyi-mermaid-2026-03-23-151201" src="https://github.com/user-attachments/assets/0d27e243-1b70-4aff-be21-2e4c888e4714" />

## 4.4 场景三：活动报名与名额扣减 (防超卖)
<img width="3578" height="1506" alt="tongyi-mermaid-2026-03-23-151215" src="https://github.com/user-attachments/assets/4119bc77-2681-41f7-9e27-cce51bcd3715" />


## 4.5 交互协议规范
- 通信协议: HTTPS (TLS 1.3)
- 数据格式: JSON (UTF-8)
- 状态码规范:
- HTTP 200: 业务成功 (code: 200, data: {...})
- HTTP 401: 未登录/Token 过期 (code: 401, msg: "Unauthorized")
- HTTP 403: 权限不足
- HTTP 400: 参数错误
- HTTP 500: 服务器内部错误
- 版本控制: URL 路径包含版本号，如 /api/v1/posts。
