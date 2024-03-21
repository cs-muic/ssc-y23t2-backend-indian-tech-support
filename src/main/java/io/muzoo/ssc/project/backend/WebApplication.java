package io.muzoo.ssc.project.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebApplication {

	public static void main(String[] args) throws Throwable {
		SpringApplication.run(WebApplication.class, args);
	}

}