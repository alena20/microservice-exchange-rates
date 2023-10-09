package com.example.microserviceexchangerates.service;

import com.example.microserviceexchangerates.model.NbRbRate;
import com.example.microserviceexchangerates.repository.NbRateRepository;
import com.example.microserviceexchangerates.util.NbRbAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@Service
public class NbRbService {
    @Autowired
    private NbRateRepository nbRateRepository;
    @Autowired
    private NbRbAPI nbRbAPI;

    public NbRbRate getByDateCode(String date, String code) throws ParseException {
        Date formatDate = this.convertStringToDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formatDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterdayDate = calendar.getTime();
        NbRbRate nbRbRate = this.getNbRbRateByDateCode(formatDate, code);
        NbRbRate yesterdayNbRbRate = this.getNbRbRateByDateCode(yesterdayDate, code);
        double calcChange = nbRbRate.getCurOfficialRate() - yesterdayNbRbRate.getCurOfficialRate();
        String change = String.format("%1$,.5f", calcChange);
        change = String.format("%s%s", (calcChange > 0 ? "+" : "-"), change);
        nbRbRate.setChange(change);
        return nbRbRate;
    }

    public LinkedHashMap[] getByDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date formatDate = formatter.parse(date);
        LinkedHashMap[] nbRbRates = this.nbRbAPI.getByDate(date);
        nbRateRepository.deleteByDate(formatDate);
        for (LinkedHashMap nbRbRate : nbRbRates) {
            nbRateRepository.save(new NbRbRate(
                            (Integer) nbRbRate.get("Cur_ID"),
                            (String) nbRbRate.get("Cur_Abbreviation"),
                            (String) nbRbRate.get("Cur_Name"),
                            (double) nbRbRate.get("Cur_OfficialRate"),
                            (String) nbRbRate.get("Date")
                    )
            );
        }
        return nbRbRates;
    }

    public String getCRC32Checksum(byte[] bytes) {
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);

        return String.valueOf(checksum.getValue());
    }

    private NbRbRate getNbRbRateByDateCode(Date formatDate, String code) throws ParseException {
        NbRbRate nbRbRate = nbRateRepository.findByDateAndCurAbbreviation(formatDate, code);
        if (nbRbRate == null) {
            LinkedHashMap response = this.nbRbAPI.getByDateCode(this.convertDateToString(formatDate), code);
            nbRbRate = nbRateRepository.save(new NbRbRate(
                            (Integer) response.get("Cur_ID"),
                            (String) response.get("Cur_Abbreviation"),
                            (String) response.get("Cur_Name"),
                            (double) response.get("Cur_OfficialRate"),
                            (String) response.get("Date")
                    )
            );
        }
        return nbRbRate;
    }

    private Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return formatter.parse(date);
    }

    private String convertDateToString(Date date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return formatter.format(date);
    }
}
