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

public class SignUp extends AppCompatActivity {

    private Toast toast;
    EditText nameTxt, usernameTxt, emailTxt, phoneTxt, passwordTxt, repassword;
    RequestQueue signUpQueue;

    GifImageView loadingView;
    LinearLayout mainParents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loadingView = findViewById(R.id.loading_id);
        mainParents = findViewById(R.id.main_parent);

        nameTxt = findViewById(R.id.name);
        usernameTxt = findViewById(R.id.username);
        emailTxt = findViewById(R.id.email);
        phoneTxt = findViewById(R.id.phone);
        passwordTxt = findViewById(R.id.password);
        repassword = findViewById(R.id.repass);

        TextView logIn = findViewById(R.id.login);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntenty = new Intent(SignUp.this, LogIn.class);
                startActivity(loginIntenty);
            }
        });

        signUpQueue = Volley.newRequestQueue(SignUp.this);

        Button signUp = findViewById(R.id.signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignUp();
            }
        });
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(SignUp.this, msgForToast, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void doSignUp() {
        final String name = nameTxt.getText().toString().trim();
        final String username = usernameTxt.getText().toString().trim();
        String email = emailTxt.getText().toString().trim();
        String phone = phoneTxt.getText().toString().trim();
        final String password = passwordTxt.getText().toString().trim();
        String rePass = repassword.getText().toString().trim();

        if (name.matches("")) {
            showToast("Please Enter Your Name");
        } else {
            if (username.matches("")) {
                showToast("Please Enter Username");
            } else {
                if (email.matches("")) {
                    showToast("Please Enter Email");
                } else {
                    if (phone.matches("")) {
                        showToast("Please Enter Phone Number");
                    } else {
                        if (password.matches("")) {
                            showToast("Please Enter Password");
                        } else {
                            if (rePass.matches("")) {
                                showToast("Please Re Enter Your Password");
                            } else {
                                if (password.matches(rePass)) {
                                    if (isThisValidEmail(email)) {
                                        if (isValidMobile(phone)) {
                                            if (isValidUsername(username)) {

                                                //PERFORMING SIGN UP

                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("name", name);
                                                hashMap.put("username", username);
                                                hashMap.put("email", email);
                                                hashMap.put("phone", phone);
                                                hashMap.put("password", password);

                                                showLoading();
                                                final VolleyRequest volleyRequest = new VolleyRequest("signup.php", hashMap, new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        hideLoading();
                                                        Log.i("Login Response", response);
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            if (jsonObject.getBoolean("success")) {
                                                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SignUp.this);
                                                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                                                editor.putString("name", name);
                                                                editor.apply();

                                                                editor.putString("usrname", username);
                                                                editor.apply();

                                                                editor.putString("pswd", password);
                                                                editor.apply();

                                                                editor.putBoolean("allow", true);
                                                                editor.apply();

                                                                showToast("Successfully Signed Up");
                                                                Intent home = new Intent(SignUp.this, Home.class);
                                                                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(home);
                                                            } else {
                                                                if (jsonObject.getString("status").equals("INVALID")) {
                                                                    showToast("User Not Found");
                                                                } else if (jsonObject.getString("status").equals("ALREADY")) {
                                                                    showToast("This Username Is Taken,Try Another");
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
                                                signUpQueue.add(volleyRequest);


                                            } else {
                                                showToast("Username must not not contain white spaces");
                                            }
                                        } else {
                                            showToast("Please Enter A Valid Phone Number");
                                        }
                                    } else {
                                        showToast("Please Enter A Valid Email");
                                    }

                                } else {
                                    showToast("Password didn't match");
                                }
                            }
                        }
                    }
                }
            }
        }
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

    private boolean isThisValidEmail(String emaile) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emaile).matches();
    }

    private boolean isValidMobile(String phonee) {
        return android.util.Patterns.PHONE.matcher(phonee).matches();
    }

    private boolean isValidUsername(String usernamee) {
        return !usernamee.contains(" ");
    }
}
