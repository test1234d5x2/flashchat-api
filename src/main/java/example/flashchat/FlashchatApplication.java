package example.flashchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlashchatApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlashchatApplication.class, args);
	}

}
