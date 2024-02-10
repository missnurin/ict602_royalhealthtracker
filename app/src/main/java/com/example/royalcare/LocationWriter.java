package com.example.royalcare;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationWriter {

    private DatabaseReference locationRef;

    public LocationWriter() {
        // Initialize Firebase Database reference
        locationRef = FirebaseDatabase.getInstance().getReference().child("user_location");
    }

    public void writeLocationToFirebase(double latitude, double longitude, String fullName, String userAgent) {
        // Create a unique key for the location
        String locationKey = locationRef.push().getKey();
        if (locationKey != null) {
            // Get current date and time
            String dateTime = getCurrentDateTime();

            // Write the location data to Firebase
            locationRef.child(locationKey).child("latitude").setValue(latitude);
            locationRef.child(locationKey).child("longitude").setValue(longitude);
            locationRef.child(locationKey).child("fullName").setValue(fullName);
            locationRef.child(locationKey).child("userAgent").setValue(userAgent);
            locationRef.child(locationKey).child("dateTime").setValue(dateTime); // Store date and time
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }
}
