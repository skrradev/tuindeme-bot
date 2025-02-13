package dev.skrra.tuindeme.bot.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {
    private Storage storage = new Storage();
    private RateLimit rateLimit = new RateLimit();

    @Data
    @Validated
    public static class Storage {
        private int maxMessages = 50;
    }

    @Data
    @Validated
    public static class RateLimit {
        private int summaryCommandMinutes = 1;
    }
}