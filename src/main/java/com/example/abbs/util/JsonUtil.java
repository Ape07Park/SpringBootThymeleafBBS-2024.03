package com.example.abbs.util;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

@Component

	// list -> json
public class JsonUtil {
	public String list2Json(List<String> list) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("list", list);
		
		return jsonObj.toString();
	}
	
	// json -> list 
	public List<String>json2List(String jsonStr){
		// scanner 사용과 유사
		JSONParser parser = new JSONParser();
		List<String> list = null;
		
		try {
			JSONObject jsonObj = (JSONObject) parser.parse(jsonStr);
			JSONArray jsonArr = (JSONArray) jsonObj.get("list"); 
			list = (List<String>) jsonArr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
