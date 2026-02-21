package plus.gaga.middleware.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.gaga.middleware.sdk.domain.service.impl.OpenAiCodeReviewService;
import plus.gaga.middleware.sdk.infrastructure.git.GitCommand;
import plus.gaga.middleware.sdk.infrastructure.openai.IOpenAI;
import plus.gaga.middleware.sdk.infrastructure.openai.impl.ChatGLM;
import plus.gaga.middleware.sdk.infrastructure.openai.impl.OpenAICompatible;
import plus.gaga.middleware.sdk.infrastructure.weixin.WeiXin;
import plus.gaga.middleware.sdk.types.config.ReviewConfig;
import plus.gaga.middleware.sdk.types.config.ReviewConfigLoader;

public class OpenAiCodeReview {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiCodeReview.class);

    public static void main(String[] args) throws Exception {
        ReviewConfig reviewConfig = ReviewConfigLoader.load();

        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );

        WeiXin weiXin = new WeiXin(
                getEnv("WEIXIN_APPID"),
                getEnv("WEIXIN_SECRET"),
                getEnv("WEIXIN_TOUSER"),
                getEnv("WEIXIN_TEMPLATE_ID")
        );

        IOpenAI openAI = buildOpenAiClient(reviewConfig);

        OpenAiCodeReviewService openAiCodeReviewService = new OpenAiCodeReviewService(
                gitCommand,
                openAI,
                weiXin,
                reviewConfig.getModel(),
                reviewConfig.getPromptTemplate()
        );
        openAiCodeReviewService.exec();

        logger.info("openai-code-review done! provider={} model={}", reviewConfig.getProvider(), reviewConfig.getModel());
    }

    private static IOpenAI buildOpenAiClient(ReviewConfig reviewConfig) {
        String provider = reviewConfig.getProvider() == null ? "" : reviewConfig.getProvider().trim().toLowerCase();
        switch (provider) {
            case "chatglm":
                return new ChatGLM(reviewConfig.getApiHost(), reviewConfig.getApiKey());
            case "openai-compatible":
            case "openai":
            default:
                return new OpenAICompatible(reviewConfig.getApiHost(), reviewConfig.getApiKey(), reviewConfig.getAuthScheme());
        }
    }

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null: " + key);
        }
        return value;
    }

}
