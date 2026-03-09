### 前端代码实现说明 (.md)

# 一、技术栈选型
    核心框架: Vue 3 (Composition API)
    构建工具: Vite
    UI 组件库: Vant 4 (专为移动端设计)
    路由管理: Vue Router 4
    状态管理: Pinia (用于用户信息、Token 存储)
    HTTP 请求: Axios (封装拦截器处理 Token)
    样式预处理: Sass/SCSS

# 二、实现逻辑与组件使用
    2.1 全局配置与基础设置
        主要任务: 引入 Vant 组件库，配置 Axios 拦截器。
        Vant 按需引入: 在 main.js 中自动导入所需组件，减少包体积。
        Axios 拦截器:
        请求拦截: 自动在 Header 中添加 Authorization: Bearer <token>。
        响应拦截: 统一处理 401 (未登录跳转登录页) 和 500 错误提示。
    2.2 核心页面功能详解
        1/2. 认证模块 (Login.vue / Register.vue)
            UI 布局: 使用 van-form, van-field, van-button。
            逻辑:
                表单验证：用户名/密码非空校验，密码长度校验。
                提交后调用 api/user/login 或 api/user/register。
                登录成功将 token 和 userInfo 存入 Pinia 和 localStorage。
                跳转至 Home.vue。
        3. 首页 (Home.vue)
            UI 布局:
                顶部：van-search (搜索框)。
                中部：van-tabs (分类筛选：体育/游戏/学习等)。
                下部：van-list (下拉加载更多) + TeamCard (自定义卡片组件)。
            逻辑:
                监听 Tab 切换和搜索关键词变化，重新请求 api/team/list。
                实现无限滚动加载（当滚动到底部时自动加载下一页）。
                点击卡片跳转至 TeamDetail.vue?id=xxx。
        4. 组队详情页 (TeamDetail.vue)
            UI 布局:
                头部：标题、活动类型标签、剩余人数进度条 (van-progress)。
                内容：详细描述、队长信息卡片。
                成员列表：展示当前已加入成员头像 (van-grid)。
                底部操作栏 (van-action-bar):
                    若当前用户是队长：显示“管理队伍”按钮。
                    若已满员：显示“已满”禁用按钮。
                    否则：显示“申请加入”按钮。
                逻辑:
                    根据 URL 参数 id 获取详情。
                    “申请加入”弹出 van-dialog 确认框，输入申请理由后调用 api/application/create。
        5. 发布组队页 (CreateTeam.vue)
            UI 布局: 
                van-form 包含：
                    标题输入框。
                    活动类型选择器 (van-picker 或 van-radio-group)。
                    最大人数步进器 (van-stepper)。
                    描述多行文本框 (van-field type="textarea")。
            逻辑:
                提交前校验所有字段。
                调用 api/team/create。
                成功后提示并跳转回“我的队伍”或“首页”。
        6. 我的队伍页 (MyTeams.vue)
            UI 布局: 使用 van-tabs 分为两个标签页：
                我创建的: 列表展示，每项带“管理”入口。
                我加入的: 列表展示，显示所在队伍状态。
            逻辑:
                分别调用两个不同的接口或带参数的列表接口获取数据。
        7. 队伍管理页 (TeamManage.vue) - 队长专属
            UI 布局:
                Tab 1: 成员管理 (van-cell-group)，每个成员右侧有“踢出”按钮 (红色)。
                Tab 2: 申请审核 (van-list)，展示待审核申请。
                每条申请显示：申请人昵称、申请留言。
                操作按钮组：van-button (绿色-同意，红色-拒绝)。
            逻辑:
                同意: 调用 api/application/approve -> 成功后刷新列表，触发系统通知。
                拒绝: 调用 api/application/reject。
                踢出: 调用 api/team/removeMember。
                解散: 底部危险操作区，二次确认后调用删除接口。
        8. 申请记录页 (MyApplications.vue)
            UI 布局: 列表展示历史申请。
                状态展示: 使用不同颜色的 Tag 区分状态：
                待审核 (灰色/橙色)
                已同意 (绿色)
                已拒绝 (红色)
            逻辑: 只读列表，无需复杂交互。
        9. 消息通知页 (Notification.vue)
            UI 布局: van-cell 列表。
            逻辑:
                展示系统通知（如：“您的加入申请已被队长同意”）。
                未读消息可加红点标记 (van-badge)。
                点击可标记为已读。
        10. 个人中心 (Profile.vue)
            UI 布局:
                头部：用户头像 (van-image) + 昵称。
                菜单列表 (van-cell-group): 修改资料、消息通知入口、关于我们。
                底部：退出登录按钮。
            逻辑:
                “修改资料”可跳转新页面或使用 van-popup 弹窗表单。
                “退出登录”清除 Pinia 状态和 LocalStorage，跳转 Login.vue。
