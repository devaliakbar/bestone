package com.mna.bestone;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.mna.bestone.Adapters.OrderAdapter;
import com.mna.bestone.Network.VolleyRequest;
import com.mna.bestone.data.Items;
import com.mna.bestone.data.JsonExtract;
import com.mna.bestone.data.Order;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class MyOrders extends AppCompatActivity implements OrderAdapter.OrderRemoveOnClickListener {
    Toast toast;
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    LinearLayout emptyView;

    RelativeLayout past, current;
    View pastV, currentV;
    TextView pastTxt, currentTxt;

    LinearLayout loadingItem;

    GifImageView loadingView;
    LinearLayout mainParents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

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

        pastTxt = findViewById(R.id.past_txt);
        currentTxt = findViewById(R.id.cur_txt);

        pastV = findViewById(R.id.s_past);
        currentV = findViewById(R.id.s_current);

        current = findViewById(R.id.current);
        past = findViewById(R.id.past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTxt.setTextColor(Color.parseColor("#808080"));
                currentV.setBackgroundColor(getResources().getColor(R.color.white));
                pastTxt.setTextColor(getResources().getColor(R.color.colorAccent));
                pastV.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                loadOrders("past");
            }
        });

        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pastTxt.setTextColor(Color.parseColor("#808080"));
                pastV.setBackgroundColor(getResources().getColor(R.color.white));
                currentTxt.setTextColor(getResources().getColor(R.color.colorAccent));
                currentV.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                loadOrders("order");
            }
        });

        emptyView = findViewById(R.id.empty_view);

        recyclerView = findViewById(R.id.order_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        orderAdapter = new OrderAdapter(MyOrders.this, MyOrders.this);
        recyclerView.setAdapter(orderAdapter);

        loadOrders("order");
    }

    @Override
    public void onRemove(String currentRaw) {
        cancelOrder(currentRaw);
    }

    void loadOrders(String type) {
        loadingItem.setVisibility(View.VISIBLE);
        RequestQueue orderQueue = Volley.newRequestQueue(MyOrders.this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyOrders.this);
        String username = sharedPreferences.getString("usrname", null);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", username);
        hashMap.put("type", type);

        final VolleyRequest volleyRequest = new VolleyRequest("order.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingItem.setVisibility(View.GONE);
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<Order> orderItems = JsonExtract.extractOrderJson(jsonObject.getJSONArray("cursor"));
                        if (orderItems != null) {
                            orderAdapter.swapCursor(orderItems);
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        if (jsonObject.getString("status").equals("EMPTYDATABASE")) {
                            emptyView.setVisibility(View.VISIBLE);
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
        orderQueue.add(volleyRequest);
    }

    void cancelOrder(String orderId) {
        showLoading();
        RequestQueue orderQueue = Volley.newRequestQueue(MyOrders.this);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", orderId);

        final VolleyRequest volleyRequest = new VolleyRequest("cancelOrder.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        loadOrders("order");
                        showToast("Your Order Has Been Canceled");
                    } else {
                        if (jsonObject.getString("status").equals("FAILED")) {
                            showToast("Failed To Cancel");
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
        orderQueue.add(volleyRequest);
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(MyOrders.this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
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
