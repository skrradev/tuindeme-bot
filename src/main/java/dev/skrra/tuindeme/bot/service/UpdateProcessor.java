package dev.skrra.tuindeme.bot.service;

import dev.skrra.tuindeme.bot.config.prop.TelegramProperties;
import dev.skrra.tuindeme.bot.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProcessor {

    private final TelegramProperties telegramProperties;
    private final CommandProcessor commandProcessor;
    private final MessageStorageService messageStorageService;

    public void processUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String userId = update.getMessage().getFrom().getId().toString();
            String messageText = update.getMessage().getText();

            // Check if message is from authorized chat
            if (!chatId.equals(telegramProperties.getAuthorizedChatGroupId())) {
                log.warn("Received message from unauthorized chat: {}", chatId);
                return;
            }

            // Store message from authorized chat regardless of user
            ChatMessage chatMessage = ChatMessage.builder()
                    .username(update.getMessage().getFrom().getUserName())
                    .messageText(messageText)
                    .build();
            messageStorageService.addMessage(chatMessage);

            // Process commands only from authorized users
            if (messageText.startsWith("/")) {
                if (!telegramProperties.getAuthorizedUsers().contains(userId)) {
                    log.warn("Command attempt from unauthorized user: {}", userId);
                    return;
                }
                commandProcessor.processCommand(messageText, chatId);
            }
        }
    }
}
