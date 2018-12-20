package com.echoplus.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author EchoPlus
 */
@RestController
public class HelloWorldController {
	
	@RequestMapping("/hello")
	public String Hello() {
		return "Hello Spring Boot~~~~";
	}
	
}
