package com.example.abbs.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.abbs.entity.Board;
import com.example.abbs.entity.Like;
import com.example.abbs.entity.Reply;
import com.example.abbs.service.BoardService;
import com.example.abbs.service.LikeService;
import com.example.abbs.service.ReplyService;
import com.example.abbs.util.JsonUtil;

import jakarta.servlet.http.HttpSession;
// 내용물 바꾸는 update는 알아서 만들기

@Controller
@RequestMapping("/board")
public class BoardController {
	/*
	 * 변수 선언
	 */
	@Autowired
	private BoardService boardService;
	@Autowired
	private JsonUtil jsonUtil;
	@Autowired
	ReplyService replyService;
	@Autowired
	private LikeService likeService;
	
	@Value("${spring.servlet.multipart.location}")
	
	private String uploadDir;
	
/*
 * 여기부터 구현
 */
	
	@GetMapping("/list") // 이름은 p로하고 값이 없으면 1로
	
	public String list(@RequestParam(name = "p", defaultValue = "1") int page,
			@RequestParam(name = "f", defaultValue = "title") String field,
			@RequestParam(name = "q", defaultValue = "") String query, HttpSession session, Model model) {
		List<Board> boardList = boardService.getBoardList(page, field, query);
		System.out.println(boardList);

		// pagenation
		int totalBoardCount = boardService.getBoardCount(field, query);

		// 자바의 기본값이 double이니 double 사용함
		int totalPages = (int) Math.ceil(totalBoardCount / (double) BoardService.COUNT_PER_PAGE);

		int startPage = (int) Math.ceil((page - 0.5) / BoardService.PAGE_PER_SCREEN - 1) * BoardService.PAGE_PER_SCREEN
				+ 1;
		int endPage = Math.min(totalPages, startPage + BoardService.PAGE_PER_SCREEN - 1);
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

	@PostMapping("/insert")
	public String insertProc(String title, String content, 
			MultipartHttpServletRequest req, HttpSession session) {
		String sessUid = (String) session.getAttribute("sessUid");
		List<MultipartFile> uploadFileList = req.getFiles("files");

		List<String> fileList = new ArrayList<>();

		for (MultipartFile part : uploadFileList) {
			// 첨부 파일이 없는 경우 - application/octet-stream
			if (part.getContentType().contains("octet-stream")) {
				continue; // 읽을 필요 x라서 넘김
			}
			String filename = part.getOriginalFilename();
			String uploadPath = uploadDir + "upload/" + filename;
			try {
				part.transferTo(new File(uploadPath));
			} catch (Exception e) {
				e.printStackTrace();
			}
			fileList.add(filename);
		}

		// 주고 받는 것을 json으로 하니까 파일들을 json으로 바꿈
		String files = jsonUtil.list2Json(fileList);

		Board board = new Board(title, content, sessUid, files);
		boardService.insertBoard(board);
		return "redirect:/board/list";
	}

	@GetMapping("/detail/{bid}/{uid}")
	public String detail(@PathVariable int bid, @PathVariable String uid, String option, 
			HttpSession session, Model model) {
		// 본인이 조회 시 or 댓글 작성 후에는 조회수 증가 x
		String sessUid = (String) session.getAttribute("sessUid");
		if (!uid.equals(sessUid) && (option == null || option.equals(""))) {
			boardService.increaseViewCount(bid);
		}

		Board board = boardService.getBoard(bid);
		String jsonFiles = board.getFiles();

		// jsonFiles 빈 거 방지, json을 파일로 만들어서 보냄
		if (!(jsonFiles == null || jsonFiles.equals(""))) {
			List<String> fileList = jsonUtil.json2List(jsonFiles);
			model.addAttribute("fileList", fileList);
		}

		model.addAttribute("board", board);
		model.addAttribute("count", board.getLikeCount());
		
		List<Reply> replyList = replyService.getReplyList(bid);
		model.addAttribute("replyList", replyList);

		return "board/detail";
	}

	@GetMapping("/delete/{bid}")
	public String delete(@PathVariable int bid, HttpSession session) {
		boardService.deleteBoard(bid);
		return "redirect:/board/list?p=" + session.getAttribute("currentBoardPage");
	}

	@PostMapping("/reply") // 로그인한 사람 누구인지 알기 위해 HttpSession session 사용
	public String reply(int bid, String uid, String comment, HttpSession session) {
		String sessUid = (String) session.getAttribute("sessUid");
		int isMine = (sessUid.equals(uid)) ? 1 : 0;
		Reply reply = new Reply(comment, sessUid, bid, isMine);

		replyService.insertReply(reply);
		boardService.increaseReplyCount(bid);

		return "redirect:/board/detail/" + bid + "/" + uid + "?option=DNI";
	}
	
	// AJAX 처리
	@GetMapping("/like/{bid}")
	public String like(@PathVariable int bid, HttpSession session, Model model) {
		String sessUid = (String) session.getAttribute("sessUid");
		 Like like = likeService.getLike(bid, sessUid);
		 // 있으면 가져오고 없으면 만들어야 함
		 if(like == null) {
			 likeService.insertLike(new Like(sessUid, bid, 1));
		 }
		 else {
			 likeService.toggleLike(like);
		 }
		 int count = likeService.getLikeCount(bid);
		 
//		 boardService. 		board.likeCount update 만들어야 함
		 model.addAttribute("count", count);
		
		 // :: - 람다의 간결한 버전
		return "board/detail::#likeCount";
	}

}
