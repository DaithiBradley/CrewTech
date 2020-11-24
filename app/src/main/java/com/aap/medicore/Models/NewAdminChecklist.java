package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewAdminChecklist {
    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("form")
    @Expose
    private List<AdminFormModel> forms;

    public List<AdminFormModel> getForms() {
        return forms;
    }

    public void setForms(List<AdminFormModel> forms) {
        this.forms = forms;
    }
}
