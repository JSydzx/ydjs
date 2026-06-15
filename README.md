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

|  姓名  |                  核心职责                  |
| :----: | :----------------------------------------: |
| 肖云志 | 需求分析、系统交互设计、前端代码、文档编写 |
| 郑嘉玮 |  后端代码，数据库设计，API设计，文档编写   |

## 技术栈

### 前端

- **框架**: React 18
- **语言**: TypeScript
- **构建工具**: Vite
- **样式**: Tailwind CSS 3
- **路由**: React Router
- **状态管理**: React Context / Zustand
- **图标**: Lucide React

### 后端

- **框架**: Node.js + Express
- **数据库**: MongoDB
- **ORM**: Mongoose
- **认证**: JWT (JSON Web Token)
- **API文档**: Swagger/OpenAPI

### 开发工具

- **代码规范**: ESLint + Prettier
- **测试**: Jest + Supertest
- **CI/CD**: GitHub Actions
- **代码覆盖率**: Codecov

## 项目结构

```
ydjs/
├── frontend/ # 前端代码
│   ├── src/
│   │   ├── components/ # 组件
│   │   ├── pages/ # 页面
│   │   ├── routes/ # 路由配置
│   │   ├── store/ # 状态管理
│   │   ├── services/ # API服务
│   │   ├── styles/ # 样式文件
│   │   └── utils/ # 工具函数
│   ├── public/ # 静态资源
│   ├── index.html
│   ├── package.json
│   └── vite.config.ts
├── backend/ # 后端代码
│   ├── src/
│   │   ├── controllers/ # 控制器
│   │   ├── models/ # 数据模型
│   │   ├── routes/ # 路由
│   │   ├── middleware/ # 中间件
│   │   ├── services/ # 业务逻辑
│   │   └── config/ # 配置文件
│   ├── package.json
│   └── server.js
├── .github/ # GitHub配置
│   └── workflows/ # CI/CD工作流
├── .gitignore
├── README.md
└── package.json
```
