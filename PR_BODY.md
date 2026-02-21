# PR 摘要：multi-provider config + DeepSeek + WeChat E2E 修复

## 改动概览
本 PR 让 `openai-code-review` 从单一 ChatGLM 接入升级为 **多提供商（OpenAI-compatible）**，并完成微信通知全链路修复。

### 1) 多提供商能力
- 新增 YAML 配置 + 环境变量覆盖：
  - `ReviewConfig` / `ReviewConfigLoader`
  - 默认读取 `.github/code-review.yml`
- 新增 `OpenAICompatible` 客户端，兼容 OpenAI 协议网关（如 DeepSeek）
- 保留 `chatglm` 兼容路径

### 2) 工作流与可运维性
- workflow 注入 `REVIEW_PROVIDER` / `REVIEW_MODEL` / `OPENAI_*`
- 保留 `CHATGLM_*` 兼容
- WeChat 通知改为“配置存在时才发送”，减少无效失败

### 3) 稳定性修复（本轮关键）
- 修复 review 日志文件名非法字符（分支名/作者名带 `/`、`<>` 等）
- workflow 改为构建**当前分支** SDK Jar（避免下载旧 release 导致代码不一致）
- shaded jar 补齐 YAML 依赖链（`jackson-dataformat-yaml` + `snakeyaml`）
- CI 固定 `openai-compatible` 路径验证

## 验证结果
- ✅ E2E run 成功：`22258085523`
- ✅ review 日志成功写入 log repo
- ✅ 微信模板消息成功发送（`{"errcode":0,"errmsg":"ok"}`）

## 向后兼容
- 保留原有 `CHATGLM_*` 变量逻辑
- 不影响现有架构图和原 README 说明，仅补充新章节

## 风险与后续建议
- 建议新增 `workflow_dispatch` 手动触发入口，方便快速回归验证
- 建议为上游 API 增加 429 重试/退避
