package com.example.abbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.abbs.dao.AnniversaryDao;
import com.example.abbs.entity.Anniversary;

@Service
public class AnniversaryServiceImpl implements AnniversaryService {
	@Autowired private AnniversaryDao annivDao;
	
	@Override
	public List<Anniversary> getAnnivListByDay(String uid, String sdate) {
		return annivDao.getAnnviList(uid, uid, sdate);
	}

	@Override
	public List<Anniversary> getAnnivList(String uid, String startDay, String endDay) {
		return annivDao.getAnnviList(uid, startDay, endDay);
	}

	@Override
	public void insertAnniv(Anniversary anniv) {
		annivDao.insertAnniv(anniv);
		
	}

}
