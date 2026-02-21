package plus.gaga.middleware.sdk.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.gaga.middleware.sdk.infrastructure.git.GitCommand;
import plus.gaga.middleware.sdk.infrastructure.openai.IOpenAI;
import plus.gaga.middleware.sdk.infrastructure.weixin.WeiXin;

import java.io.IOException;


public abstract class AbstractOpenAiCodeReviewService implements IOpenAiCodeReviewService {

    private final Logger logger = LoggerFactory.getLogger(AbstractOpenAiCodeReviewService.class);

    protected final GitCommand gitCommand;
    protected final IOpenAI openAI;
    protected final WeiXin weiXin;

    public AbstractOpenAiCodeReviewService(GitCommand gitCommand, IOpenAI openAI, WeiXin weiXin) {
        this.gitCommand = gitCommand;
        this.openAI = openAI;
        this.weiXin = weiXin;
    }

    @Override
    public void exec() {
        try {
            String diffCode = getDiffCode();
            String recommend = codeReview(diffCode);
            String logUrl = recordCodeReview(recommend);
            if (isWeixinConfigured()) {
                pushMessage(logUrl);
            } else {
                logger.warn("weixin env missing, skip weixin notify. logUrl={}", logUrl);
            }
        } catch (Exception e) {
            logger.error("openai-code-review error", e);
        }

    }

    private boolean isWeixinConfigured() {
        return nonEmpty(System.getenv("WEIXIN_APPID"))
                && nonEmpty(System.getenv("WEIXIN_SECRET"))
                && nonEmpty(System.getenv("WEIXIN_TOUSER"))
                && nonEmpty(System.getenv("WEIXIN_TEMPLATE_ID"));
    }

    private boolean nonEmpty(String v) {
        return v != null && !v.trim().isEmpty();
    }

    protected abstract String getDiffCode() throws IOException, InterruptedException;

    protected abstract String codeReview(String diffCode) throws Exception;

    protected abstract String recordCodeReview(String recommend) throws Exception;

    protected abstract void pushMessage(String logUrl) throws Exception;

}
