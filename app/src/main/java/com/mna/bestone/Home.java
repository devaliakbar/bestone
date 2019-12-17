package com.mna.bestone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
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
import com.mna.bestone.Adapters.CatagoryAdapter;
import com.mna.bestone.Adapters.HomeBanner;
import com.mna.bestone.Adapters.ItemAdapter;
import com.mna.bestone.CustomView.OnSwipeTouchListener;
import com.mna.bestone.Network.VolleyRequest;
import com.mna.bestone.data.CatItem;
import com.mna.bestone.data.CatagoryList;
import com.mna.bestone.data.JsonExtract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

import static com.mna.bestone.data.Version.BEST_ONE_VERSION;

public class Home extends AppCompatActivity implements CatagoryAdapter.CatagoryAdapterOnClickListener, ItemAdapter.ItemAdapterOnClickListener {
    Handler handlerText;
    boolean LAST_REACHED;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;

    ViewPager viewPager;
    HomeBanner sliderAdapter;
    LinearLayout dotLinear;
    TextView[] dots;


    private Toast toast;
    RequestQueue loadQueue;

    EditText searchText;
    String filter = "name";

    RecyclerView itemRecyclerView;
    CatagoryAdapter mAdapter;

    LinearLayout emptyView;

    RelativeLayout topFrame;

    LinearLayout filterFrame;

    RelativeLayout catView, nameView, brandView;

    TextView nameT, brandT, catT;
    View nameV, brandV, catV;


    LinearLayout navBar;
    LinearLayout mainParent;


    TextView nameInNavigation;

    LinearLayout loadingItem;

    GifImageView loadingView;
    RelativeLayout mainParents;

    RelativeLayout qtyView;

    EditText qty_text;
    Button a_button, m_button;
    Button qty_add, qty_cancel;

    List<CatagoryList> catagoryLists;
    int PAGE = 1;
    HashMap<String, String> filterHelperMap;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBar = findViewById(R.id.load_more);

