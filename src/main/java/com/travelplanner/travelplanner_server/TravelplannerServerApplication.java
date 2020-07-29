package com.travelplanner.travelplanner_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class TravelplannerServerApplication {

	public static void main(String[] args) {
		SpringApplication. run(TravelplannerServerApplication.class, args);
	}

}
