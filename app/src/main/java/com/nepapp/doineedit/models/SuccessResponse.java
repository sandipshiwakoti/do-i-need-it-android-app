package com.nepapp.doineedit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuccessResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @Expose
    @SerializedName("data")
    private Product data;

    public SuccessResponse(Boolean success, String message, Product data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Product getData() {
        return data;
    }

    public void setData(Product data) {
        this.data = data;
    }
}
