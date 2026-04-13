## 前端目录结构说明

本项目采用 **Vue 3 + Vite + Vant 4** 技术栈。

```text
campus-team-platform/
├── public/                      # 静态资源目录（不参与打包处理）
│   ├── favicon.ico              # 网站图标
│   └── logo.png                 # 应用 Logo
│
├── src/                         # 源代码目录
│   ├── api/                     # API 接口管理模块
│   │   ├── index.js             # Axios 实例统一导出
│   │   ├── user.js              # 用户模块接口 (登录/注册/信息)
│   │   ├── team.js              # 组队模块接口 (发布/列表/详情/管理)
│   │   ├── application.js       # 申请模块接口 (申请/审核)
│   │   └── notification.js      # 消息通知接口
│   │
│   ├── assets/                  # 项目资源文件
│   │   ├── images/              # 图片资源 (空状态图, 引导页图等)
│   │   └── styles/              # 全局样式
│   │       ├── variables.scss   # SCSS 变量 (主题色, 字体大小)
│   │       └── global.scss      # 全局重置样式
│   │
│   ├── components/              # 公共通用组件
│   │   ├── TeamCard.vue         # [核心] 组队卡片组件
│   │   ├── StatusTag.vue        # [核心] 状态标签组件
│   │   ├── EmptyState.vue       # 空数据占位组件
│   │   └── LoadingSpinner.vue   # 自定义加载动画
│   │
│   ├── layouts/                 # 布局组件
│   │   ├── MainLayout.vue       # 主布局 (包含底部 TabBar 导航)
│   │   └── AuthLayout.vue       # 认证布局 (无底部导航)
│   │
│   ├── router/                  # 路由配置
│   │   ├── index.js             # 路由入口及守卫逻辑
│   │   └── routes.js            # 路由表定义
│   │
│   ├── stores/                  # Pinia 状态管理
│   │   ├── index.js             # Store 注册入口
│   │   └── user.js              # 用户状态管理
│   │
│   ├── utils/                   # 工具函数库
│   │   ├── request.js           # Axios 封装
│   │   ├── constants.js         # 常量定义
│   │   └── validate.js          # 表单验证规则
│   │
│   ├── views/                   # 页面视图组件
│   │   ├── auth/                # 认证相关
│   │   │   ├── Login.vue        # 1. 登录页
│   │   │   └── Register.vue     # 2. 注册页
│   │   ├── home/                # 首页相关
│   │   │   ├── Home.vue         # 3. 首页
│   │   │   └── TeamDetail.vue   # 4. 组队详情页
│   │   ├── team/                # 组队管理
│   │   │   ├── CreateTeam.vue   # 5. 发布组队页
│   │   │   ├── MyTeams.vue      # 6. 我的队伍页
│   │   │   └── TeamManage.vue   # 7. 队伍管理页
│   │   ├── application/         # 申请相关
│   │   │   └── MyApplications.vue # 8. 申请记录页
│   │   ├── message/             # 消息相关
│   │   │   └── Notification.vue # 9. 消息通知页
│   │   └── profile/             # 个人中心
│   │       └── Profile.vue      # 10. 个人中心
│   │
│   ├── App.vue                  # 根组件
│   └── main.js                  # 项目入口文件
│
├── .env                         # 环境变量配置
├── .gitignore                   # Git 忽略文件配置
├── index.html                   # HTML 模板
├── package.json                 # 项目依赖配置
├── vite.config.js               # Vite 构建配置
└── README.md                    # 项目说明文档
