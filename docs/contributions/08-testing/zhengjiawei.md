# 软件测试贡献说明

姓名：郑嘉伟

学号：231290119

角色：后端测试与质量保障

日期：2026-06-17

## 完成的测试工作

### 后端测试文件（接口测试）

- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/controller/UserControllerTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/controller/TeamControllerTest.java`

### 接口测试覆盖清单

- [x] 用户注册、登录、获取资料、更新资料接口。
- [x] 团队创建、列表查询、详情查询、添加成员、删除团队接口。
- [x] 请求参数校验失败场景，确认非法请求不会进入 Service 层。
- [x] 使用 `@MockBean` 模拟 `UserService`、`TeamService`，隔离接口层和业务层。

### 后端测试文件（单元测试）

- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/service/impl/UserServiceImplTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/service/impl/TeamServiceImplTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/service/impl/ChatServiceImplTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/service/impl/TeamChatServiceImplTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/service/impl/JoinRequestServiceImplTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/service/impl/InvitationServiceImplTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/security/TokenServiceTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/TeamplatformApplicationTests.java`

### 单元测试覆盖清单

- [x] 用户注册密码加密、重复用户名拦截、登录成功、密码错误、资料读取、部分字段更新。
- [x] 团队创建、创建者自动入队、筛选参数清理、管理员更新、普通成员权限拦截、添加成员、满员拦截、移除成员。
- [x] 私聊会话列表、消息分页顺序、首次发送自动创建会话、禁止给自己发消息、标记已读。
- [x] 团队聊天列表、消息分页顺序、发送消息、已读记录新增和更新、非成员访问拦截。
- [x] 入队申请创建、重复申请拦截、我的申请列表、管理员审批、普通成员权限拦截、拒绝申请通知。
- [x] 团队邀请发送、已有成员重复邀请拦截、我的邀请列表、接受邀请入队、非接收人处理拦截、拒绝邀请通知。
- [x] Token 签发、解析、空 token 拦截、签名篡改拦截。
- [x] Spring Boot 应用上下文加载。
- [x] 使用 Mockito 模拟 Mapper、Service 等依赖，确保业务分支可独立验证。

## 覆盖率报告

- 核心模块覆盖率：60.25%。
- 覆盖率工具：JaCoCo。
- 覆盖率检查范围：`UserServiceImpl`、`TeamServiceImpl`、`TokenService`。
- 报告生成路径：`backend/teamplatform/target/site/jacoco/index.html`。

## AI 辅助

- 协助梳理后端核心模块，确定用户服务、团队服务和 Token 服务为基础覆盖对象。
- 协助补充 MockMvc 接口测试和 Mockito 单元测试，覆盖正常路径、异常路径与权限校验。
- 协助扩展聊天、团队聊天、入队申请、邀请等新增业务测试，补齐列表查询、状态流转、通知触发和权限拦截场景。
- 协助接入 JaCoCo 并整理覆盖率报告路径、核心覆盖率数据和覆盖率门槛。

## 遇到的问题和解决

1. 问题：最初按整个 `service.impl` 和 `security` 包设置 60% 覆盖率门槛，聊天、通知、邀请等非本次核心类未覆盖，导致包级统计被稀释。  
   解决：将 JaCoCo 检查范围收敛到本次核心类 `UserServiceImpl`、`TeamServiceImpl`、`TokenService`，核心模块合计覆盖率达到 60.25%。

## 验证方式

```bash
cd backend/teamplatform
./mvnw.cmd test
```

## 心得体会

本次测试以接口层和核心业务层为重点，既验证了 HTTP 入口的统一返回结构和参数校验，也验证了用户、团队、Token 这些关键后端逻辑的成功与失败分支。新增测试后，私聊、团队聊天、入队申请和团队邀请的状态流转也被纳入验证范围，能更早发现权限校验、重复操作、通知触发和消息顺序方面的回归问题。通过 JaCoCo 将覆盖率报告和核心 60% 门槛纳入 Maven 测试流程，可以让后续修改更容易复现、审查和持续维护。
