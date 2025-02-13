package dev.skrra.tuindeme.bot.repository;

import dev.skrra.tuindeme.bot.model.CommandUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CommandUsageRepository extends JpaRepository<CommandUsage, Long> {
    @Query("SELECT cu FROM CommandUsage cu WHERE cu.command = :command AND cu.chatId = :chatId ORDER BY cu.executedAt DESC LIMIT 1")
    Optional<CommandUsage> findLastUsage(@Param("command") String command, @Param("chatId") String chatId);

    @Query("SELECT SUM(cu.totalCost) FROM CommandUsage cu WHERE cu.chatId = :chatId")
    BigDecimal calculateTotalCostForChat(@Param("chatId") String chatId);
}