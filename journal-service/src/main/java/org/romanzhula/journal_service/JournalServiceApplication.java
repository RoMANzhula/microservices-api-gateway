package org.romanzhula.journal_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class JournalServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JournalServiceApplication.class, args);
	}

}
