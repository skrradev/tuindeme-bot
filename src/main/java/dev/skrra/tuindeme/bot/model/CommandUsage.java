package dev.skrra.tuindeme.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "command_usage")
public class CommandUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String command;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String chatId;

    @Column(nullable = false)
    private Integer promptTokens;

    @Column(nullable = false)
    private Integer completionTokens;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal totalCost;

    @Column(nullable = false)
    private Instant executedAt;
}