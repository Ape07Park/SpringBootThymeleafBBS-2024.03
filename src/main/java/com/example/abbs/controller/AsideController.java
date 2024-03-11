package com.example.abbs.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.abbs.util.AsideUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/aside")
public class AsideController {
	@Autowired
	private AsideUtil asideUtil; // AsideUtil 클래스의 인스턴스 생성

	@ResponseBody // 데이터 바로 보냄
	@GetMapping("/stateMsg")
	public String changeStateMsg(String stateMsg, HttpSession session) {
		session.setAttribute("stateMsg", stateMsg);
		return "return message"; // 데이터 바로 보냄
	}

	@ResponseBody 
	@GetMapping("/weather") // weather로 들어오는 거 받음
	// session 타입이라 String으로 형변환
	
	   public String weather(HttpSession session) {
	      String location = (String) session.getAttribute("location") + "청";
	      String roadAddr = asideUtil.getRoadAddr(location);
	      Map<String, String> map = asideUtil.getGeocode(roadAddr);
	      String result = asideUtil.getWeather(map.get("lon"), map.get("lat"));
	      return result;
	   }		
	
}	


