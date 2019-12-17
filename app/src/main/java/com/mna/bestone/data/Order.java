package com.mna.bestone.data;

import java.util.List;

public class Order {
    private int id;
    private int noItem;
    private String date;
    private String status;
    private double price;
    private List<Items> itemList;

    public Order(int id, String date, String status, double price, int noItem, List<Items> itemList) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.price = price;
        this.noItem = noItem;
        this.itemList = itemList;
    }

    public double getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public int getNoItem() {
        return noItem;
    }

    public List<Items> getItemList() {
        return itemList;
    }
}
