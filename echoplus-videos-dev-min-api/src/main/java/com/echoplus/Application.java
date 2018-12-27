package com.echoplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author EchoPlus
 */
@EnableConfigurationProperties
@SpringBootApplication()
@MapperScan(basePackages = "com.echoplus.mapper")
@ComponentScan(basePackages= {"com.echoplus","org.n3r.idworker"})
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
