package com.github.aico.service.openai;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OpenAiClient {
    private final OpenAiService openAiService;

    public OpenAiClient(@Value("${openai.api-key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    public String getAiSummary(String content) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo-0125") // 최신 모델로 변경 (또는 gpt-4 사용 가능)
                .messages(Collections.singletonList(
                        new ChatMessage("user", "You are an AI that summarizes meeting notes in markdown format. Respond in Korean.\n" + content)
                ))
                .maxTokens(500)
                .build();

        return openAiService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }
}