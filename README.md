基于 GitHub Actions + OpenAI(ChatGLM) + Git/GitHub + 公众号模板消息实现，然后串联出从代码提交获取通知，Git 检出分支变化，在使用 OpenAI 进行代码和写入日志，再发送消息通知,完成了一个AI代码评审的功能
![image](https://github.com/user-attachments/assets/f557e7bc-108b-42b8-893c-918eb1e27176)
项目流程
![image](https://github.com/user-attachments/assets/380b8e72-e06f-4e19-b643-e63c35e9398a)

代码提交：开发者在GitHub上提交代码变更。
Git检出：GitHub Actions触发，检出最新的代码变更。
代码评审：利用OpenAI（ChatGLM）进行代码评审，识别潜在的代码问题或改进建议。
写入日志：将评审结果写入到指定的日志库中，以便后续跟踪和查询。
消息通知：通过微信公众号发送模板消息，通知相关人员代码评审的结果和变更详情。
