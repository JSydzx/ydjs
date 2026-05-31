# 软件架构设计贡献说明

姓名：郑嘉玮
学号：2312190119
日期：2026-03-22

## 我完成的工作

### 1. 架构设计

- [ ] 前端架构设计
- [√] 后端架构设计
- [√] 数据库设计
- [ ] 系统交互流程设计

#### 后端架构设计

- 设计分层架构：**Controller → Service → Mapper → Entity**，遵循经典三层架构，职责清晰
- 确定统一响应格式 `Result<T>`（`code` + `message` + `data`），前端可统一处理所有接口返回
- 设计 `ErrorCode` 枚举错误码体系：SUCCESS(200) / BAD_REQUEST(400) / UNAUTHORIZED(401) / NOT_FOUND(404) / CONFLICT(409) / INTERNAL_ERROR(500)
- 设计全局异常处理机制：`@RestControllerAdvice` + `GlobalExceptionHandler`，覆盖业务异常、参数校验异常、反序列化异常、未捕获异常
- 设计 `Constants` 系统常量类，集中管理请求头 Key（`X-User-Id`）、状态枚举值等
- 设计 CORS 跨域配置（`WebConfig`），支持前后端分离开发
- 设计包结构：`controller` / `service` + `service/impl` / `mapper` / `entity` / `dto` / `vo` / `common` / `exception` / `config`

#### 数据库设计

- 完成完整的 ER 模型设计，共 **10 张核心数据表**：

| 表名 | 说明 |
|------|------|
| `user` | 用户表 |
| `team` | 团队表 |
| `team_member` | 团队成员表 |
| `join_request` | 加入请求表 |
| `invitation` | 邀请表 |
| `notification` | 通知表 |
| `chat_conversation` | 私聊会话表 |
| `chat_message` | 私聊消息表 |
| `team_chat_message` | 团队聊天消息表 |
| `team_chat_read` | 团队聊天已读状态表 |

- 设计表间关联关系：`user` 1:N `team`（创建），`user` N:M `team`（通过 `team_member`），`user` 1:N `join_request`，`user` 1:N `notification`
- 设计字段约束：唯一索引（`team_member(team_id, user_id)`、`join_request(team_id, user_id)`）、外键关联、状态枚举字段
- 设计状态流转：加入请求 `PENDING → APPROVED / REJECTED`，邀请 `PENDING → ACCEPTED / REJECTED`
- 绘制 ER 图（mermaid 格式），体现核心实体关系

### 2. 技术选型

| 类别 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 开发语言 | Java | 17 LTS | 长期支持版本，生态成熟 |
| 核心框架 | Spring Boot | 3.5.12 | 主流 Java Web 框架，自动配置、起步依赖 |
| ORM 框架 | MyBatis | 3.0.5 | 灵活 SQL 控制，适合复杂查询场景 |
| 数据库 | MySQL | 8.0 | 成熟的关系型数据库，社区广泛 |
| 构建工具 | Maven | 3.9+ | Java 生态标准构建工具 |
| 代码简化 | Lombok | 1.18.34 | 减少样板代码（@Data、@RequiredArgsConstructor 等） |
| 密码加密 | Spring Security Crypto | — | BCrypt 加密用户密码 |
| 参数校验 | Jakarta Validation | — | @Valid + 注解校验请求参数 |
| 结构化日志 | Logstash Logback Encoder | 7.0.1 | JSON 格式日志，便于日志收集与分析 |
| 可观测性 | Micrometer + Prometheus | — | 应用指标采集与暴露 |
| 容器化 | Docker | — | 多阶段构建，最小化镜像体积 |

### 3. 环境搭建

- [ ] 前端项目初始化
- [√] 后端项目初始化
- [√] 数据库连接配置
- [√] CLAUDE.md 编写

#### 后端项目初始化

- 使用 Maven 构建 Spring Boot 项目骨架
- 配置多环境 `application.yml`：
  - `spring.datasource` — MySQL 连接（URL、用户名、密码、驱动）
  - `mybatis` — mapper 扫描路径、类型别名
  - `spring.actuator` — 健康检查端点暴露
- 引入核心依赖：spring-boot-starter-web、mybatis-spring-boot-starter、mysql-connector-j、lombok、spring-security-crypto 等
- 编写 Dockerfile（多阶段构建：maven 构建 + jre-alpine 运行），配置 HEALTHCHECK、非 root 用户运行
- 配置 `.dockerignore` 排除无关文件，优化构建上下文
- 配置 `.gitignore` 排除 target、logs、.idea 等

#### 数据库连接配置

- 配置 MySQL 8.0 数据源连接
- 配置 H2 内存数据库用于测试环境
- MyBatis mapper XML 路径映射与类型别名配置

#### AGENTS.md 编写

- 编写项目级 AI Agent 指令文档（`AGENTS.md`），定义：
  - 代码规范（KISS、DRY、命名规范、注释规范）
  - 分层结构要求（controller / service / model）
  - 编码行为规则（复用已有代码、单一职责、禁止长函数）
  - 运行与测试要求、Bug 修复流程、禁止行为清单

### 4. 文档编写

- [ ] architecture.md（全部）
- [√] architecture.md（第 2 节：后端架构设计）
- [√] architecture.md（第 3 节：数据库设计）
- [√] backend.md — 后端技术文档（技术栈、目录结构、数据库设计、实体类、DTO/VO、API 接口、依赖配置、业务逻辑说明）
- [√] docs/api.md — API 接口文档（用户、团队、团队成员、加入请求、邀请、通知 共 6 大模块 27 个接口）
- [√] docs/database.md — 数据库 ER 图（mermaid 格式）
- [√] docs/design-spec.md — 设计说明（配色方案、字体规范、核心页面、交互说明）
- [√] 其他文档（README.md 中的团队分工、功能需求等）

## PR 链接

- PR #X: https://github.com/JSydzx/ydjs/tree/main/

## 遇到的问题和解决

1. **ER 图绘制工具选择**：最初尝试使用 PlantUML，但因 GitHub 原生渲染支持不佳，最终切换到 mermaid 格式，可在 GitHub 上直接渲染。
2. **技术栈选型权衡**：在 Spring Data JPA 和 MyBatis 之间做了权衡。考虑到项目中多表关联查询较多，MyBatis 手动 SQL 更灵活，最终选择 MyBatis。
3. **数据库表设计迭代**：初期设计了 6 张基础表（user、team、team_member、join_request、invitation、notification），第 14 周新增聊天功能后扩展了 4 张聊天相关表（chat_conversation、chat_message、team_chat_message、team_chat_read）。

## 心得体会

通过本次架构设计工作，深入理解了后端分层架构的设计原则与数据库建模方法。在技术选型过程中，学会了根据实际需求权衡不同技术方案的利弊。数据库设计方面，掌握了 ER 模型设计、字段约束、状态流转等核心技能，也体会到了需求变更时数据库扩展的挑战。文档编写方面，提升了技术文档的规范化表达能力。
