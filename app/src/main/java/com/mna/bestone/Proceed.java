package com.mna.bestone;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.mna.bestone.Network.LocationHelper;
import com.mna.bestone.Network.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pl.droidsonroids.gif.GifImageView;

public class Proceed extends AppCompatActivity implements LocationListener{
    LocationManager locationManager;
    Toast toast;

    String amount, totItem, extraMsg;

    EditText name, phone, house, landmark, extra;

    GifImageView loadingView;
    LinearLayout mainParents;

    boolean allowForProceed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceed);

        setUpAutoPlace();

        loadingView = findViewById(R.id.loading_id);
        mainParents = findViewById(R.id.main_parent);

        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        amount = getIntent().getStringExtra("totalAmount");
        totItem = getIntent().getStringExtra("noOfItems");
        extraMsg = getIntent().getStringExtra("extraMsg");

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        house = findViewById(R.id.house);
        landmark = findViewById(R.id.landmark);
        extra = findViewById(R.id.extra);

        final Button proceed = findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().matches("") || phone.getText().toString().trim().matches("") ||
                        house.getText().toString().trim().matches("")) {
                    showToast("Please Enter The Basic Info Like Name,Phone,House");
                } else {
                    if (allowForProceed) {
                        proceedOrder();
                    } else {
                        showToast("Please Select Location Which Provide Delivery");
                    }
                }
            }
        });

        loadPreviosAddress();
        ImageView locateMe = findViewById(R.id.locate_me);
        locateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpLocation();
            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

    }

    void proceedOrder() {
        showLoading();
        RequestQueue proceedQueue = Volley.newRequestQueue(Proceed.this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Proceed.this);
        String username = sharedPreferences.getString("usrname", null);

        String landmarkS = landmark.getText().toString().trim();
        String extraS = extra.getText().toString().trim();
        if (landmarkS.matches("")) {
            landmarkS = "NA";
        }
        if (extraS.matches("")) {
            extraS = "NA";
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", username);
        hashMap.put("name", name.getText().toString().trim());
        hashMap.put("phone", phone.getText().toString().trim());
        hashMap.put("house", house.getText().toString().trim());
        hashMap.put("landmark", landmarkS);
        hashMap.put("extra", extraS);
        hashMap.put("extraMsg", extraMsg);

        hashMap.put("total", amount);
        hashMap.put("totNum", totItem);

        final VolleyRequest volleyRequest = new VolleyRequest("proceed.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        showToast("Your Order Has Been Proceed");
                        Intent homeIntent = new Intent(Proceed.this, Home.class);
                        startActivity(homeIntent);
                    } else {
                        if (jsonObject.getString("status").equals("INVALID")) {
                            showToast("User Not Found");
                        } else if (jsonObject.getString("status").equals("FAILED")) {
                            showToast("Failed To Proceed");
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
        proceedQueue.add(volleyRequest);
    }


    void loadPreviosAddress() {
        showLoading();
        RequestQueue proceedQueue = Volley.newRequestQueue(Proceed.this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Proceed.this);
        String username = sharedPreferences.getString("usrname", null);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", username);

        final VolleyRequest volleyRequest = new VolleyRequest("checkAddress.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                Log.i("Login Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        String Fname = jsonObject.getString("name");
                        String Fphone = jsonObject.getString("phone");
                        String Fhouse = jsonObject.getString("house");
                        String Flandmark = jsonObject.getString("landmark");
                        String Fextra = jsonObject.getString("extra");

                        if (!Fname.matches("null")) {
                            name.setText(Fname);
                        }

                        if (!Fphone.matches("null")) {
                            phone.setText(Fphone);
                        }

                        if (!Fhouse.matches("null")) {
                            house.setText(Fhouse);
                        }
                        if (!Flandmark.matches("null")) {
                            landmark.setText(Flandmark);
                        }

                        if (Fextra.matches("null")) {
                            setUpLocation();
                        } else {
                            extra.setText(Fextra);
                            allowForProceed = true;
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
        proceedQueue.add(volleyRequest);
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(Proceed.this, msgForToast, Toast.LENGTH_LONG);
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

    private void enableViews(ViewGroup layout) {
        layout.setEnabled(true);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                enableViews((ViewGroup) child);
            } else {
                child.setEnabled(true);
            }
        }
        extra.setEnabled(false);
    }



    private void performLocationAction(Location location, String locA) {
        LocationHelper locationHelper = new LocationHelper(Proceed.this);
        String locationName = locationHelper.getLocationName(location);
        extra.setText(locationName);
        if (locA != null) {
            extra.setText(locA);
        }
        if (!locationHelper.checkCurrentLocationAvailable(location)) {
            Toast.makeText(Proceed.this, "Current Location Is Not Available For Delivery", Toast.LENGTH_LONG).show();
            allowForProceed = false;
        } else {
            allowForProceed = true;
        }
    }

    private void setUpAutoPlace() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                temp.setLatitude(place.getLatLng().latitude);
                temp.setLongitude(place.getLatLng().longitude);
                performLocationAction(temp, place.getAddress().toString());
            }

            @Override
            public void onError(Status status) {

            }
        });
    }

    private void setUpLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        performLocationAction(location, null);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(Proceed.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}

