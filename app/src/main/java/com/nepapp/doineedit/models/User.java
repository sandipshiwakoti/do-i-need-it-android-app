package com.nepapp.doineedit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("mobile")
    @Expose
    private Integer mobile;
    @SerializedName("password")
    @Expose
    private String password;


    public User(String id, String email, String fullname, Integer mobile, String password) {
        super();
        this.id = id;
        this.email = email;
        this.fullname = fullname;
        this.mobile = mobile;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Integer getMobile() {
        return mobile;
    }

    public void setMobile(Integer mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}