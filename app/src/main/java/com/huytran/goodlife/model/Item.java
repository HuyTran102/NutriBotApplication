package com.huytran.goodlife.model;

public class Item {
    public String name, unit_type;
    public int image, kcal;
    public double amount, protein, lipid, glucid;

    public Item(String name, int image, int kcal, double protein, double lipid, double glucid, String unit_type) {
        this.name = name;
        this.image = image;
        this.kcal = kcal;
        this.protein = protein;
        this.lipid = lipid;
        this.glucid = glucid;
        this.unit_type = unit_type;
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
}
