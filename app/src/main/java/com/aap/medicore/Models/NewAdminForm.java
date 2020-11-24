package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewAdminForm {


    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("vehicle-checklist-form")
    @Expose
    private NewAdminChecklist vehicleChecklistForm;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NewAdminChecklist getVehicleChecklistForm() {
        return vehicleChecklistForm;
    }

    public void setVehicleChecklistForm(NewAdminChecklist vehicleChecklistForm) {
        this.vehicleChecklistForm = vehicleChecklistForm;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
