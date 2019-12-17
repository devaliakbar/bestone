package com.mna.bestone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mna.bestone.Network.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pl.droidsonroids.gif.GifImageView;

public class ForgotPassword extends AppCompatActivity {
    RequestQueue resetQueue;
    Toast toast;
    EditText username, email;

    GifImageView loadingView;
    LinearLayout mainParents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        loadingView = findViewById(R.id.loading_id);
        mainParents = findViewById(R.id.main_parent);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);

        resetQueue = Volley.newRequestQueue(ForgotPassword.this);


        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button send = findViewById(R.id.msg);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fUsername = username.getText().toString().trim();
                String fEmail = email.getText().toString().trim();

                if (fUsername.matches("") && fEmail.matches("")) {
                    showToast("Please Enter At least One Field");
                } else {
                    if (fUsername.matches("")) {
                        fUsername = "NO";
                    }
                    if (fEmail.matches("")) {
                        fEmail = "NO";
                    }
                    getPassword(fEmail, fUsername);
                }
            }
        });

    }

    void getPassword(String email, String username) {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("email", email);
        hashMap.put("username", username);

        final VolleyRequest volleyRequest = new VolleyRequest("reset.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        showToast("We send your username and password to your email");
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            showToast("You Entered Information Are Not Registered.Please Sign Up Again");
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
        resetQueue.add(volleyRequest);
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(ForgotPassword.this, msgForToast, Toast.LENGTH_LONG);
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
