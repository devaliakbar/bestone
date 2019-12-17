package com.mna.bestone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import static com.mna.bestone.data.Version.BEST_ONE_VERSION;

public class LogIn extends AppCompatActivity {

    private Toast toast;
    RequestQueue loginQueue;
    EditText usernameText, passwordText;
    CheckBox remember;

    GifImageView loadingView;
    LinearLayout mainParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        loadingView = findViewById(R.id.loading_id);
        mainParent = findViewById(R.id.main_parent);

        checkForRemember();

        remember = findViewById(R.id.remember_id);

        TextView forgotPassword = findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPasswordIntent = new Intent(LogIn.this, ForgotPassword.class);
                startActivity(forgotPasswordIntent);
            }
        });

        usernameText = findViewById(R.id.usrname);
        passwordText = findViewById(R.id.password);


        Button signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupActivity = new Intent(LogIn.this, SignUp.class);
                startActivity(signupActivity);
            }
        });

        Button logIn = findViewById(R.id.login);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameText.getText().toString().trim().matches("")) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(LogIn.this, "Please Enter Your Username", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    if (passwordText.getText().toString().trim().matches("")) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(LogIn.this, "Please Enter Your Password", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        performLogIn(usernameText.getText().toString().trim(), passwordText.getText().toString().trim());
                    }
                }
            }
        });

        loginQueue = Volley.newRequestQueue(LogIn.this);

    }

    private void performLogIn(final String username, final String password) {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("version", "" + BEST_ONE_VERSION);

        hashMap.put("username", username);
        hashMap.put("password", password);


        final VolleyRequest volleyRequest = new VolleyRequest("login.php",
                hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LogIn.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (remember.isChecked()) {
                            editor.putBoolean("allow", true);
                        } else {
                            editor.putBoolean("allow", false);
                        }
                        editor.apply();

                        editor.putString("name", jsonObject.getString("name"));
                        editor.apply();

                        editor.putString("usrname", username);
                        editor.apply();

                        editor.putString("pswd", password);
                        editor.apply();

                        Intent home = new Intent(LogIn.this, Home.class);
                        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(home);
                    } else {
                        switch (jsonObject.getString("status")) {
                            case "INVALID":
                                if (toast != null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(LogIn.this, "User Not Found", Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                            case "U":
                                if (toast != null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(LogIn.this, "Username is incorrect", Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                            case "P":
                                if (toast != null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(LogIn.this, "Password is incorrect", Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                            case "VERSION":
                                Intent loadUpdate = new Intent(LogIn.this, Update.class);
                                loadUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(loadUpdate);
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(LogIn.this, "Bad Response From Server:" + response, Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(LogIn.this, "Server Error", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof TimeoutError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(LogIn.this, "Connection Timed Out", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (error instanceof NetworkError) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(LogIn.this, "Bad Network Connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        loginQueue.add(volleyRequest);
    }

    void checkForRemember() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean allowToHome = sharedPreferences.getBoolean("allow", false);
        if (allowToHome) {
            Intent loadHome = new Intent(LogIn.this, Home.class);
            loadHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loadHome);
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("usrname", null);
            editor.apply();
            editor.putString("pswd", null);
            editor.apply();
            editor.putString("name", null);
            editor.apply();
        }
    }

    void showLoading() {
        mainParent.setAlpha((float) 0.7);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.bringToFront();

        disableViews(mainParent);
    }

    void hideLoading() {
        loadingView.setVisibility(View.GONE);
        mainParent.setAlpha((float) 1);

        enableViews(mainParent);
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
