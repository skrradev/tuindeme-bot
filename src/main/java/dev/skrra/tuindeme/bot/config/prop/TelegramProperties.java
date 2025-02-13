package dev.skrra.tuindeme.bot.config.prop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {
    @NotNull
    private Bot bot;
    @NotEmpty
    private Set<String> authorizedUsers;
    @NotBlank
    private String authorizedChatGroupId;
    @NotBlank
    private String developerChatId;

    @Data
    @Validated
    public static class Bot {
     

        @NotBlank
        private String token;
        @NotBlank
        private String userName;

    }
}
