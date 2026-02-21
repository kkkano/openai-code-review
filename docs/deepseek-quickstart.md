# DeepSeek 快速启用指南

## 1) GitHub Secrets（仓库 Settings -> Secrets and variables -> Actions）

必填：
- `OPENAI_APIHOST` = `https://api.deepseek.com/chat/completions`
- `OPENAI_APIKEY` = 你的 DeepSeek key（例如 `sk-***`）
- `REVIEW_PROVIDER` = `openai-compatible`
- `REVIEW_MODEL` = `deepseek-chat`（或 `deepseek-reasoner`）
- `CODE_REVIEW_LOG_URI` = 你的日志仓库 URL（不带 `.git`）
- `CODE_TOKEN` = 有推送日志仓库权限的 token

可选（如不需要微信通知可不填）：
- `WEIXIN_APPID`
- `WEIXIN_SECRET`
- `WEIXIN_TOUSER`
- `WEIXIN_TEMPLATE_ID`

## 2) 配置文件
默认读取 `.github/code-review.yml`，可保留：

```yaml
provider: openai-compatible
apiHost: https://api.openai.com/v1/chat/completions
apiKey: ${OPENAI_APIKEY}
authScheme: Bearer
model: gpt-4o-mini
promptTemplate: |
  你是资深代码审查工程师。请基于 git diff 输出结构化结果：
  1) Summary
  2) Risks（Critical/High/Medium/Low）
  3) Actionable Fixes
  4) Tests to Add
```

> 实际运行时会优先使用 Secrets 覆盖（`OPENAI_APIHOST/OPENAI_APIKEY/REVIEW_MODEL`）。

## 3) 触发工作流
- 向 `master` 或 `master-close` 推送一次提交（按 workflow 规则）
- 查看 Actions 日志确认 code review 运行成功

## 4) 常见问题
- `value is null`：检查必要 Secrets 是否配置
- 401/403：检查 key 是否可用、host 是否正确
- 日志仓库写入失败：检查 `CODE_TOKEN` 权限

