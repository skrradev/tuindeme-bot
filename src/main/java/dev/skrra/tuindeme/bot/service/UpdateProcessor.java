package dev.skrra.tuindeme.bot.service;

import dev.skrra.tuindeme.bot.config.prop.TelegramProperties;
import dev.skrra.tuindeme.bot.model.ChatMessage;
import dev.skrra.tuindeme.bot.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProcessor {

    private final TelegramProperties telegramProperties;
    private final CommandProcessor commandProcessor;
    private final MessageStorageService messageStorageService;

    @EventListener
    public void processUpdate(Update update) {
        log.info("Processing update from user: {} from chat: {}", 
        update.getMessage().getFrom().getUserName(), 
        update.getMessage().getChatId());

        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String userName = update.getMessage().getFrom().getUserName();
            String messageText = update.getMessage().getText();

            // Check if message is from authorized chat
            if (!chatId.equals(telegramProperties.getAuthorizedChatGroupId())) {
                log.warn("Received message from unauthorized chat: {}", chatId);
                return;
            }

            if (messageText.startsWith("/")) {
                if (!telegramProperties.getAuthorizedUsers().contains(userName)) {
                    log.warn("Command attempt from unauthorized user: {}", userName);
                    return;
                }
                commandProcessor.processCommand(update);
                return;
            }

            ChatMessage chatMessage = ChatMessage.builder()
                    .username(UserUtils.constructUserName(update.getMessage().getFrom()))
                    .messageText(messageText)
                    .build();
            messageStorageService.addMessage(chatMessage);
        }
    }
}

