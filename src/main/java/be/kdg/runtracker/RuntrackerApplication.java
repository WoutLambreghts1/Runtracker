package be.kdg.runtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("be.kdg.runtracker")
public class RuntrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RuntrackerApplication.class, args);
	}
}
