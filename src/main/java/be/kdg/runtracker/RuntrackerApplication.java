package be.kdg.runtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("be.kdg.runtracker")
public class RuntrackerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(RuntrackerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RuntrackerApplication.class);
	}
}
