package com.aap.medicore.Models;

import org.json.JSONArray;

import java.util.ArrayList;

public class SubmittedData {

    private String job_id;
    private String lat;
    private String lng;
    private String notes;
    private String dob;
    private String ref;
    private String return_facility;
    private JSONArray forms;

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getReturn_facility() {
        return return_facility;
    }

    public void setReturn_facility(String return_facility) {
        this.return_facility = return_facility;
    }

    public JSONArray getForms() {
        return forms;
    }

    public void setForms(JSONArray forms) {
        this.forms = forms;
    }
}
