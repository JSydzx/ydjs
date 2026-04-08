package com.zjgsu.teamplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan("com.zjgsu.teamplatform.mapper")
@SpringBootApplication
public class TeamplatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamplatformApplication.class, args);
	}

}
