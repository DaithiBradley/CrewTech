package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;

public class SaveField2 {

    @SerializedName("id")
    @Expose
    private Integer fieldId;
    @SerializedName("input_type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("placeholder")
    @Expose
    private String placeholder;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("required")
    @Expose
    private String required;
    @SerializedName("formOptions")
    @Expose
    private ArrayList<Option2> options ;

    @SerializedName("barcode")
    @Expose
    private boolean barcode;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @SerializedName("value")
    @Expose
    private String value;

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public ArrayList<Option2> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Option2> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("fieldId", fieldId).append("type", type).append("label", label).append("placeholder", placeholder).append("name", name).append("required", required).append("options", options).toString();
    }

    public boolean isBarcode() {
        return barcode;
    }

    public void setBarcode(boolean barcode) {
        this.barcode = barcode;
    }
}