package com.zjgsu.teamplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@SpringBootApplication
public class TeamplatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamplatformApplication.class, args);
	}

}
