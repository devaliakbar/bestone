package com.mna.bestone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.mna.bestone.Adapters.SliderAdapter;
import com.mna.bestone.Network.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.mna.bestone.data.Version.BEST_ONE_VERSION;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    SliderAdapter sliderAdapter;

    LinearLayout dotLinear;
    TextView[] dots;
    Button continueButton;

    ImageView startImage;
    RelativeLayout firstTimeLayout;
    boolean isItFirstTime;

    Toast toast;
    RequestQueue loadQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startImage = findViewById(R.id.start_image);
        firstTimeLayout = findViewById(R.id.first_view);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isItFirstTime = sharedPreferences.getBoolean("first_time", true);

        if (isItFirstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("first_time", false);
            editor.apply();

            firstTimeLayout.setVisibility(View.VISIBLE);

            continueButton = findViewById(R.id.continue_but);
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loadLogIn = new Intent(MainActivity.this, LogIn.class);
                    loadLogIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loadLogIn);
                }
            });


            viewPager = findViewById(R.id.page_viewer);
            sliderAdapter = new SliderAdapter(this);
            viewPager.setAdapter(sliderAdapter);

            dotLinear = findViewById(R.id.dot_lenear);
            addDotsIndicator(0);
            viewPager.addOnPageChangeListener(viewListener);
        } else {
            startImage.setVisibility(View.VISIBLE);
        }
        loadQueue = Volley.newRequestQueue(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForUpdate();
    }

    void addDotsIndicator(int position) {
        dots = new TextView[3];
        dotLinear.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(55);
            dots[i].setTextColor(getResources().getColor(R.color.white_));
            dotLinear.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            if (i == 2) {
                continueButton.setVisibility(View.VISIBLE);
            } else {
                continueButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

void checkForUpdate(){
    HashMap<String, String> hashMap = new HashMap<>();
    hashMap.put("version", "" + BEST_ONE_VERSION);
    final VolleyRequest volleyRequest = new VolleyRequest("update.php", hashMap, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.i("Login Response", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("success")) {
                    if (!isItFirstTime) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loadLogIn = new Intent(MainActivity.this, LogIn.class);
                                loadLogIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(loadLogIn);
                            }
                        }, 2000);
                    }
                } else {
                    Intent loadUpdate = new Intent(MainActivity.this, Update.class);
                    loadUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loadUpdate);
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
        toast = Toast.makeText(MainActivity.this, msgForToast, Toast.LENGTH_SHORT);
        toast.show();
    }

}
