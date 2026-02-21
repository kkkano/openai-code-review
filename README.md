基于 GitHub Actions + OpenAI(ChatGLM) + Git/GitHub + 公众号模板消息实现，然后串联出从代码提交获取通知，Git 检出分支变化，在使用 OpenAI 进行代码和写入日志，再发送消息通知,完成了一个AI代码评审的功能
写入日志查看：https://github.com/kkkano/openai-code-review-log
微信公众号效果演示：![EC6B76YAX9X2J_RZBMA34 D](https://github.com/user-attachments/assets/8f379984-c035-459e-913b-015f121c6ccd)

## 3 分钟开箱清单（Checklist）

给新仓库接入时，按下面打钩即可：

- [ ] 复制 `docs/examples/code-review-workflow-template.yml` 到目标仓库 `.github/workflows/code-review.yml`
- [ ] 复制 `docs/examples/code-review.yml` 到目标仓库 `.github/code-review.yml`
- [ ] 在目标仓库配置 Secrets：`CODE_REVIEW_LOG_URI`、`CODE_TOKEN`、`OPENAI_APIHOST`、`OPENAI_APIKEY`、`REVIEW_MODEL`、`WEIXIN_APPID`、`WEIXIN_SECRET`、`WEIXIN_TOUSER`、`WEIXIN_TEMPLATE_ID`
- [ ] 确认 workflow 里的 `SDK_VERSION`（当前推荐：`v1.1.0`）
- [ ] push 一次代码或提一个 PR，触发 Actions
- [ ] 验收 3 件事：Action 成功 / 日志仓有新 markdown / 微信收到消息

> 详细图文步骤见下方「开箱即用」章节与 `docs/bind-other-projects.md`。

## 一键复制区块（直接贴到你的仓库）

### 1) `.github/workflows/code-review.yml`

```yaml
name: AI Code Review (OpenAI-compatible + WeChat)

on:
  push:
    branches: ["**"]
  pull_request:
    branches: ["**"]

permissions:
  contents: read

jobs:
  review:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4
        with:
          fetch-depth: 2

      - name: Setup JDK 11
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "11"

      - name: Download openai-code-review SDK jar
        env:
          SDK_VERSION: v1.1.0
        run: |
          mkdir -p libs
          curl -L -o libs/openai-code-review-sdk.jar \
            "https://github.com/kkkano/openai-code-review/releases/download/${SDK_VERSION}/openai-code-review-sdk-1.0.jar"

      - name: Resolve commit metadata
        run: |
          echo "REPO_NAME=${GITHUB_REPOSITORY##*/}" >> $GITHUB_ENV
          echo "BRANCH_NAME=${GITHUB_HEAD_REF:-${GITHUB_REF#refs/heads/}}" >> $GITHUB_ENV
          echo "COMMIT_AUTHOR=$(git log -1 --pretty=format:'%an <%ae>')" >> $GITHUB_ENV
          echo "COMMIT_MESSAGE=$(git log -1 --pretty=format:'%s')" >> $GITHUB_ENV

      - name: Run AI Code Review
        run: java -jar ./libs/openai-code-review-sdk.jar
        env:
          GITHUB_REVIEW_LOG_URI: ${{ secrets.CODE_REVIEW_LOG_URI }}
          GITHUB_TOKEN: ${{ secrets.CODE_TOKEN }}

          COMMIT_PROJECT: ${{ env.REPO_NAME }}
          COMMIT_BRANCH: ${{ env.BRANCH_NAME }}
          COMMIT_AUTHOR: ${{ env.COMMIT_AUTHOR }}
          COMMIT_MESSAGE: ${{ env.COMMIT_MESSAGE }}

          WEIXIN_APPID: ${{ secrets.WEIXIN_APPID }}
          WEIXIN_SECRET: ${{ secrets.WEIXIN_SECRET }}
          WEIXIN_TOUSER: ${{ secrets.WEIXIN_TOUSER }}
          WEIXIN_TEMPLATE_ID: ${{ secrets.WEIXIN_TEMPLATE_ID }}

          REVIEW_CONFIG_FILE: .github/code-review.yml
          REVIEW_PROVIDER: openai-compatible
          REVIEW_MODEL: ${{ secrets.REVIEW_MODEL }}

          OPENAI_APIHOST: ${{ secrets.OPENAI_APIHOST }}
          OPENAI_APIKEY: ${{ secrets.OPENAI_APIKEY }}
          OPENAI_AUTH_SCHEME: Bearer
```

### 2) `.github/code-review.yml`

```yaml
provider: openai-compatible
apiHost: https://api.deepseek.com/v1/chat/completions
apiKey: ${OPENAI_APIKEY}
authScheme: Bearer
model: deepseek-chat
promptTemplate: |
  你是资深代码审查工程师。
  请基于本次提交 diff 输出：
  1) Summary（做了什么）
  2) Risks（潜在风险）
  3) Fixes（可执行修复建议）
  4) Tests（建议补充的测试）
  要求：简洁、可落地、按优先级排序。
```

### 3) 必须配置的 GitHub Secrets

- `CODE_REVIEW_LOG_URI`
- `CODE_TOKEN`
- `OPENAI_APIHOST`
- `OPENAI_APIKEY`
- `REVIEW_MODEL`
- `WEIXIN_APPID`
- `WEIXIN_SECRET`
- `WEIXIN_TOUSER`
- `WEIXIN_TEMPLATE_ID`

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



