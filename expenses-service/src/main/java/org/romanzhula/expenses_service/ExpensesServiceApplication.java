package org.romanzhula.expenses_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@EnableEurekaServer
@SpringBootApplication
public class ExpensesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpensesServiceApplication.class, args);
	}

}
