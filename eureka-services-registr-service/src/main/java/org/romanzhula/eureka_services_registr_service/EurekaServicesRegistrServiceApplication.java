package org.romanzhula.eureka_services_registr_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@EnableEurekaServer
@SpringBootApplication
public class EurekaServicesRegistrServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServicesRegistrServiceApplication.class, args);
	}

}
