package com.example.microserviceexchangerates.repository;

import com.example.microserviceexchangerates.model.NbRbRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface NbRateRepository extends JpaRepository<NbRbRate, Long> {
    List<NbRbRate> findByDate(Date date);
    NbRbRate findByDateAndCurAbbreviation(Date date, String cur_Abbreviation);
    void deleteByDate(Date date);
}
