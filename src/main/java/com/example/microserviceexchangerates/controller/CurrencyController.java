package com.example.microserviceexchangerates.controller;

import com.example.microserviceexchangerates.model.NbRbRate;
import com.example.microserviceexchangerates.service.NbRbService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.*;

@RestController
@Transactional
public class CurrencyController {

    @Autowired
    private NbRbService nbRbService;

    @GetMapping("api/get-by-date")
    public ResponseEntity getByDate(@RequestParam("date") String date) throws ParseException {
        LinkedHashMap[] response = nbRbService.getByDate(date);
        return new ResponseEntity<>(
                response,
                this.getHeaders(Arrays.toString(response).getBytes()),
                HttpStatus.OK
        );
    }

    @GetMapping("api/get-by-date-code")
    public ResponseEntity getByDateCode(@RequestParam("date") String date, @RequestParam("code") String code) throws ParseException {
        NbRbRate nbRbRate = this.nbRbService.getByDateCode(date, code);
        String json = new Gson().toJson(nbRbRate);
        return new ResponseEntity<>(
                json,
                this.getHeaders(nbRbRate.toString().getBytes()),
                HttpStatus.OK
        );
    }

    public HttpHeaders getHeaders(byte[] bytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("CRC32", nbRbService.getCRC32Checksum(bytes));
        return headers;
    }

}
