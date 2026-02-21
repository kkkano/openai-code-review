package plus.gaga.middleware.sdk.domain.service.impl;


import plus.gaga.middleware.sdk.domain.service.AbstractOpenAiCodeReviewService;
import plus.gaga.middleware.sdk.infrastructure.git.GitCommand;
import plus.gaga.middleware.sdk.infrastructure.openai.IOpenAI;
import plus.gaga.middleware.sdk.infrastructure.openai.dto.ChatCompletionRequestDTO;
import plus.gaga.middleware.sdk.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;
import plus.gaga.middleware.sdk.infrastructure.weixin.WeiXin;
import plus.gaga.middleware.sdk.infrastructure.weixin.dto.TemplateMessageDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpenAiCodeReviewService extends AbstractOpenAiCodeReviewService {

    private final String reviewModel;
    private final String promptTemplate;

    private static final String DEFAULT_PROMPT_TEMPLATE =
            "你是资深代码审查工程师。请基于以下 git diff 输出结构化评审：\n" +
                    "1) Summary（本次改动摘要）\n" +
                    "2) Risks（按 Critical/High/Medium/Low 列出，含原因）\n" +
                    "3) Actionable Fixes（可执行修复建议，尽量给出示例）\n" +
                    "4) Tests（建议补充的测试点）\n" +
                    "要求：聚焦真实问题，避免空泛表述。";

    public OpenAiCodeReviewService(GitCommand gitCommand, IOpenAI openAI, WeiXin weiXin) {
        this(gitCommand, openAI, weiXin, "glm-4-flash", null);
    }

    public OpenAiCodeReviewService(GitCommand gitCommand, IOpenAI openAI, WeiXin weiXin, String reviewModel, String promptTemplate) {
        super(gitCommand, openAI, weiXin);
        this.reviewModel = reviewModel;
        this.promptTemplate = (promptTemplate == null || promptTemplate.trim().isEmpty()) ? DEFAULT_PROMPT_TEMPLATE : promptTemplate;
    }

    @Override
    protected String getDiffCode() throws IOException, InterruptedException {
        return gitCommand.diff();
    }

    @Override
    protected String codeReview(String diffCode) throws Exception {
        ChatCompletionRequestDTO chatCompletionRequest = new ChatCompletionRequestDTO();
        chatCompletionRequest.setModel(reviewModel);
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequestDTO.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(new ChatCompletionRequestDTO.Prompt("system", promptTemplate));
                add(new ChatCompletionRequestDTO.Prompt("user", "请评审以下 git diff：\n\n" + diffCode));
            }
        });

        ChatCompletionSyncResponseDTO completions = openAI.completions(chatCompletionRequest);
        ChatCompletionSyncResponseDTO.Message message = completions.getChoices().get(0).getMessage();
        return message.getContent();
    }

    @Override
    protected String recordCodeReview(String recommend) throws Exception {
        return gitCommand.commitAndPush(recommend);
    }

    @Override
    protected void pushMessage(String logUrl) throws Exception {
        Map<String, Map<String, String>> data = new HashMap<>();
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.REPO_NAME, gitCommand.getProject());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.BRANCH_NAME, gitCommand.getBranch());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.COMMIT_AUTHOR, gitCommand.getAuthor());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.COMMIT_MESSAGE, gitCommand.getMessage());
        weiXin.sendTemplateMessage(logUrl, data);
    }

}
