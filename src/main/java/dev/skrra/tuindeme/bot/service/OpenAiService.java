package dev.skrra.tuindeme.bot.service;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.stereotype.Service;

import dev.skrra.tuindeme.bot.config.prop.LlmProps.OpenAiProperties;

import java.time.Instant;
import java.util.List;

import org.springframework.ai.chat.messages.Message;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAiChatModel openAiChatModel;
    private final OpenAiProperties openAiProperties;

    private static final String SYSTEM_PROMPT = """
            You are a helpful assistant that creates summaries in Kazakh language.
            When summarizing conversations:
            1. Always respond in Kazakh language
            2. Focus on the main topics and key points of discussion
            3. Maintain a formal but friendly tone
            4. Include relevant usernames when they contribute significant points
            5. Keep the summary concise but informative
            6. Use proper Kazakh grammar and punctuation
            7. If technical terms are mentioned, provide their commonly used Kazakh equivalents
            8. Structure the summary in clear paragraphs
            """;

    public ChatResponse processMessage(String promptText) {
        Message systemMessage = new SystemMessage(SYSTEM_PROMPT);
        Message userMessage = new UserMessage(promptText);

        var chatOptions = OpenAiChatOptions.builder()
                .model(openAiProperties.getIntelligentModel().getModel())
                .temperature(0.3)
                .build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), chatOptions);

        return openAiChatModel.call(prompt);
    }
}
