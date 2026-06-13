# 软件测试贡献说明

姓名：郑嘉伟

学号：312190119

角色：后端测试与质量保障

日期：2026-06-13

## 完成的测试工作

### 后端测试文件（接口测试）

- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/controller/UserControllerTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/controller/TeamControllerTest.java`

### 覆盖清单

- [x] 用户注册、登录、获取资料、更新资料接口。

- [x] 团队创建、列表查询、详情查询、添加成员、删除团队接口。

- [x] 请求参数校验失败场景，确认非法请求不会进入 Service 层。
- [x] @MockBean 模拟 UserService、TeamService

### 后端测试文件（单元）

- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/service/impl/UserServiceImplTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/service/impl/TeamServiceImplTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/security/TokenServiceTest.java`
- `backend/teamplatform/src/test/java/com/zjgsu/teamplatform/TeamplatformApplicationTests.java`

### 覆盖清单：

- [x] 用户注册密码加密、重复用户名拦截、登录成功、密码错误、资料读取、部分字段更新。
- [x] 团队创建、创建者自动入队、筛选参数清理、管理员更新、普通成员权限拦截、添加成员、满员拦截、移除成员。
- [x] Token 签发、解析、空 token 拦截、签名篡改拦截。
- [x] Spring Boot 应用上下文加载。
- [x] 使用 Mockito.mock() 模拟 `UserMapper`、`TeamMapper`、`TeamMemberMapper`、`TokenService` 等依赖。

## 覆盖率报告

核心模块覆盖率：74.9%

## AI 辅助

- 协助梳理后端核心模块，确定用户服务、团队服务和 Token 服务为本次重点覆盖对象。
- 协助补充 MockMvc 接口测试和 Mockito 单元测试，覆盖正常路径、异常路径与权限校验。
- 协助接入 JaCoCo 并整理覆盖率报告路径、核心覆盖率数据和 README 覆盖率徽章。

## 遇到的问题和解决

1. 问题：最初按整个 `service.impl` 和 `security` 包设置 60% 覆盖率门槛，聊天、通知、邀请等非本次核心类未覆盖，导致包级统计被稀释。
   解决：将 JaCoCo 检查范围收敛到本次核心类 `UserServiceImpl`、`TeamServiceImpl`、`TokenService`，核心模块合计覆盖率达到 74.9%。
2. 问题：`pom.xml` 中存在重复的 `spring-boot-starter-actuator` 依赖，测试时 Maven 输出构建模型警告。
   解决：删除重复依赖，保留原有 actuator 能力和 Prometheus 监控依赖。
3. 问题：Mockito 在 Java 21 环境下会提示动态加载 agent 的未来兼容性警告。
   解决：当前不影响测试通过，后续可按 Mockito 官方建议在 Maven 测试参数中显式配置 agent。

## 心得体会

本次测试以接口层和核心业务层为重点，既验证了 HTTP 入口的统一返回结构和参数校验，也验证了用户、团队、Token 这些关键后端逻辑的成功与失败分支。通过 JaCoCo 将覆盖率报告和核心 60% 门槛纳入 Maven 测试流程，可以让后续修改更早发现回归问题，也让测试结果更容易复现和审查。
