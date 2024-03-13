package com.example.abbs.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.abbs.entity.SchDay;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

	@GetMapping({ "/calendar/{arrow}", "/calendar" })
	public String calendar(@PathVariable(required = false) String arrow, 
			HttpSession session, Model model) {
		LocalDate today = LocalDate.now();
		int year = today.getYear();
		int month = today.getMonthValue();
		
		// 요일 만들기
		String date = "일 월 화 수 목 금 토".split(" ")[today.getDayOfWeek().getValue() % 7]; // 그냥하면 1~7 나오니 %로 나누어 0 ~ 6 나오게 함
		String sessionMonthYear = (String) session.getAttribute("scheduleMonthYear"); // 2024.03
		if (sessionMonthYear != null) {
			year = Integer.parseInt(sessionMonthYear.substring(0,4)); // 2024
			month = Integer.parseInt(sessionMonthYear.substring(5)); // .03
		}
		if(arrow != null) {
			switch(arrow) {
			case "left":
				month -= 1;
				if(month == 0) {
					year -= 1;
					month = 12;
				}
				break;
			case "right":
				month += 1;
				if(month == 13) {
					year += 1;
					month = 1;					
				}
				break;
				
			case "left2":
				year -=1;
				break;
				
			case "right2":
				year +=1;
				break;

			}
		}
		sessionMonthYear = String.format("%d.%02d", year, month);
		session.setAttribute("scheduleMonthYear", sessionMonthYear);
		String sessUid = (String) session.getAttribute("sessUid");
		
		List<SchDay> week = new ArrayList<>();
		List<List<SchDay>> calendar = new ArrayList<>();
		
		LocalDate startDay = LocalDate.parse(String.format("%d-%02d-01", year, month));
		int startDate = startDay.getDayOfWeek().getValue() % 7;
		LocalDate lastDay = startDay.withDayOfMonth(startDay.lengthOfMonth()); // 3월 31 구하는 법
		int lastDate = lastDay.getDayOfWeek().getValue() % 7;
		
		// k는 날짜, i는 요일
		String sdate = null;
		
		// 지난 달
		if (startDate != 0) { // month의 시작일이 일요일이 아니면
			LocalDate prevSunday = startDay.minusDays(startDate);
			int prevDay = prevSunday.getDayOfMonth();
			int prevMonth = prevSunday.getMonthValue();
			int prevYear = prevSunday.getYear();
			for (int i = 0; i < startDate; i++) {
				sdate = String.format("%d%02d%02d", prevYear, prevMonth, prevDay + i);
				SchDay sd = new SchDay();
				sd.setDay(prevDay + i);
				sd.setDate(i); // 요일
				sd.setSdate(sdate);
				week.add(sd);
			}
		}
		
		// 이번 달
		for (int i = startDate, k = 1; i < 7; i++, k++) {
			sdate = String.format("%d%02d%02d", year, month, k);  
			SchDay sd = new SchDay();
			sd.setDay(k);
			sd.setDate(i); // 요일
			sd.setSdate(sdate);
			week.add(sd);
		}
		calendar.add(week);
		
		// 둘째 주부터 해당월의 마지막 날까지
		// 둘째 주 시작일
		int day = 8 - startDate;
		for (int k = day, i = 0; k <= lastDay.getDayOfMonth(); k++, i++) {
			if (i % 7 == 0) {
				week = new ArrayList<>();				
			}
			sdate = String.format("%d%02d%02d", year, month, k);  
			SchDay sd = new SchDay();
			sd.setDay(k);
			sd.setDate(i % 7); // 요일
			sd.setSdate(sdate);
			week.add(sd);
			
			if(i % 7 == 6) {
				calendar.add(week);
			}	
		}
		
		// 다음 달 1일부터 그주 토요일까지
		if(lastDate != 6) {
			LocalDate nextDay = lastDay.plusDays(1);
			int nextYear = nextDay.getDayOfYear();
			int nextMonth = nextDay.getMonthValue();
			
			for (int i = lastDate +1, k = 1; i < 7; i++, k++) {
				sdate = String.format("%d%02d%02d", nextYear, nextMonth, k);  
				SchDay sd = new SchDay();
				sd.setDay(k);
				sd.setDate(i); 
				sd.setSdate(sdate);
				week.add(sd);
			}
			calendar.add(week);
		}
		
		model.addAttribute("calendar", calendar);
		
		model.addAttribute("today", today+"("+ date+")");
		model.addAttribute("year", year);
		model.addAttribute("month", String.format("%02d", month));
		model.addAttribute("height", 600 / calendar.size());
		
		return "schedule/calendar";
	}
}
