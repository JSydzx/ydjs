 检查内容： 

1.注入漏洞（SQL / 命令注入） 

2.失效的访问控制（未鉴权的接口） 

3.硬编码密钥或敏感信息 

4.密码是否明文存储

5.错误信息是否暴露内部细节 

对每个发现的问题：

- 说明漏洞类型和危害等级（高/中/低）
- 提供修复后的完整代码

---

  Summary

  #: 1
  Severity: HIGH
  Category: Broken Authentication
  File(s): UserController.java, user.ts, all controllers
  Issue: No server-side auth; X-User-Id header is forgeable; fake client-side token
  ────────────────────────────────────────
  #: 2
  Severity: MEDIUM
  Category: PII Exposure
  File(s): TeamController.java:91, TeamServiceImpl.java:160
  Issue: Team member emails exposed without authentication
  ────────────────────────────────────────
  #: 3
  Severity: MEDIUM
  Category: Hardcoded Credentials
  File(s): application.properties:5-6
  Category: PII Exposure
  File(s): TeamController.java:91, TeamServiceImpl.java:160
  Issue: Team member emails exposed without authentication
  ────────────────────────────────────────
  #: 3
  Severity: MEDIUM
  Category: Hardcoded Credentials
  File(s): application.properties:5-6
  Issue: Database password committed in plaintext
  ────────────────────────────────────────
  #: 4
  Severity: MEDIUM
  Category: CORS Misconfiguration
  File(s): WebConfig.java:12-16
  Issue: allowCredentials(true) with wildcard origin
  ────────────────────────────────────────
  #: 5
  Severity: MEDIUM
  Category: Information Leakage
  File(s): GlobalExceptionHandler.java:46-48
  Issue: Raw exception messages returned to client

  Follow-up note: Finding 1 (broken authentication) is the root cause that amplifies all other access control issues.
  Once proper JWT-based authentication is implemented, the X-User-Id header should be removed entirely, as user identity
   should be derived solely from the verified JWT token.

