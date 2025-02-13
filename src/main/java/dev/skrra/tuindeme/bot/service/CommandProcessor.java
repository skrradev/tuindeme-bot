package dev.skrra.tuindeme.bot.service;

import dev.skrra.tuindeme.bot.config.prop.LlmProps.OpenAiProperties;
import dev.skrra.tuindeme.bot.model.ChatMessage;
import dev.skrra.tuindeme.bot.model.CommandUsage;
import dev.skrra.tuindeme.bot.repository.CommandUsageRepository;
import dev.skrra.tuindeme.bot.config.prop.TelegramProperties;
import dev.skrra.tuindeme.bot.config.prop.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import dev.skrra.tuindeme.bot.util.CostCalculator;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandProcessor {
    private final OpenAiService openAiService;
    private final MessageStorageService messageStorageService;
    private final OpenAiProperties openAiProperties;
    private final CommandUsageRepository commandUsageRepository;
    private final TelegramProperties telegramProperties;
    private final TelegramMessageService messageService;
    private final ApplicationProperties applicationProperties;

    public void processCommand(Update update) {
        String command = update.getMessage().getText();
        if (command.startsWith("/summary")) {
            processSummaryCommand(update);
        } else if (command.startsWith("/credits")) {
            processCreditsCommand(update);
        }
    }

    private void processSummaryCommand(Update update) {
        if (isRateLimited(update)) {
            sendRateLimitMessage(update);
            return;
        }

        List<ChatMessage> messages = messageStorageService.getLastMessages();
        if (messages.isEmpty()) {
            sendNoMessagesMessage(update);
            return;
        }

        String prompt = createSummaryPrompt(messages);
        ChatResponse response = openAiService.processMessage(prompt);
        sendSummaryResponse(update, response);
    }

    private boolean isRateLimited(Update update) {
        return commandUsageRepository.findLastUsage("/summary", update.getMessage().getChatId().toString())
                .map(lastUsage -> lastUsage.getExecutedAt()
                        .isAfter(Instant.now()
                                .minus(applicationProperties.getRateLimit().getSummaryCommandMinutes(),
                                        ChronoUnit.MINUTES)))
                .orElse(false);
    }

    private void notifyDeveloper(String errorMessage, Exception e) {
        try {
            String fullError = String.format("❌ Error: %s\n\nException: %s\n\nStack trace: %s",
                    errorMessage,
                    e.getMessage(),
                    e.getStackTrace().length > 0 ? e.getStackTrace()[0].toString() : "No stack trace");

            messageService.sendMessage(telegramProperties.getDeveloperChatId(), fullError);
        } catch (TelegramApiException ex) {
            log.error("Failed to send error notification to developer", ex);
        }
    }

    private void sendRateLimitMessage(Update update) {
        try {
            messageService.sendMessage(update.getMessage().getChatId(), "⚠️ Команданы қайта пайдалану үшін 1 минут күтіңіз");
        } catch (TelegramApiException e) {
            handleError("Failed to send rate limit message", e, update);
        }
    }

    private void sendNoMessagesMessage(Update update) {
        try {
            messageService.sendMessage(update.getMessage().getChatId(), "ℹ️ Қазіргі уақытта хабарламалар жоқ");
        } catch (TelegramApiException e) {
            handleError("Failed to send no messages notification", e, update);
        }
    }

    private String createSummaryPrompt(List<ChatMessage> messages) {
        return messages.stream()
                .map(msg -> String.format("%s: %s", msg.getUsername(), msg.getMessageText()))
                .collect(Collectors.joining("\n"));
    }

    private void sendSummaryResponse(Update update, ChatResponse response) {
        try {
            String summary = response.getResult().getOutput().getText() + "\n #summary";

            // Send summary
            messageService.sendMessage(update.getMessage().getChatId(), summary);

            // Send cost information
            BigDecimal totalCost = calculateTotalCost(response);
            String costMessage = formatCost(totalCost);
            messageService.sendMessage(update.getMessage().getChatId(), costMessage);

            // Save usage statistics
            saveCommandUsage(update, response, totalCost);

            // Clear messages after successful summary
            messageStorageService.clearMessages();
            log.info("Messages cleared after successful summary generation");
        } catch (Exception e) {
            handleError("Error processing summary response", e, update);
        }
    }

    private void saveCommandUsage(Update update, ChatResponse response, BigDecimal totalCost) {
        int promptTokens = response.getMetadata().getUsage().getPromptTokens();
        int completionTokens = response.getMetadata().getUsage().getCompletionTokens();

        CommandUsage usage = CommandUsage.builder()
                .command("/summary")
                .userId(update.getMessage().getFrom().getId().toString())
                .chatId(update.getMessage().getChatId().toString())
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .totalCost(totalCost)
                .executedAt(Instant.now())
                .build();

        commandUsageRepository.save(usage);
    }

    private String formatCost(BigDecimal totalCost) {
        return String.format(
                "💰 Шығындар: $%s",
                totalCost.setScale(6, RoundingMode.HALF_UP));
    }

    private BigDecimal calculateTotalCost(ChatResponse chatResponse) {
        BigDecimal inputPrice = openAiProperties.getIntelligentModel().getPrice().getInput();
        BigDecimal outputPrice = openAiProperties.getIntelligentModel().getPrice().getOutput();

        return CostCalculator.calculateTotalCost(chatResponse, inputPrice, outputPrice);
    }


    private void processCreditsCommand(Update update) {
        try {
            BigDecimal totalCost = commandUsageRepository.calculateTotalCostForChat(update.getMessage().getChatId().toString());

            if (totalCost == null) {
                messageService.sendMessage(update.getMessage().getChatId(), "ℹ️ Әзірге ешқандай кредит жұмсалған жоқ");
                return;
            }

            String statsMessage = String.format(
                    "💰 Жалпы шығындар: $%s",
                    totalCost.setScale(6, RoundingMode.HALF_UP));

            messageService.sendMessage(update.getMessage().getChatId(), statsMessage);
        } catch (Exception e) {
            handleError("Error processing credits command", e, update);
        }
    }

    private void handleError(String errorMessage, Exception e, Update update) {
        log.error(errorMessage, e);
        notifyDeveloper(errorMessage, e);
        try {
            messageService.sendMessage(update.getMessage().getChatId(), "⚠️ Қате орын алды. Әзірлеушіге хабарланды.");
        } catch (TelegramApiException ex) {
            log.error("Failed to send error message to user", ex);
        }
    }


}