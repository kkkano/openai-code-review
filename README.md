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

## 新增：可直接复用的 Workflow 模板

已提供示例文件：
- `docs/examples/code-review-workflow-template.yml`（可复制到目标仓库 `.github/workflows/code-review.yml`）
- `docs/examples/code-review.yml`（目标仓库的评审配置示例）

使用方法：
1. 复制 workflow 模板到目标仓库；
2. 按文档配置 secrets（`CODE_*`、`OPENAI_*`、`WEIXIN_*`）；
3. 在目标仓库添加 `.github/code-review.yml`；
4. push 一次代码即可触发评审与微信通知。