        loadQueue = Volley.newRequestQueue(Home.this);
        checkForUserAndUpdate();
    }

    private void hideEditext() {
        hideKepard();
        filter = "name";
        setAllItemBackgroundDefault();
        nameV.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        nameT.setTextColor(getResources().getColor(R.color.colorAccent));
        searchText.setText("");
        loadItems("","",1);
        filterFrame.setVisibility(View.GONE);
        topFrame.setVisibility(View.VISIBLE);
    }

    private void showEditext() {
        filterFrame.setVisibility(View.VISIBLE);
        topFrame.setVisibility(View.GONE);
    }

    private void setAllItemBackgroundDefault() {
        catV.setBackgroundColor(getResources().getColor(R.color.white));
        nameV.setBackgroundColor(getResources().getColor(R.color.white));
        brandV.setBackgroundColor(getResources().getColor(R.color.white));

        brandT.setTextColor(Color.parseColor("#808080"));
        catT.setTextColor(Color.parseColor("#808080"));
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
            hashMap.put("version", "" + BEST_ONE_VERSION);
            if (selection.matches("") && selectArg.matches("")) {
                hashMap.put("filter", "no");
            } else {
                hashMap.put("filter", "yes");
                hashMap.put("selection", selection);
                hashMap.put("selectArg", selectArg);
            }
            if (pageNo != 1) {
                hashMap = filterHelperMap;
            } else {
                filterHelperMap = hashMap;
            }

            hashMap.put("pageNo", "" + pageNo);
            final VolleyRequest volleyRequest = new VolleyRequest("home.php", hashMap, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loadingItem.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Log.i("Login Response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            List<CatagoryList> catItems = JsonExtract.extractCatagoryJson(jsonObject.getJSONArray("cursor"));
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
                            switch (jsonObject.getString("status")) {
                                case "INVALID":
                                    showToast("User Not Found");
                                    break;
                                case "EMPTYDATABASE":
                                    emptyView.setVisibility(View.VISIBLE);
                                    break;
                                case "VERSION":
                                    Intent loadUpdate = new Intent(Home.this, Update.class);
                                    loadUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(loadUpdate);
                                    break;
                                case "finish":
                                    LAST_REACHED = true;
                                    break;
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
        toast = Toast.makeText(Home.this, msgForToast, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(String currentRaw) {
        Intent catIntent = new Intent(Home.this, Catagory.class);
        catIntent.putExtra("cat_name", currentRaw);
        startActivity(catIntent);
    }

    @Override
    public void onItemClick(final String currentItem) {

        showDialogue();
        qty_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> hashMap = new HashMap<>();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Home.this);
                String username = sharedPreferences.getString("usrname", null);
                hashMap.put("username", username);
                hashMap.put("itemId", currentItem);
                hashMap.put("qty", qty_text.getText().toString().trim());
                hideDialogue();
                addToCart(hashMap);
            }
        });
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

    void setName() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String nameFromPreference = sharedPreferences.getString("name", null);
        nameInNavigation.setText(nameFromPreference);
    }


    void showLoading() {
        mainParents.setAlpha((float) 0.7);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.bringToFront();

        disableViews(mainParents, true);
    }

    void hideLoading() {
        loadingView.setVisibility(View.GONE);
        mainParents.setAlpha((float) 1);

        enableViews(mainParents);
    }


    private void disableViews(ViewGroup layout, boolean me) {
        if (me) {
            layout.setEnabled(false);
        }
        itemRecyclerView.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disableViews((ViewGroup) child, true);
            } else {
                child.setEnabled(false);
            }
        }
    }

    private void enableViews(ViewGroup layout) {
        layout.setEnabled(true);
        itemRecyclerView.setEnabled(true);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                enableViews((ViewGroup) child);
            } else {
                child.setEnabled(true);
            }
        }
    }


    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    void addDotsIndicator(int position) {
        dots = new TextView[3];
        dotLinear.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(40);
            dots[i].setTextColor(getResources().getColor(R.color.white_));
            dotLinear.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    void setUpBanner(String[] bannerText) {

        viewPager = findViewById(R.id.page_viewer);
        sliderAdapter = new HomeBanner(this, bannerText);
        viewPager.setAdapter(sliderAdapter);


        dotLinear = findViewById(R.id.dot_lenear);
        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == 3) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);


    }

    void initiallSetup(String[] bannerText) {
        setUpBanner(bannerText);

        loadingView = findViewById(R.id.loading_id);
        mainParents = findViewById(R.id.main_parent);

        loadingItem = findViewById(R.id.loading_item);

        nameInNavigation = findViewById(R.id.name_nav);
        setName();

        mainParent = findViewById(R.id.main_parent_id);
        mainParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navBar.setVisibility(View.GONE);
                mainParent.setAlpha((float) 1);
                enableViews(mainParent);
            }
        });

        ImageView menu = findViewById(R.id.menu_icon);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainParent.setAlpha((float) 0.3);
                disableViews(mainParent, false);
                navBar.setVisibility(View.VISIBLE);
            }
        });

        navBar = findViewById(R.id.nav_bar);

        navBar.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                navBar.setVisibility(View.GONE);
                mainParent.setAlpha((float) 1);
                enableViews(mainParent);
            }
        });


        topFrame = findViewById(R.id.top_frame);

        nameT = findViewById(R.id.s_name_txt);
        nameV = findViewById(R.id.s_name_view);

        brandT = findViewById(R.id.s_man_txt);
        brandV = findViewById(R.id.s_man_view);

        catT = findViewById(R.id.s_cat_txt);
        catV = findViewById(R.id.s_cat_view);

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditext();
            }
        });

        FrameLayout backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
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


        catView = findViewById(R.id.catid);
        brandView = findViewById(R.id.brandid);
        nameView = findViewById(R.id.nameid);


        catView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter = "cat";
                setAllItemBackgroundDefault();
                catV.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                catT.setTextColor(getResources().getColor(R.color.colorAccent));

                String fromEditText = searchText.getText().toString().trim();
                if (!fromEditText.matches(""))  {
                    loadItems(filter, fromEditText, 1);
                }
            }
        });

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


        ImageView cartButton = findViewById(R.id.view_cart);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Cart.class);
                startActivity(intent);
            }
        });

        LinearLayout settingsButton = findViewById(R.id.view_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navBar.setVisibility(View.GONE);
                mainParent.setAlpha((float) 1);
                enableViews(mainParent);
                Intent intent = new Intent(Home.this, Settings.class);
                startActivity(intent);
            }
        });


        LinearLayout myOrderButton = findViewById(R.id.view_order);
        myOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navBar.setVisibility(View.GONE);
                mainParent.setAlpha((float) 1);
                enableViews(mainParent);
                Intent intent = new Intent(Home.this, MyOrders.class);
                startActivity(intent);
            }
        });

        LinearLayout logOut = findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Home.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("allow", false);
                editor.apply();
                Intent loadLogIn = new Intent(Home.this, LogIn.class);
                loadLogIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loadLogIn);
            }
        });

        emptyView = findViewById(R.id.empty_view);

        itemRecyclerView = findViewById(R.id.home_items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemRecyclerView.setLayoutManager(layoutManager);
        itemRecyclerView.setHasFixedSize(true);
        mAdapter = new CatagoryAdapter(Home.this, Home.this, Home.this);
        itemRecyclerView.setAdapter(mAdapter);

        loadItems("", "", 1);

        qtyView = findViewById(R.id.qty_dialog);
        qty_text = findViewById(R.id.qty_text);
        a_button = findViewById(R.id.a_but);
        m_button = findViewById(R.id.m_button);
        qty_add = findViewById(R.id.add_qty);
        qty_cancel = findViewById(R.id.cancel_qty);


        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hideEditext();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mAdapter.setOnBottomReachedListener(new CatagoryAdapter.OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                if (!LAST_REACHED) {
                    PAGE++;
                    loadItems("", "", PAGE);
                }
            }
        });
        handlerText = new Handler();
    }

    void checkForUserAndUpdate() {
        HashMap<String, String> hashMap = new HashMap<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Home.this);
        String username = sharedPreferences.getString("usrname", null);
        hashMap.put("username", username);
        hashMap.put("password", sharedPreferences.getString("pswd", null));
        hashMap.put("version", "" + BEST_ONE_VERSION);
        final VolleyRequest volleyRequest = new VolleyRequest("initial.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        initiallSetup(new String[]{jsonObject.getString("o"), jsonObject.getString("t"), jsonObject.getString("th")});
                    } else {
                        if (jsonObject.getString("status").equals("USER")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Home.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("allow", false);
                            editor.apply();
                            Intent loadUpdate = new Intent(Home.this, LogIn.class);
                            loadUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(loadUpdate);
                            showToast("Please Sign In");
                        }
                        if (jsonObject.getString("status").equals("VERSION")) {
                            Intent loadUpdate = new Intent(Home.this, Update.class);
                            loadUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(loadUpdate);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Bad Response From Server" + response);

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

    void showDialogue() {
        qtyView.setVisibility(View.VISIBLE);
        qtyView.bringToFront();
        mainParents.setAlpha((float) 0.4);
        disableViews(mainParents, true);


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
}
