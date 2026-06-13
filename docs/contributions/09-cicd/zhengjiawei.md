# CI/CD 配置贡献说明

姓名：郑嘉玮  学号：2312190119 角色：前后端  日期：2026-06-13

## 完成的工作

### 工作流相关

- [x] 参与编写并维护 `.github/workflows/ci.yml`

- [x] 配置后端 Spring Boot 自动化测试流程
- [x] 配置前端依赖安装、Lint 检查和单元测试流程
- [x] 升级 GitHub Actions 版本，适配 Node.js 24 运行环境
- [x] 移除 `FORCE_JAVASCRIPT_ACTIONS_TO_NODE24` 强制兼容配置，避免旧版 Action 运行时告警

### Docker 发布流程

- [x] 维护 `.github/workflows/docker.yml`
- [x] 配置 GHCR 登录、后端镜像构建与推送
- [x] 修复后端 Docker 构建上下文路径，确保使用 `backend/teamplatform`
- [x] 配置 Docker Buildx，支持 GitHub Actions 缓存后端
- [x] 修复 Trivy Action 版本不可解析问题
- [x] 配置镜像漏洞扫描流程，输出安全扫描结果

### 代码适配

- [x] 调整 ESLint 配置，使其匹配当前 Vue 项目代码风格
- [x] 修复 `npm run lint --prefix frontend` 在 CI 中失败的问题
- [x] 保证前端测试命令与本地开发命令一致

### 验证结果

- [x] `npm run lint --prefix frontend` 通过
- [x] `npm run test --prefix frontend` 通过
- [x] `npm run build --prefix frontend` 通过
- [x] `.\mvnw.cmd test` 通过
- [x] `docker compose -f compose.prod.yaml config` 通过

## 相关文件

- `.github/workflows/ci.yml`
- `.github/workflows/docker.yml`
- `.github/workflows/coverage.yml`
- `.github/workflows/security.yml`
- `frontend/eslint.config.js`

## CI 运行链接

- CI：<https://github.com/JSydzx/ydjs/actions/runs/27459979025>

- Docker 构建：http://github.com/JSydzx/ydjs/actions/runs/27459979021

  

