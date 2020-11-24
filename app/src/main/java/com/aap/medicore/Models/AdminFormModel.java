package com.aap.medicore.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdminFormModel {

    private int id;
    private String title;
    private String sub_title;

    @SerializedName("barcode_enabled")
    private String timeout;

    @SerializedName("form_field")
    private List<SaveField2> fieldsJson;


    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public int getId() {
        return id;
    }

    public void setId(int form_id) {
        this.id = form_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return sub_title;
    }

    public void setSubtitle(String subtitle) {
        this.sub_title = subtitle;
    }

    public List<SaveField2> getFieldsJson() {
        return fieldsJson;
    }

    public void setFieldsJson(List<SaveField2> fieldsJson) {
        this.fieldsJson = fieldsJson;
    }
}
