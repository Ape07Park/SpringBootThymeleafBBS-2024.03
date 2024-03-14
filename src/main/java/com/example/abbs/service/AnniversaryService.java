package com.example.abbs.service;

import java.util.List;

import com.example.abbs.entity.Anniversary;

public interface AnniversaryService {
	
	List<Anniversary> getAnnivListByDay(String uid, String sdate);
	
	List<Anniversary> getAnnivList(String uid, String startDay,  String endDay ); // admin 거 기본으로 가져오고  사용자(james 등) 거 가져오기
	
	void insertAnniv(Anniversary anniv);
}
