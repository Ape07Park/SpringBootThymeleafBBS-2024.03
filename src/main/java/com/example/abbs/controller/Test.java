package com.example.abbs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/lb")
public class Test {
	
	@GetMapping("/test")
	public String test() {
		return "/test";
	}
}
