package dev.skrra.tuindeme.bot.config.prop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class LlmProps {

    @Data
    @Component
    @Validated
    @ConfigurationProperties(prefix = "llm.open-ai")
    public static class OpenAiProperties {
        @NotNull
        private Model intelligentModel;
        @NotNull
        private Model fastModel;
    }


    @Data
    @Validated
    public static class Model {
        @NotBlank
        private String model;
        @NotNull
        private ModelPrice price;
    }

    @Data
    @Validated
    public static class ModelPrice {
        @NotNull
        private BigDecimal input;
        @NotNull
        private BigDecimal output;
    }
}

