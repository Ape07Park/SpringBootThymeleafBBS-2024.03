package com.example.abbs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/aside")
public class AsideController {
	
	@ResponseBody // 데이터 바로 보냄
	@GetMapping("/stateMsg")
	public String changeStateMsg(String stateMsg, HttpSession session) {
		session.setAttribute("stateMsg", stateMsg);
		return "1"; // 데이터 바로 보냄
	}
	
}
