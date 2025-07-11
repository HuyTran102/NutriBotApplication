package com.huytran.goodlife.model;

public class NutritionistDataView {

    private int image, rating;
    private String name, description1, description2, basicInfo, email, phone;

    public NutritionistDataView(int image, int rating, String name, String description1, String description2
            , String basicInfo, String email, String phone) {
        this.image = image;
        this.rating = rating;
        this.name = name;
        this.description1 = description1;
        this.description2 = description2;
        this.basicInfo = basicInfo;
        this.email = email;
        this.phone = phone;
    }

    public NutritionistDataView() {

    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription1() {
        return description1;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(String basicInfo) {
        this.basicInfo = basicInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
