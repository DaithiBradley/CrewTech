package com.aap.medicore.Models;

import com.aap.medicore.Utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AssignedIncidencesModel {
    private int id;
    String json = "";
    private String date;
    private String jobDateTime;
    private Date objDate;

    public String getJobDateTime() {
        return jobDateTime;
    }

    public void setJobDateTime(String jobDateTime) {
        this.jobDateTime = jobDateTime;
    }

    public Date getObjDate() {
        return objDate;
    }

    public void setObjDate(Date objDate) {
        this.objDate = objDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setDate(String date) {
        this.date = date;
        try {
           // objDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
            objDate = DateUtils.tryParseDateWithPatternList(date, new String[]{
                    DateUtils.slashDateWithYearAtEndEU,
                    DateUtils.hyphenDateWithYearAtStartUS});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDate() {
        return date;
    }
}
