package com.example.whocanplay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WhocanplayApplication {
	public static final String SQLUSERNAME = "springuser";
	public static final String SQLPASSWORD = "#Kwanz9Laur3nCarolinEW3reHere";
	public static final String URL = "jdbc:mysql://localhost:3306/WHOCANPLAY";


	public static void main(String[] args) {
		SpringApplication.run(WhocanplayApplication.class, args);
	}

}
