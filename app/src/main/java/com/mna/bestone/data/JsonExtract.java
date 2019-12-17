package com.mna.bestone.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonExtract {
    public static List<CatagoryList> extractCatagoryJson(JSONArray catJson) throws JSONException {
        List<CatagoryList> returnCatList = new ArrayList<>();
        for (int i = 0; i < catJson.length(); i++) {
            JSONObject obj = catJson.getJSONObject(i);

            returnCatList.add(new CatagoryList(obj.getString("catagoryName"), extractCatItemJson(obj.getJSONArray("itemsCursor"))));

        }
        return returnCatList;
    }

    private static List<CatItem> extractCatItemJson(JSONArray catItemJson) throws JSONException {
        List<CatItem> returnCatItemList = new ArrayList<>();
        for (int i = 0; i < catItemJson.length(); i++) {
            JSONObject obj = catItemJson.getJSONObject(i);
            returnCatItemList.add(new CatItem(obj.getInt("pId"), obj.getString("pName"),
                    obj.getString("pBrand"), obj.getDouble("pPrice"), obj.getString("url")));
        }
        return returnCatItemList;
    }

    public static List<CatItem> extractCatagoryItemJson(JSONArray catItemJson) throws JSONException {
        List<CatItem> returnCatItemList = new ArrayList<>();
        for (int i = 0; i < catItemJson.length(); i++) {
            JSONObject obj = catItemJson.getJSONObject(i);
            returnCatItemList.add(new CatItem(obj.getInt("pId"), obj.getString("pName"),
                    obj.getString("pBrand"), obj.getDouble("pPrice"), obj.getString("url")));
        }
        return returnCatItemList;
    }

    public static List<Items> extractCartItemJson(JSONArray cartItemJson) throws JSONException {
        List<Items> returnCartItemList = new ArrayList<>();
        for (int i = 0; i < cartItemJson.length(); i++) {
            JSONObject obj = cartItemJson.getJSONObject(i);
            returnCartItemList.add(new Items(obj.getInt("pId"), obj.getString("pCat"), obj.getString("pName"),
                    obj.getString("pBrand"), obj.getDouble("pPrice"), obj.getDouble("pQty"), obj.getString("url")));
        }
        return returnCartItemList;
    }

    public static List<Order> extractOrderJson(JSONArray orderJson) throws JSONException {
        List<Order> returnOrderList = new ArrayList<>();
        for (int i = 0; i < orderJson.length(); i++) {
            JSONObject obj = orderJson.getJSONObject(i);
            returnOrderList.add(new Order(obj.getInt("id"), obj.getString("dates"), obj.getString("status"), obj.getDouble("price"),
                    obj.getInt("no_items"), extractCartItemJson(obj.getJSONArray("itemsCursor"))));
        }
        return returnOrderList;
    }
}
