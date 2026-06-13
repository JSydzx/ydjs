# Docker 部署贡献说明

姓名：郑嘉玮
学号：2312190119
日期：2026-05-17

## 我完成的工作

###  安全审查

- 审查了哪些文件/模块：重点审查了后端用户、团队、邀请、加入申请、通知、聊天等接口层与业务层，包含 `UserController.java`、`TeamController.java`、`JoinRequestController.java`、`InvitationController.java`、`NotificationController.java`、`ChatController.java`、`TeamChatController.java`、`UserServiceImpl.java`、`TeamServiceImpl.java`、`WebConfig.java`、`AuthInterceptor.java`、`TokenService.java`，同时检查了前端 `frontend/src/api/http.ts`、`frontend/src/stores/user.ts`、各接口封装文件、`.github/workflows/security.yml`、`.github/workflows/docker.yml`、`compose.prod.yaml` 和 `docs/security-review.md`。
- AI 发现的主要问题：后端接口最初依赖可伪造的 `X-User-Id` 请求头，存在失效鉴权风险；用户密码需要避免明文存储；数据库密码等敏感信息不能直接写入配置；部分团队成员信息在未登录场景下可能暴露；CORS 配置需要兼容前后端分离同时避免固定写死来源；全局异常返回原始异常信息会带来内部细节泄露风险。

### 安全检查清单

- [x] 注入漏洞检查：后端主要通过 MyBatis 参数绑定和 Mapper 方法传参访问数据库，未发现拼接用户输入执行 SQL 或命令的实现。
- [x] 失效访问控制检查：新增 `AuthInterceptor` 统一拦截受保护接口，登录后由 Bearer Token 解析当前用户 ID，替代前端可伪造的 `X-User-Id`。
- [x] 硬编码密钥或敏感信息检查：生产 Docker Compose 使用 `SPRING_DATASOURCE_PASSWORD_FILE` 和 Docker secrets 读取数据库密码，CI 中使用 GitHub Secrets 保存 `GITHUB_TOKEN`、`GITLEAKS_LICENSE` 等敏感值。
- [x] 密码存储检查：`UserServiceImpl` 在注册时使用 `BCryptPasswordEncoder` 加密密码，登录时使用 `matches` 校验，避免明文密码入库。
- [x] 错误信息检查：识别出 `GlobalExceptionHandler` 直接返回异常消息的问题，已在安全审查文档中记录为后续需要收敛的风险点。
- [x] 前端鉴权检查：统一 `frontend/src/api/http.ts` 的 `baseURL`，请求拦截器自动附加 `Authorization: Bearer <token>`，401 响应时清理本地登录状态并跳转登录。
- [x] 容器安全检查：后端、前端使用多阶段 Dockerfile，生产 Compose 增加健康检查和资源限制，镜像发布后通过 Trivy 扫描高危和严重漏洞。

### CI 安全扫描

- 选项 A：密钥泄露扫描。维护 `.github/workflows/security.yml`，在 push 和 pull request 时通过 `gitleaks/gitleaks-action@v3` 扫描 Git 历史和当前代码，`actions/checkout@v6` 配置 `fetch-depth: 0` 保证扫描完整提交历史，许可证通过 `secrets.GITLEAKS_LICENSE` 注入，避免明文提交。

### 前后端联调

- 删除假数据，改为从后端获取：前端团队、加入申请、通知、个人资料等页面改为调用后端真实接口，减少本地模拟数据造成的权限和数据一致性问题。
- 统一前端请求 baseUrl：通过 `frontend/src/api/http.ts` 统一设置 `baseURL: '/api'`、超时时间、请求头、响应解包和错误处理，避免各页面分散维护接口地址。
- 接入登录态与鉴权联调：登录接口返回后端签发的 Token，前端保存 Token 并在后续请求中自动携带 Bearer Token；后端拦截器校验 Token 后把当前用户 ID 写入请求上下文，控制器和业务层不再信任前端传入的用户身份。
- 使用 ui-ux-pro-max skills 美化前端页面：在完成真实接口联调后，调整首页、个人资料、发布、团队详情等页面展示，使接口数据、登录状态和错误提示在页面中更清晰可用。
- 为前端添加统一的hover效果
