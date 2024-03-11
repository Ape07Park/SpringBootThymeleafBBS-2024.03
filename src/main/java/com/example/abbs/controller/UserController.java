package com.example.abbs.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.abbs.entity.User;
import com.example.abbs.service.UserService;
import com.example.abbs.util.AsideUtil;
import com.example.abbs.util.imageUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user") // user 붙는 요청은 여기서 다 처리
public class UserController {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired private UserService uSvc;
	@Autowired private imageUtil imageUtil;
	@Autowired private AsideUtil asideUtil;
	@Autowired private ResourceLoader resourceLoader;
	@Value("${spring.servlet.multipart.location}") private String uploadDir;

	// register
	@GetMapping("/register")
	public String registerForm() {
		return "user/register"; // prefix, suffix에서 미리 다 표시해놓음
	}

	@PostMapping("/register")
	public String registerProc(MultipartHttpServletRequest req, Model model, String uid, 
			String pwd, String pwd2, String uname, String email, 
			String github, String insta, String location) {		
		String filename = null;
		MultipartFile filePart = req.getFile("profile");
		
		if(uSvc.getUserByUid(uid) != null) {
			model.addAttribute("msg", "사용자 ID 중복");
			model.addAttribute("url", "/abbs/user/register");
			return "common/alertMsg";
		}
		
		if(pwd.equals(pwd2) && pwd != null) {
			// 이미지 처리
			if(filePart.getContentType().contains("image")) {
				filename = filePart.getOriginalFilename();
				String path = uploadDir + "profile/" + filename;
				try {
					filePart.transferTo(new File(path));
				} catch (Exception e) {
					e.printStackTrace();
				}
				filename = imageUtil.squareImage(uid, filename);
		}
			
			User user = new User(uid, pwd, uname, email, filename, github, insta, location);
			uSvc.registerUser(user);

			model.addAttribute("msg", "등록 완료, 로그인 하시오");
			model.addAttribute("url", "/abbs/user/login");
			return "common/alertMsg";
		}
		else {
			model.addAttribute("msg", "패스워드 틀림");
			model.addAttribute("url", "/abbs/user/register");
			return "common/alertMsg";
		}		 		
	}

	// login
	@GetMapping("/login")
	public String loginForm() {
		return "user/login"; // prefix, suffix에서 미리 다 표시해놓음
		
	}
	// 로그인 할려면 세션에 값을 적어야 함
	@PostMapping("/login")
	public String loginProc(String uid, String pwd, HttpSession session, Model model) {
		int result = uSvc.login(uid, pwd);
		switch(result) {
		
		case UserService.CORRECT_LOGIN:
			User user = uSvc.getUserByUid(uid);
			session.setAttribute("sessUid", uid);
			session.setAttribute("sessUname", user.getUname());
			session.setAttribute("profile", user.getProfile());
			session.setAttribute("email", user.getEmail());
			session.setAttribute("github", user.getGithub());
			session.setAttribute("insta", user.getInsta());
			
			session.setAttribute("location", user.getLocation());
			
			// 상태 메세지
			// c:/temp.abbs.data/todayQuote.txt
//			String quiteFile = uploadDir + "data/todayQuote.txt";
			// resource/static/data/todayQuote.txt
			
			/*
			 * 컴퓨터에 있는 것을 가져와 사용하는 방법
			 */
			Resource resource = resourceLoader.getResource("classpath:/static/data/todayQuote.txt");
			String quiteFile = null;
			try {
				quiteFile = resource.getURI().getPath();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			String stateMsg = asideUtil.getTodayQuote(quiteFile);
			session.setAttribute("stateMsg", stateMsg);
			
			// 환영 메세지
			log.info("info Login: {}, {}", uid, user.getUname());
			model.addAttribute("msg", user.getUname() + "님 환영합니다");
			model.addAttribute("url", "/abbs/board/list");
			break;
			
		case UserService.USER_NOT_EXIST:
			model.addAttribute("msg", "ID가 없음, 회원가입 페이지로 이동");
			model.addAttribute("url", "/abbs/user/register");
			break;
			
		
		case UserService.WRONG_PASSWORD:
			model.addAttribute("msg", "패스워드 틀림. 다시 입력하시오");
			model.addAttribute("url", "/abbs/user/login");
		}
		
		return "common/alertMsg";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/user/login";
	}
}
