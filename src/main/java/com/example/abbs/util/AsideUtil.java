package com.example.abbs.util;

import java.io.BufferedReader;
import java.io.FileReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
// 별도로 파일을 빼냄, 여기 위치에 있는 파일을 불러오겠다 
@PropertySource("classpath:static/data/myKeys.properties")
public class AsideUtil {
	@Value("roadAddrKey") private String roadAddrKey;
	@Value("kakaoApiKey") private String kakaoApiKey;
	@Value("openWeatherApiKey") private String openWeatherApiKey;
	
	
	public String getTodayQuote(String filename) {
		String result = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename), 1024);
			int index = (int) Math.floor(Math.random() * 100); // 0 ~ 99
			for (int i = 0; i <= index; i++) { // index = 10
				result = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
