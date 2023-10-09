package com.example.microserviceexchangerates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(basePackages = "com.example.microserviceexchangerates")
@SpringBootApplication
@EntityScan("com.example.microserviceexchangerates.*")
public class MicroserviceExchangeRatesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceExchangeRatesApplication.class, args);
	}

}