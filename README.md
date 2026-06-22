# ydjs

[![CI](https://github.com/JSydzx/ydjs/actions/workflows/ci.yml/badge.svg)](https://github.com/JSydzx/ydjs/actions)
[![Backend Coverage](https://codecov.io/gh/JSydzx/ydjs/branch/main/graph/badge.svg?flag=backend)](https://codecov.io/gh/JSydzx/ydjs)
[![Frontend Coverage](https://codecov.io/gh/JSydzx/ydjs/branch/main/graph/badge.svg?flag=frontend)](https://codecov.io/gh/JSydzx/ydjs)

## 设计背景

校园组队平台（移动端Web）

## 项目介绍

校园组队平台是一个移动端Web应用，旨在为学生提供一个便捷的在线平台，帮助他们快速找到志同道合的队友，组建团队完成各种项目和活动。该平台支持用户创建个人资料、发布组队需求、浏览其他用户的组队信息，并通过智能匹配算法推荐合适的队友。用户还可以通过平台进行沟通和协作，提升团队合作效率。校园组队平台致力于促进学生之间的交流与合作，帮助他们更好地实现学术和兴趣上的目标。

## 功能需求

1. 用户注册与登录：用户可以通过邮箱或社交媒体账号注册和登录平台。
2. 个人资料管理：用户可以创建和编辑个人资料，包括姓名、年级、专业、兴趣爱好等信息。
3. 发布组队需求：用户可以发布组队需求，描述项目或活动的内容、所需技能、人数等信息。
4. 浏览组队信息：用户可以浏览其他用户发布的组队需求，查看详细信息并进行筛选。
5. 队员消息沟通：用户可以通过平台内置的消息系统与其他用户进行沟通，讨论组队细节。

## 团队分工

|  姓名  |                           核心职责                           |
| :----: | :----------------------------------------------------------: |
| 肖云志 |          需求分析、系统交互设计、前端代码、文档编写          |
| 郑嘉玮 | API设计、数据库设计、后端代码、前端代码、联调、测试、CI/CD、部署、监控、文档编写 |

## 技术栈

### 前端

- **框架**: Vue 3
- **语言**: TypeScript
- **构建工具**: Vite
- **UI 组件库**: Vant 4
- **路由**: Vue Router
- **状态管理**: Pinia
- **HTTP 客户端**: Axios

### 后端

- **框架**: Java + Spring Boot 3.5
- **语言**: Java 17
- **构建工具**: Maven
- **数据库**: MySQL 8.0
- **ORM**: MyBatis / MyBatis-Spring-Boot-Starter
- **安全**: Spring Security Crypto + TokenService
- **监控**: Spring Actuator + Micrometer + Prometheus

### 开发工具 & 基础设施

- **容器化**: Docker + Docker Compose
- **代码规范**: ESLint（前端）, Lombok + MyBatis 规范（后端）
- **前端测试**: Vitest + Vue Test Utils + jsdom
- **后端测试**: JUnit + Spring Boot Test + JaCoCo
- **CI/CD**: GitHub Actions（ci, docker, coverage, security）
- **代码覆盖率**: Codecov

## 项目结构

```
ydjs/
├── frontend/                         # 前端代码（Vue 3）
│   ├── src/
│   │   ├── api/                      # API 接口封装
│   │   ├── assets/                   # 静态资源
│   │   ├── router/                   # 路由配置
│   │   ├── stores/                   # Pinia 状态管理
│   │   ├── views/                    # 页面视图
│   │   ├── __tests__/                # 测试文件
│   │   ├── App.vue                   # 根组件
│   │   ├── main.ts                   # 入口文件
│   │   └── style.css                 # 全局样式
│   ├── public/                       # 公共静态资源
│   ├── index.html
│   ├── package.json
│   └── vite.config.ts
├── backend/                          # 后端代码（Spring Boot）
│   └── teamplatform/
│       ├── src/main/java/com/zjgsu/teamplatform/
│       │   ├── controller/           # 控制器
│       │   ├── entity/               # 实体类
│       │   ├── mapper/               # MyBatis Mapper 接口
│       │   ├── service/              # 业务逻辑接口
│       │   │   └── impl/             # 业务逻辑实现
│       │   ├── dto/                  # 数据传输对象
│       │   ├── vo/                   # 视图对象
│       │   ├── config/               # 配置类
│       │   ├── security/             # 安全相关（TokenService等）
│       │   ├── common/               # 公共类
│       │   └── exception/            # 异常处理
│       ├── src/main/resources/
│       │   ├── application.properties # 应用配置
│       │   └── schema.sql            # 数据库初始化脚本
│       ├── src/test/                 # 测试代码
│       ├── pom.xml                   # Maven 配置
│       └── mvnw                      # Maven Wrapper
├── docs/                             # 项目文档
│   ├── api.md / api.yaml             # API 文档
│   ├── architecture.md               # 架构文档
│   ├── database.md                   # 数据库设计
│   ├── design-spec.md                # 设计规范
│   ├── monitoring.md                 # 监控文档
│   ├── security-review.md            # 安全审计
│   └── contributions/                # 贡献文档
├── .github/workflows/                # CI/CD 工作流
│   ├── ci.yml                        # 全栈 CI（前端 + 后端）
│   ├── docker.yml                    # Docker 构建与推送
│   ├── coverage.yml                  # 覆盖率上报
│   └── security.yml                  # 安全扫描
├── compose.yaml                      # Docker 开发环境
├── compose.prod.yaml                 # Docker 生产环境
├── .gitignore
├── README.md
└── package.json                      # 根级 Codecov 配置
```

## 快速开始

### 开发环境（Docker Compose）

```bash
# 启动全部服务（前端 + 后端 + MySQL）
docker compose up -d

# 前端运行在 http://localhost:5173
# 后端运行在 http://localhost:8765
```

### 本地开发

**前端：**

```bash
cd frontend
npm ci
npm run dev
```

**后端：**

```bash
cd backend/teamplatform
./mvnw spring-boot:run
```

需提前启动 MySQL 实例，或通过 Docker Compose 单独启动数据库：

```bash
docker compose up -d db
```

### 测试

```bash
# 前端测试
cd frontend && npm test

# 后端测试
cd backend/teamplatform && ./mvnw test
```
