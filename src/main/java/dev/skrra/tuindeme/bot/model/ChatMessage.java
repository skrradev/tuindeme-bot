package dev.skrra.tuindeme.bot.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessage {
    private String username;
    private String messageText;
}