package com.mna.bestone;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.mna.bestone.Adapters.CatagoryItemAdapter;
import com.mna.bestone.Network.VolleyRequest;
import com.mna.bestone.data.CatItem;
import com.mna.bestone.data.JsonExtract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class Catagory extends AppCompatActivity implements CatagoryItemAdapter.CatagoryItemAdapterOnClickListener {
    RelativeLayout qtyView;
    EditText qty_text;
    Button a_button, m_button;
    Button qty_add, qty_cancel;

    Handler handlerText;
    boolean LAST_REACHED;
    RecyclerView itemRecyclerView;
    CatagoryItemAdapter mAdapter;

    String FROM_INTENT;
    private Toast toast;
    RequestQueue loadQueue;

    LinearLayout emptyView;

    EditText searchText;
    String filter = "name";

    LinearLayout filterFrame;

    RelativeLayout topFrame;


    RelativeLayout nameView, brandView;

    TextView nameT, brandT;
    View nameV, brandV;

    LinearLayout loadingItem;

    GifImageView loadingView;
    LinearLayout mainParents;

    List<CatItem> catagoryLists;
    int PAGE = 1;
    HashMap<String, String> filterHelperMap;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagory);

        progressBar = findViewById(R.id.load_more);

        loadingView = findViewById(R.id.loading_id);
        mainParents = findViewById(R.id.main_parent);

        loadingItem = findViewById(R.id.loading_item);

        nameT = findViewById(R.id.s_name_txt);
        brandT = findViewById(R.id.s_man_txt);

        nameV = findViewById(R.id.s_name_view);
        brandV = findViewById(R.id.s_man_view);

        topFrame = findViewById(R.id.top_frame);

        ImageView backPress = findViewById(R.id.back_icon_);
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FROM_INTENT = getIntent().getStringExtra("cat_name");
        TextView textView = findViewById(R.id.cat_text_head);
        textView.setText(FROM_INTENT);

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditext();
            }
        });


        FrameLayout backEditext = findViewById(R.id.back_icon);
        backEditext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEditext();
            }
        });


        searchText = findViewById(R.id.search_data);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                handlerText.removeCallbacksAndMessages(null);
                handlerText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String fromEditText = s.toString().trim();
                        if (!fromEditText.matches("")) {
                            loadItems(filter, fromEditText, 1);
                        }
                    }
                }, 2000);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        filterFrame = findViewById(R.id.filter_frame_id);
        brandView = findViewById(R.id.brandid);
        nameView = findViewById(R.id.nameid);

        brandView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter = "brand";
                setAllItemBackgroundDefault();
                brandV.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                brandT.setTextColor(getResources().getColor(R.color.colorAccent));

                String fromEditText = searchText.getText().toString().trim();
                if (!fromEditText.matches("")) {
                    loadItems(filter, fromEditText, 1);
                }
            }
        });

        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter = "name";
                setAllItemBackgroundDefault();
                nameV.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                nameT.setTextColor(getResources().getColor(R.color.colorAccent));

                String fromEditText = searchText.getText().toString().trim();
                if (!fromEditText.matches("")) {
                    loadItems(filter, fromEditText, 1);
                }
            }
        });

        emptyView = findViewById(R.id.empty_view);

        itemRecyclerView = findViewById(R.id.each_item_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemRecyclerView.setLayoutManager(layoutManager);
        itemRecyclerView.setHasFixedSize(true);
        mAdapter = new CatagoryItemAdapter(Catagory.this, Catagory.this);
        itemRecyclerView.setAdapter(mAdapter);

        loadQueue = Volley.newRequestQueue(Catagory.this);
        loadItems("", "", 1);

        mAdapter.setOnBottomReachedListener(new CatagoryItemAdapter.OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                if (!LAST_REACHED) {
                    PAGE++;
                    loadItems("", "", PAGE);
                }
            }
        });
        handlerText = new Handler();

        qtyView = findViewById(R.id.qty_dialog);
        qty_text = findViewById(R.id.qty_text);
        a_button = findViewById(R.id.a_but);
        m_button = findViewById(R.id.m_button);
        qty_add = findViewById(R.id.add_qty);
        qty_cancel = findViewById(R.id.cancel_qty);
    }


    private void hideEditext() {
        hideKepard();
        filter = "name";
        searchText.setText("");
        loadItems("", "", 1);
        setAllItemBackgroundDefault();
        nameV.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        nameT.setTextColor(getResources().getColor(R.color.colorAccent));

        filterFrame.setVisibility(View.GONE);
        topFrame.setVisibility(View.VISIBLE);
    }

    private void showEditext() {
        topFrame.setVisibility(View.GONE);
        filterFrame.setVisibility(View.VISIBLE);
    }

    private void setAllItemBackgroundDefault() {
        nameV.setBackgroundColor(getResources().getColor(R.color.white));
        brandV.setBackgroundColor(getResources().getColor(R.color.white));

        brandT.setTextColor(Color.parseColor("#808080"));
        nameT.setTextColor(Color.parseColor("#808080"));
    }


    void loadItems(final String selection, final String selectArg, final int pageNo) {
        if (pageNo == 1) {
            LAST_REACHED = false;
            loadingItem.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        HashMap<String, String> hashMap = new HashMap<>();
        if (selection.matches("") && selectArg.matches("")) {
            hashMap.put("filter", "no");
            hashMap.put("cat", FROM_INTENT);
        } else {
            hashMap.put("filter", "yes");
            hashMap.put("cat", FROM_INTENT);
            hashMap.put("selection", selection);
            hashMap.put("selectArg", selectArg);
        }

        if (pageNo != 1) {
            hashMap = filterHelperMap;
        } else {
            filterHelperMap = hashMap;
        }
        hashMap.put("page", pageNo + "");
        final VolleyRequest volleyRequest = new VolleyRequest("catagory.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingItem.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        List<CatItem> catItems = JsonExtract.extractCatagoryItemJson(jsonObject.getJSONArray("cursor"));
                        if (catItems != null) {
                            emptyView.setVisibility(View.GONE);
                            if (pageNo != 1) {
                                catagoryLists.addAll(catItems);
                                mAdapter.swapCursor(catagoryLists);
                            } else {
                                PAGE = 1;
                                catagoryLists = catItems;
                                mAdapter.swapCursor(catItems);
                            }
                        }
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            showToast("User Not Found");
                        } else if (jsonObject.getString("status").equals("EMPTYDATABASE")) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else if (jsonObject.getString("status").equals("finish")) {
                            LAST_REACHED = true;
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
        loadQueue.add(volleyRequest);
    }


    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(Catagory.this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onClick(final String currentItem) {

        showDialogue();
        qty_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> hashMap = new HashMap<>();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Catagory.this);
                String username = sharedPreferences.getString("usrname", null);
                hashMap.put("username", username);
                hashMap.put("itemId", currentItem);
                hashMap.put("qty", qty_text.getText().toString().trim());
                hideDialogue();
                addToCart(hashMap);
            }
        });
    }

    void showDialogue() {
        qtyView.setVisibility(View.VISIBLE);
        qtyView.bringToFront();
        mainParents.setAlpha((float) 0.4);
        disableViews(mainParents);


        a_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c_qty = Integer.parseInt(qty_text.getText().toString());
                if (c_qty != 100) {
                    c_qty++;
                }
                qty_text.setText("" + c_qty);
            }
        });

        m_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c_qty = Integer.parseInt(qty_text.getText().toString());
                if (c_qty != 1) {
                    c_qty--;
                }
                qty_text.setText("" + c_qty);
            }
        });

        qty_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialogue();
            }
        });
    }

    void hideDialogue() {
        qty_text.setText("1");
        enableViews(mainParents);
        mainParents.setAlpha((float) 1);
        qtyView.setVisibility(View.GONE);
    }

    void addToCart(HashMap<String, String> hashMap) {
        showLoading();
        final VolleyRequest volleyRequest = new VolleyRequest("addToCart.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        showToast("Added To Cart");
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            showToast("User Not Found");
                        } else if (jsonObject.getString("status").equals("ALREADY")) {
                            showToast("This Item Is Already Added");
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
        loadQueue.add(volleyRequest);
    }

    void hideKepard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
