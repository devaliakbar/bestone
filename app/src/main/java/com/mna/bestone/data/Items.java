package com.mna.bestone.data;

public class Items {
    private int id;
    private String name;
    private String brand;
    private double price;
    private String catagory;
    private double qty;
    private String url;

    public Items(int id, String catagory, String name, String brand, double price, double qty,String url) {
        this.catagory = catagory;
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.qty = qty;
        this.url = url;
    }

    public String getCatagory() {
        return catagory;
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

    public double getQty() {
        return qty;
    }

    public String getUrl() {
        return url;
    }
}
