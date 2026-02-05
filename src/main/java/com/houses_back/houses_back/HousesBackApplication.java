package com.houses_back.houses_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HousesBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(HousesBackApplication.class, args);
	}

}
