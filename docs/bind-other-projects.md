# 绑定到其他项目（Push 即触发代码评审+微信通知）

你有两种方式：

## 方式 A：直接复制工作流（最快）
在目标项目新增 `.github/workflows/code-review.yml`，核心步骤：

1. checkout 代码
2. 构建 `openai-code-review-sdk`（或下载 release 包）
3. 设置环境变量并运行 jar

最小配置（Secrets）：
- `CODE_REVIEW_LOG_URI`
- `CODE_TOKEN`
- `OPENAI_APIHOST`
- `OPENAI_APIKEY`
- `REVIEW_MODEL`
- `WEIXIN_APPID`
- `WEIXIN_SECRET`
- `WEIXIN_TOUSER`
- `WEIXIN_TEMPLATE_ID`

并在目标项目放置：`.github/code-review.yml`

```yaml
provider: openai-compatible
apiHost: https://api.deepseek.com/v1/chat/completions
apiKey: ${OPENAI_APIKEY}
authScheme: Bearer
model: deepseek-chat
promptTemplate: |
  你是资深代码审查工程师，请按 Summary / Risks / Fixes / Tests 输出。
```

## 方式 B：发布 SDK 后复用（推荐长期）
- 在 `openai-code-review` 仓库发布 release（包含 fat jar）
- 其他项目 workflow 直接下载该 jar
- 统一升级版本即可全项目生效

## 要不要改配置文件？
要。至少要改两处：
1) 目标项目的 GitHub Secrets（API/微信/日志仓库）
2) 目标项目的 `.github/code-review.yml`（provider、model、prompt）

这样目标项目每次 push/pull_request 都能触发并收到微信通知。
