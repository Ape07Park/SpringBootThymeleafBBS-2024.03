package com.example.abbs.service;

import java.util.List;

import com.example.abbs.entity.Like;

public interface LikeService {
	
	Like getLike(int bid, String uid);
	
	Like getLikeByLid(int lid);
	
	List<Like> getLikeList(int bid);
	
	void insertLike(Like like);
	
	void toggleLike(Like like); //  toggle: value가 0이면 1로 바꾸고, 1이면 0으로 바꿈
	
	int getLikeCount(int bid);
}
