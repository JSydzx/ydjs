## API 接口

### 用户相关

- POST /api/user/register - 用户注册 (包括 nickname)
- POST /api/user/login - 用户登录
- GET /api/user/profile - 获取用户信息 (包括 nickname, avatar)
- PUT /api/user/profile - 更新用户信息

### 团队相关

- POST /api/team/create - 发起组队 (创建团队，包括 tag)
- GET /api/team/list - 查看组队列表
- GET /api/team/{id} - 查看组队详情 (包括 tag)
- GET /api/team/my - 查看我的团队
- PUT /api/team/{id} - 更新团队信息
- DELETE /api/team/{id} - 删除团队 (仅创建者)

### 团队成员相关

- GET /api/team/{id}/members - 查看团队成员列表
- POST /api/team/{id}/members - 添加成员 (通过批准加入请求)
- DELETE /api/team/{id}/members/{userId} - 移除成员 (管理员权限)

### 加入请求相关

- POST /api/join/request - 加入组队 (发送加入请求，包括 reason)
- GET /api/join/requests - 查看我的加入请求
- GET /api/team/{id}/join-requests - 查看团队的加入请求 (管理员权限)
- POST /api/join/{requestId}/approve - 批准加入请求
- POST /api/join/{requestId}/reject - 拒绝加入请求

### 邀请相关

- POST /api/invitation/send - 发送邀请
- GET /api/invitation/list - 查看我的邀请
- GET /api/team/{id}/invitations - 查看团队的邀请 (管理员权限)
- POST /api/invitation/{id}/accept - 接受邀请
- POST /api/invitation/{id}/reject - 拒绝邀请

### 通知相关

- GET /api/notification/list - 查看通知列表 (包括 type)
- POST /api/notification/{id}/read - 标记通知为已读
- DELETE /api/notification/{id} - 删除通知