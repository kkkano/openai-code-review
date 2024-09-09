curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiN2VjOTY5ZDUzOTZlMzMxZGFlNjIzODgwZjQ2MzBlODAiLCJleHAiOjE3MjU4NzE3MTkxMTUsInRpbWVzdGFtcCI6MTcyNTg2OTkxOTEzNn0.JzAmAPRTCpujzhwbtelT76OfdpeoALLl-PGjL5iw8bg" \
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