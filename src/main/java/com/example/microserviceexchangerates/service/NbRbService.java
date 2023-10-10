package com.example.microserviceexchangerates.service;

import com.example.microserviceexchangerates.model.NbRbRate;
import com.example.microserviceexchangerates.repository.NbRateRepository;
import com.example.microserviceexchangerates.util.NbRbAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@Service
public class NbRbService {
    private static final Logger logger = LogManager.getLogger(NbRbService.class);
    @Autowired
    private NbRateRepository nbRateRepository;
    private final NbRbAPI nbRbAPI = new NbRbAPI();

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
        logger.info(String.format("getByDateCode %s, %s, %s.", date, code, nbRbRate));
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
        logger.info(String.format("getByDate %s, collection size: %s",date, nbRbRates.length));
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
        logger.info(String.format("getNbRbRateByDateCode. nbRbRate: %s",nbRbRate.toString()));
        return nbRbRate;
    }

    private Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        logger.info(String.format("convertStringToDate. String date: %s", formatter.parse(date)));
        return formatter.parse(date);
    }

    private String convertDateToString(Date date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        logger.info(String.format("convertDateToString. Date from String: %s", formatter.format(date)));
        return formatter.format(date);
    }
}
