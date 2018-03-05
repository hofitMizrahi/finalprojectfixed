package com.example.user.findplacesnearfinal;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by user on 05/03/2018.
 */

public class CalculateDistance {

    public CalculateDistance() {
    }

    public double getDistance(LatLng startP, LatLng endP){

            double distance = 0;
            Location locationA = new Location("A");
            locationA.setLatitude(startP.latitude);
            locationA.setLongitude(startP.longitude);
            Location locationB = new Location("B");
            locationB.setLatitude(endP.latitude);
            locationB.setLongitude(endP.longitude);
            distance = locationA.distanceTo(locationB);

            return distance / 1000;
    }
}
