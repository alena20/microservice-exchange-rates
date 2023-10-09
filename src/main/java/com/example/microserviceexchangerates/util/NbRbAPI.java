package com.example.microserviceexchangerates.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

public class NbRbAPI {

    private static final String BASE_URL = "https://api.nbrb.by/exrates/rates";

    public LinkedHashMap[] getByDate(String date) {
        String url = String.format("%s?ondate=%s&periodicity=0", BASE_URL, date);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<LinkedHashMap[]> response = restTemplate.getForEntity(url, LinkedHashMap[].class);
        return response.getBody();
    }

    public LinkedHashMap getByDateCode(String date, String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s/%s?parammode=2&ondate=%s&periodicity=0", BASE_URL, code, date);
        return restTemplate.getForEntity(url, LinkedHashMap.class).getBody();
    }
}
