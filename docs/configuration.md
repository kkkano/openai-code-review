# OpenAI Code Review Config

This project now supports **multi-provider OpenAI-compatible APIs** via YAML config.

## Config File

- Path: `.github/code-review.yml`
- Override with env: `REVIEW_CONFIG_FILE=/path/to/config.yml`

### Example

```yaml
provider: openai-compatible
apiHost: https://api.openai.com/v1/chat/completions
apiKey: ${OPENAI_APIKEY}
authScheme: Bearer
model: gpt-4o-mini
promptTemplate: |
  你是资深代码审查工程师，请按 Summary / Risks / Fixes / Tests 输出。
```

## Supported Providers

- `openai-compatible` (default): works with OpenAI and most 3rd-party OpenAI-compatible gateways
- `chatglm`: keeps backward compatibility with previous setup

## Env Overrides

- `REVIEW_PROVIDER`
- `REVIEW_MODEL`
- `REVIEW_PROMPT_TEMPLATE`
- `OPENAI_APIHOST` / `CHATGLM_APIHOST`
- `OPENAI_APIKEY` / `CHATGLM_APIKEYSECRET`
- `OPENAI_AUTH_SCHEME` (default: `Bearer`)

## Notes

- Existing WeChat notify + Git log flow remains unchanged.
- No hardcoded API key in code.
