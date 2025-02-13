package dev.skrra.tuindeme.bot.service;

import dev.skrra.tuindeme.bot.config.TelegramBot;
import dev.skrra.tuindeme.bot.config.prop.LlmProps.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandProcessor {
    private final OpenAiService openAiService;
    private final MessageStorageService messageStorageService;
    private final TelegramBot telegramBot;
    private final OpenAiProperties openAiProperties;

    public void processCommand(String command, String chatId) {
        if (command.startsWith("/summary")) {
            var messages = messageStorageService.getLastMessages();
            // Format messages in the required format
            String formattedMessages = messages.stream()
                    .map(msg -> String.format("@%s: %s", msg.getUsername(), msg.getMessageText()))
                    .collect(Collectors.joining("\n"));

            // Create prompt for OpenAI
            String prompt = "Төмендегі хабарламалардың қысқаша мазмұнын жасаңыз:\n\n" + formattedMessages;

            // Send to OpenAI for processing
            ChatResponse response = openAiService.processMessage(prompt);
            String summary = response.getResult().getOutput().toString();

            try {
                // Send summary
                SendMessage summaryMessage = new SendMessage(chatId, summary);
                telegramBot.execute(summaryMessage);

                // Calculate and send cost information
                String costMessage = calculateAndFormatCost(response);
                SendMessage costInfoMessage = new SendMessage(chatId, costMessage);
                telegramBot.execute(costInfoMessage);
            } catch (TelegramApiException e) {
                log.error("Failed to send message to Telegram", e);
            }
        }
    }

    private String calculateAndFormatCost(ChatResponse response) {
        // Get actual token counts from response
        int promptTokens = response.getMetadata().getUsage().getPromptTokens();
        int completionTokens = response.getMetadata().getUsage().getCompletionTokens();

        // Calculate costs
        BigDecimal inputCost = BigDecimal.valueOf(promptTokens)
                .multiply(openAiProperties.getIntelligentModel().getPrice().getInput())
                .divide(new BigDecimal("1000"), 6, RoundingMode.HALF_UP);

        BigDecimal outputCost = BigDecimal.valueOf(completionTokens)
                .multiply(openAiProperties.getIntelligentModel().getPrice().getOutput())
                .divide(new BigDecimal("1000"), 6, RoundingMode.HALF_UP);

        BigDecimal totalCost = inputCost.add(outputCost);

        // Format the cost message
        return String.format(
                "💰 Шығындар:\nКіріс токендер (%d): $%s\nШығыс токендер (%d): $%s\nБарлығы: $%s",
                promptTokens,
                inputCost.setScale(6, RoundingMode.HALF_UP),
                completionTokens,
                outputCost.setScale(6, RoundingMode.HALF_UP),
                totalCost.setScale(6, RoundingMode.HALF_UP));
    }
}