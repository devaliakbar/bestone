package com.mna.bestone.Network;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;
import java.util.Locale;

public class LocationHelper {
    private Context context;

    public LocationHelper(Context context) {
        this.context = context;
    }

    public String getLocationName(Location location) {
        try {
            Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                addresses.size();
                return addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private double calculateDistanceFromThevara(Location location) {
        Location temp = new Location(LocationManager.GPS_PROVIDER);
        temp.setLatitude(9.931184);
        temp.setLongitude(76.299785);
        return (location.distanceTo(temp)) / 1000;
    }

    private double calculateDistanceFromBolgatty(Location location) {
        Location temp = new Location(LocationManager.GPS_PROVIDER);
        temp.setLatitude(10.009242);
        temp.setLongitude(76.261454);
        return (location.distanceTo(temp)) / 1000;
    }

    public boolean checkCurrentLocationAvailable(Location location) {
        double distance = calculateDistanceFromBolgatty(location);
        if (distance < 3) {
            return true;
        } else {
            distance = calculateDistanceFromThevara(location);
            return distance < 7;
        }
    }
}
