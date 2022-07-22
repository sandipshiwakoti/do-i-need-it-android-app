package com.nepapp.doineedit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private User data;
    @SerializedName("message")
    @Expose
    private String message = "";

    public UserResponse(Boolean success, User data, String message) {
        super();
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}