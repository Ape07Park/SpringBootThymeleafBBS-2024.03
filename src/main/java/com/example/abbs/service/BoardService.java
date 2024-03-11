package com.example.abbs.service;

import java.util.List;

import com.example.abbs.entity.Board;

public interface BoardService {
	public static final int COUNT_PER_PAGE = 2; // 한 페이지당 글의 목록 및 갯수
	public static final int PAGE_PER_SCREEN = 10; // 한 화면에 표시되는 페이지 갯수
	
	Board getBoard(int bid);
	
	int getBoardCount(String field, String query);
	
	List<Board> getBoardList(int page, String field, String query);
	
	void insertBoard(Board board);
	
	void updateBoard(Board board);
	
	void deleteBoard(int bid);
	
	void increseViewCount(int bid);
	
	void increseReplyCount(int bid);
	
	void increseLikeCount(int bid);
	
	
	
}