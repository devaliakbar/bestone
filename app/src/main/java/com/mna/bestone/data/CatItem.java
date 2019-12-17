package com.mna.bestone.data;

public class CatItem {
    private int id;
    private String name;
    private String brand;
    private double price;
    private String url;

    public CatItem(int id, String name, String brand, double price, String url) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public String getBrand() {
        return brand;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
