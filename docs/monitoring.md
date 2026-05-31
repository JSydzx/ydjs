# 监控配置说明 

## 1. 日志 

- 格式：JSON 结构化 

- 输出：控制台 + logs/app.log 
- 级别：INFO 

## 2. 健康检查 

- 端点：/api/health 
- 返回：status、timestamp、version 

## 3. 指标 

- 地址：/actuator/prometheus 

- 包含：请求数、响应时间、错误率

