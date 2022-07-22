package com.nepapp.doineedit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("purchased")
    @Expose
    private Boolean purchased;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("imageId")
    @Expose
    private String imageId;
    @SerializedName("location")
    @Expose
    private String location;

    public Product(String id, String title, Double price, String description, String url, Boolean purchased, String createdBy, String image, String imageId, String location) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.url = url;
        this.purchased = purchased;
        this.createdBy = createdBy;
        this.image = image;
        this.imageId = imageId;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}