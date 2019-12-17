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
import android.widget.ImageView;
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

public class Settings extends AppCompatActivity {
    Toast toast;
    String name, phone, email, username, password;
    RequestQueue settingsQueue;
    TextView nameTxt, phoneTxt;

    EditText nameE, phoneE, emailE, usernameE, passE;

    GifImageView loadingView;
    LinearLayout mainParents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        loadingView = findViewById(R.id.loading_id);
        mainParents = findViewById(R.id.main_parent);

        nameE = findViewById(R.id.name);
        phoneE = findViewById(R.id.phone);
        emailE = findViewById(R.id.email);
        usernameE = findViewById(R.id.username);
        passE = findViewById(R.id.password);

        nameTxt = findViewById(R.id.name_txt);
        phoneTxt = findViewById(R.id.phone_txt);

        settingsQueue = Volley.newRequestQueue(this);

        loadOldSettings();

        Button saveChanges = findViewById(R.id.change_id);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName, fPhone, fEmail, fPass, fUsername;

                fName = nameE.getText().toString().trim();
                fPhone = phoneE.getText().toString().trim();
                fEmail = emailE.getText().toString().trim();
                fPass = passE.getText().toString().trim();
                fUsername = usernameE.getText().toString().trim();

                if (fName.matches("") || fPhone.matches("") || fEmail.matches("") || fPass.matches("") || fUsername.matches("")) {
                    showToast("Some Fields Are Empty");
                } else {

                    if (isThisValidEmail(fEmail)) {
                        if (isValidUsername(fUsername)) {
                            if (isValidMobile(fPhone)) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                if (!fName.matches(name)) {
                                    hashMap.put("name", fName);
                                }
                                if (!fPhone.matches(phone)) {
                                    hashMap.put("phone", fPhone);
                                }
                                if (!fEmail.matches(email)) {
                                    hashMap.put("email", fEmail);
                                }
                                if (!fPass.matches(password)) {
                                    hashMap.put("pass", fPass);
                                }
                                if (!fUsername.matches(username)) {
                                    hashMap.put("username", fUsername);
                                }

                                if (!hashMap.isEmpty()) {
                                    updateSettings(hashMap);
                                } else {
                                    showToast("You did'nt made any changes");
                                }
                            } else {
                                showToast("Please Enter A Valid Phone Number");
                            }
                        } else {
                            showToast("Username must not not contain white spaces");
                        }
                    } else {
                        showToast("Please Enter A Valid Email");
                    }
                }

            }
        });

        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void loadOldSettings() {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);
        final String userrname = sharedPreferences.getString("usrname", null);
        hashMap.put("username", userrname);

        final VolleyRequest volleyRequest = new VolleyRequest("settingsLoad.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        username = userrname;
                        name = jsonObject.getString("name");
                        phone = jsonObject.getString("phone");
                        email = jsonObject.getString("email");
                        password = jsonObject.getString("password");

                        nameTxt.setText(name);
                        phoneTxt.setText(phone);

                        nameE.setText(name);
                        phoneE.setText(phone);
                        emailE.setText(email);
                        passE.setText(password);
                        usernameE.setText(userrname);
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            showToast("User Not Found");
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
        settingsQueue.add(volleyRequest);
    }

    void updateSettings(HashMap<String, String> hashMap) {
        showLoading();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);
        String ussrnam = sharedPreferences.getString("usrname", null);
        hashMap.put("usernam", ussrnam);

        final VolleyRequest volleyRequest = new VolleyRequest("updateSettings.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        showToast("Successfully Updated.Please Log In Again");
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("allow", false);
                        editor.apply();
                        Intent loadLogIn = new Intent(Settings.this, LogIn.class);
                        loadLogIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loadLogIn);
                    } else {
                        switch (jsonObject.getString("status")) {
                            case "INVALID":
                                showToast("User Not Found");
                                break;
                            case "ALREADY":
                                showToast("This Username Is Taken,Try Another One");
                                break;
                            case "UPDATE ERROR":
                                showToast("Failed To Update");
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
        settingsQueue.add(volleyRequest);
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(Settings.this, msgForToast, Toast.LENGTH_LONG);
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
