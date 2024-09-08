curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiYzc4ZmJhY2QzZTEwMTE4YWQ1NjQ5ZDdhNTRhM2ExNjMiLCJleHAiOjE3MjIwNzczMTQxOTgsInRpbWVzdGFtcCI6MTcyMjA3NTUxNDIxMH0.CdDjJUs8XFUqZnjY08ffaoddLOtoCBn17vmttzkvM0I" \
        -H "Content-Type: application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -d '{
          "model":"glm-4",
          "stream": "true",
          "messages": [
              {
                  "role": "user",
                  "content": "1+1"
              }
          ]
        }' \
  https://open.bigmodel.cn/api/paas/v4/chat/completions