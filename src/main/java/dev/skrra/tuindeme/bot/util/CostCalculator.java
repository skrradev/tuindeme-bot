package dev.skrra.tuindeme.bot.util;

import org.springframework.ai.chat.model.ChatResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CostCalculator {
    private static final BigDecimal MILLION = BigDecimal.valueOf(1_000_000);
    private static final int SCALE = 6;

    public static BigDecimal calculateTotalCost(ChatResponse response, BigDecimal inputPrice, BigDecimal outputPrice) {
        // Convert prices from per million tokens to per token
        BigDecimal inputPricePerToken = inputPrice.divide(MILLION, 10, RoundingMode.HALF_UP);
        BigDecimal outputPricePerToken = outputPrice.divide(MILLION, 10, RoundingMode.HALF_UP);

        BigDecimal inputCost = BigDecimal.valueOf(response.getMetadata().getUsage().getPromptTokens())
                .multiply(inputPricePerToken);
        BigDecimal outputCost = BigDecimal.valueOf(response.getMetadata().getUsage().getCompletionTokens())
                .multiply(outputPricePerToken);

        return inputCost.add(outputCost).setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateInputCost(int tokens, BigDecimal inputPrice) {
        return BigDecimal.valueOf(tokens)
                .multiply(inputPrice)
                .divide(MILLION, SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateOutputCost(int tokens, BigDecimal outputPrice) {
        return BigDecimal.valueOf(tokens)
                .multiply(outputPrice)
                .divide(MILLION, SCALE, RoundingMode.HALF_UP);
    }
}