package dev.skrra.tuindeme.bot.service;

import dev.skrra.tuindeme.bot.config.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramMessageService {
    private final TelegramBot telegramBot;

    public void sendMessage(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage(chatId, text);
        telegramBot.execute(message);
    }

    public void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage(chatId.toString(), text);
        telegramBot.execute(message);
    }
}