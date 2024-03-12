package com.example.abbs.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.abbs.entity.Board;
import com.example.abbs.service.BoardService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/board")
public class BoardController {
	@Autowired private BoardService boardService;
	
	@GetMapping("/list") // 이름은 p로하고 값이 없으면 1로 
	// 
	public String list(@RequestParam(name="p", defaultValue="1") int page,
			@RequestParam(name="f", defaultValue="title") String field,
			@RequestParam(name="q", defaultValue="") String query,
			HttpSession session, Model model) {
		List<Board> boardList = boardService.getBoardList(page, field, query);
		System.out.println(boardList);
		
		// pagenation
		int totalBoardCount = boardService.getBoardCount(field, query);
		
		// 자바의 기본값이 double이니 double 사용함
		int totalPages = (int) Math.ceil(totalBoardCount / (double) BoardService.COUNT_PER_PAGE);
		
		int startPage = (int) Math.ceil((page-0.5)/BoardService.PAGE_PER_SCREEN - 1) * BoardService.PAGE_PER_SCREEN + 1;
		int endPage =  Math.min(totalPages, startPage + BoardService.PAGE_PER_SCREEN - 1);
		List<String> pageList = new ArrayList<>();
		for (int i = startPage; i <= endPage; i++) {
			pageList.add(String.valueOf(i));
		}
		
		// 파라미터 넘기기- 어디갔다가 다시오면 안나올 수 있음
		
		session.setAttribute("currentBoardPage", page);
		model.addAttribute("boardList", boardList);
		model.addAttribute("field", field);
		model.addAttribute("query", query);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("pageList", pageList);
		
		return "board/list";
	}
	
	@GetMapping("/insert")
	public String insertForm() {
		
		return "board/insert";
	}
	
}
