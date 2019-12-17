package com.mna.bestone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mna.bestone.Adapters.CartAdapter;
import com.mna.bestone.Network.VolleyRequest;
import com.mna.bestone.data.Items;
import com.mna.bestone.data.JsonExtract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class Cart extends AppCompatActivity implements CartAdapter.CartAdapterRemoveClickListener {
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    RequestQueue cartQueue;
    Toast toast;
    TextView totNumView, totalView;
    LinearLayout emptyView;

    LinearLayout loadingItem;

    GifImageView loadingView;
    LinearLayout mainParents;
    TextView deleveryCharge,totAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        totAmount = findViewById(R.id.ttot);

        deleveryCharge = findViewById(R.id.delevery);

        loadingView = findViewById(R.id.loading_id);
        mainParents = findViewById(R.id.main_parent);

        loadingItem = findViewById(R.id.loading_item);

        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        emptyView = findViewById(R.id.empty_view);
        totNumView = findViewById(R.id.item_no_txt);
        totalView = findViewById(R.id.total_txt);


        Button orderButton = findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String totalItem = totNumView.getText().toString().trim();
                if (totalItem.matches("0")) {
                    showToast("Cart Is Empty");
                } else {
                    String extraMsg;
                    extraMsg = "NA";
                    Intent deleveryIntent = new Intent(Cart.this, Proceed.class);
                    deleveryIntent.putExtra("noOfItems", totalItem);
                    deleveryIntent.putExtra("totalAmount", totalView.getText().toString());
                    deleveryIntent.putExtra("extraMsg", extraMsg);
                    startActivity(deleveryIntent);
                }
            }
        });


        cartQueue = Volley.newRequestQueue(Cart.this);

        recyclerView = findViewById(R.id.cart_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        cartAdapter = new CartAdapter(Cart.this, Cart.this);
        recyclerView.setAdapter(cartAdapter);

        loadCartItems();
    }

    void loadCartItems() {
        loadingItem.setVisibility(View.VISIBLE);
        HashMap<String, String> hashMap = new HashMap<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Cart.this);
        String username = sharedPreferences.getString("usrname", null);
        hashMap.put("username", username);

        final VolleyRequest volleyRequest = new VolleyRequest("cart.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingItem.setVisibility(View.GONE);
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<Items> cartItems = JsonExtract.extractCartItemJson(jsonObject.getJSONArray("cursor"));
                        if (cartItems != null) {
                            int amnt = Integer.parseInt(jsonObject.getString("total"));
                            totAmount.setText("" + amnt);
                            deleveryCharge.setText("0");
                          /*  if (amnt < 300) {
                                amnt = amnt + 30;
                                deleveryCharge.setText("30");
                            }*/
                            totalView.setText("" + amnt);
                            totNumView.setText(jsonObject.getString("totno"));

                            cartAdapter.swapCursor(cartItems);
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            showToast("User Not Found");
                        } else if (jsonObject.getString("status").equals("EMPTYDATABASE")) {
                            emptyView.setVisibility(View.VISIBLE);
                            totalView.setText("0");
                            deleveryCharge.setText("0");
                            totAmount.setText("0");
                            totNumView.setText("0");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Bad Response From Server");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    showToast("Server Error");
                } else if (error instanceof TimeoutError) {
                    showToast("Connection Timed Out");
                } else if (error instanceof NetworkError) {
                    showToast("Bad Network Connection");
                }
            }
        });
        cartQueue.add(volleyRequest);
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(Cart.this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onItemRemove(String currentItemId) {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Cart.this);
        String username = sharedPreferences.getString("usrname", null);
        hashMap.put("username", username);
        hashMap.put("itemId", currentItemId);

        final VolleyRequest volleyRequest = new VolleyRequest("removecart.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        showToast("Successfully Removed");
                        loadCartItems();
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            showToast("User Not Found");
                        } else if (jsonObject.getString("status").equals("FAILED")) {
                            showToast("Failed To Remove");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Bad Response From ServerAAA" + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    showToast("Server Error");
                } else if (error instanceof TimeoutError) {
                    showToast("Connection Timed Out");
                } else if (error instanceof NetworkError) {
                    showToast("Bad Network Connection");
                }
            }
        });
        cartQueue.add(volleyRequest);
    }

    void showLoading() {
        mainParents.setAlpha((float) 0.7);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.bringToFront();

        disableViews(mainParents);
    }

    void hideLoading() {
        loadingView.setVisibility(View.GONE);
        mainParents.setAlpha((float) 1);

        enableViews(mainParents);
    }


    private static void disableViews(ViewGroup layout) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disableViews((ViewGroup) child);
            } else {
                child.setEnabled(false);
            }
        }
    }

    private static void enableViews(ViewGroup layout) {
        layout.setEnabled(true);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                enableViews((ViewGroup) child);
            } else {
                child.setEnabled(true);
            }
        }
    }

}
