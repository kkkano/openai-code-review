基于 GitHub Actions + OpenAI(ChatGLM) + Git/GitHub + 公众号模板消息实现，然后串联出从代码提交获取通知，Git 检出分支变化，在使用 OpenAI 进行代码和写入日志，再发送消息通知,完成了一个AI代码评审的功能
写入日志查看：https://github.com/kkkano/openai-code-review-log
微信公众号效果演示：![EC6B76YAX9X2J_RZBMA34 D](https://github.com/user-attachments/assets/8f379984-c035-459e-913b-015f121c6ccd)

## 新增：多提供商接入（OpenAI-compatible）

现在支持通过 YAML 配置切换 LLM 提供商：

- `openai-compatible`：支持 OpenAI 以及兼容 OpenAI Chat Completions 协议的第三方网关
- `chatglm`：兼容原有配置

配置文件：`.github/code-review.yml`
详细说明：`docs/configuration.md`


![image](https://github.com/user-attachments/assets/f557e7bc-108b-42b8-893c-918eb1e27176)
项目流程
![image](https://github.com/user-attachments/assets/380b8e72-e06f-4e19-b643-e63c35e9398a)

代码提交：开发者在GitHub上提交代码变更。
Git检出：GitHub Actions触发，检出最新的代码变更。
代码评审：利用OpenAI（ChatGLM）进行代码评审，识别潜在的代码问题或改进建议。
写入日志：将评审结果写入到指定的日志库中，以便后续跟踪和查询。
## 新增：如何绑定到其他项目

已提供详细文档：`docs/bind-other-projects.md`

快速要点：
- 在目标仓库添加 workflow（push/pull_request 触发）
- 配置目标仓库 Secrets（`OPENAI_*`、`WEIXIN_*`、`CODE_*`）
- 在目标仓库放置 `.github/code-review.yml` 指定 provider/model/prompt

> 这样其他项目 push 时也能自动执行评审并发微信通知。

## Release 与 SDK 是什么？

- **SDK（openai-code-review-sdk）**：真正执行“读 diff → 调模型评审 → 写日志 → 发微信”的 Java 程序（jar）。
- **Release**：把某个版本的 SDK jar 固化发布（像可下载的版本快照），其他项目可以直接下载使用，避免每次都从源码构建。

当前建议：
- 开发调试阶段：优先“从当前分支构建 jar”（保证改动即时生效）
- 稳定后：发布 Release，给多仓库复用同一稳定版本

## 开箱即用（给其他仓库直接接入，按步骤做就能跑）

下面是**最稳妥**的一套接入流程，适合第一次使用：

### 第 1 步：在目标仓库添加 2 个文件

1) 工作流文件：
- 路径：`.github/workflows/code-review.yml`
- 直接复制：`docs/examples/code-review-workflow-template.yml`

2) 评审配置文件：
- 路径：`.github/code-review.yml`
- 直接复制：`docs/examples/code-review.yml`

### 第 2 步：在目标仓库配置 Secrets

进入目标仓库：`Settings -> Secrets and variables -> Actions`，新增：

- `CODE_REVIEW_LOG_URI`（评审日志仓库地址）
- `CODE_TOKEN`（可写日志仓库的 GitHub Token）
- `OPENAI_APIHOST`（如 DeepSeek 网关地址）
- `OPENAI_APIKEY`（LLM API Key）
- `REVIEW_MODEL`（如 `deepseek-chat`）
- `WEIXIN_APPID`
- `WEIXIN_SECRET`
- `WEIXIN_TOUSER`
- `WEIXIN_TEMPLATE_ID`

### 第 3 步：确认模板里的 SDK 版本

在 workflow 模板中有：

- `SDK_VERSION: v1.1.0`

建议先保持这个版本，稳定后再按 release 升级。

### 第 4 步：推送一次代码触发

- push 或发起 pull request
- 到 `Actions` 页面查看 `AI Code Review` / `Build and Run OpenAiCodeReview` 运行状态

### 第 5 步：验收是否成功

成功标志：
- Actions 运行成功；
- 日志仓库出现新的 code review markdown；
- 微信收到模板消息通知。

---

### 常见问题排查

- **没收到微信**：先看日志里是否有 `weixin template message` 和 `errcode`。
- **模型调用失败**：检查 `OPENAI_APIHOST / OPENAI_APIKEY / REVIEW_MODEL`。
- **日志没写入**：检查 `CODE_TOKEN` 权限和 `CODE_REVIEW_LOG_URI`。
- **首次接入建议先在测试仓验证**，通过后再推广到正式仓库。



