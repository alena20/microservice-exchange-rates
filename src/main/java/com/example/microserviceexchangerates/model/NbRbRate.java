package com.example.microserviceexchangerates.model;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

@Entity
public class NbRbRate {
    public NbRbRate(int cur_code, String cur_Abbreviation, String cur_name, double cur_OfficialRate, String date) throws ParseException {
        this.curCode = cur_code;
        this.curAbbreviation = cur_Abbreviation;
        this.curName = cur_name;
        this.curOfficialRate = cur_OfficialRate;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        this.date = formatter.parse(date);
    }

    public NbRbRate() {
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "Cur_code")
    private int curCode;

    @Column(name = "Cur_Abbreviation")
    private String curAbbreviation;

    @Column(name = "Cur_Name")
    private String curName;

    @Column(name = "Cur_OfficialRate")
    private double curOfficialRate;

    @Column(name = "Date")
    private Date date;

    @Transient
    private String change;

    @Override
    public String toString() {
        return "NbRbRate{" +
                "curCode=" + curCode +
                ", curAbbreviation='" + curAbbreviation + '\'' +
                ", curName='" + curName + '\'' +
                ", curOfficialRate=" + curOfficialRate +
                ", date=" + date +
                ", change='" + change + '\'' +
                '}';
    }

    public void setChange(String change) {
        this.change = change;
    }

    public double getCurOfficialRate() {
        return this.curOfficialRate;
    }
}
