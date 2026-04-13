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
