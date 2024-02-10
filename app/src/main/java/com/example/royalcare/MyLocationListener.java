package com.example.royalcare;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.example.royalcare.LocationData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyLocationListener implements LocationListener {

    private static final String TAG = "MyLocationListener";
    private DatabaseReference locationRef;
    private String fullName;
    private String userAgent;

    public MyLocationListener(String fullName) {
        // Initialize Firebase Database reference
        locationRef = FirebaseDatabase.getInstance().getReference().child("user_location");
        this.fullName = fullName;

        // Retrieve the user agent
        userAgent = System.getProperty("http.agent");
    }

    @Override
    public void onLocationChanged(Location location) {
        // Called when the user's location changes
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Log.d(TAG, "Location changed: " + latitude + ", " + longitude + ", Full Name: " + fullName + ", User Agent: " + userAgent);

            // Write the location to Firebase
            writeLocationToFirebase(latitude, longitude, fullName, userAgent);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Called when the status of the location provider changes
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Called when the location provider is enabled
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Called when the location provider is disabled
    }

    private void writeLocationToFirebase(double latitude, double longitude, String fullName, String userAgent) {
        // Create a unique key for the location
        String locationKey = locationRef.push().getKey();
        if (locationKey != null) {
            // Prepend "user agent:" to the user agent string
            String userAgentWithLabel = "user agent " + userAgent;
            // Get current date and time
            String dateTime = getCurrentDateTime();
            // Create a location object with latitude, longitude, full name, user agent, and date/time
            LocationData locationData = new LocationData(latitude, longitude, fullName, userAgentWithLabel, dateTime);
            // Write the location data to Firebase
            locationRef.child(fullName).setValue(locationData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Location data written to Firebase"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error writing location data to Firebase", e));
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }
}
