package com.mna.bestone.data;

import java.util.List;

public class CatagoryList {
    private String catagory;
    private List<CatItem> items;

    public CatagoryList(String catagory,  List<CatItem> items) {
        this.catagory = catagory;
        this.items = items;
    }
    public String getCatagory() {
        return catagory;
    }

    public List<CatItem> getItems() {
        return items;
    }
}
