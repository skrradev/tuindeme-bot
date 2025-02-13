package dev.skrra.tuindeme.bot;

import dev.skrra.tuindeme.bot.config.prop.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class TuindemeBotApplication {

	private final TelegramProperties telegramProperties;

	public static void main(String[] args) {
		SpringApplication.run(TuindemeBotApplication.class, args);


	}


	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationStartup(ApplicationReadyEvent event) {
		log.info("Chat id {}", telegramProperties.getAuthorizedChatGroupId());
	}

}
