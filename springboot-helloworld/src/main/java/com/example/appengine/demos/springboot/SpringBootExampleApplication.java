
package com.example.appengine.demos.springboot;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling
@SpringBootApplication
public class SpringBootExampleApplication {

	public static void main(String[] args) {
		System.out.println("Application going to start");
		SpringApplication.run(SpringBootExampleApplication.class, args);
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
	}
}
