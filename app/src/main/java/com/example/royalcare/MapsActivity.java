package com.example.royalcare;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.royalcare.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Vector<MarkerOptions> markerOptions;
    private DatabaseReference locationRef;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markerOptions = new Vector<>();

        // Initialize Firebase Database reference
        locationRef = FirebaseDatabase.getInstance().getReference().child("user_location");

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add markers for the predefined locations
        addPredefinedMarkers();

        // Enable user's location on the map
        enableMyLocation();

        // Move camera to a default location
        LatLng centerLocation = new LatLng(3.0, 100); // Default center location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLocation, 6));

        // Set a listener for the My Location button
        mMap.setOnMyLocationButtonClickListener(() -> {
            // Zoom in on the user's location
            startLocationUpdates();
            return true; // Return true to indicate that the listener has consumed the event
        });
    }


    private void addPredefinedMarkers() {
        // Clear existing markers from the map
        mMap.clear();

        // Add markers for the predefined locations
        markerOptions.add(new MarkerOptions().title("Unit Kesihatan Klinik UiTM")
                .position(new LatLng(6.445945084272475, 100.27979140811169))
                .snippet("Opens Everyday: 9am - 5pm\nClose: Saturday & Sunday"));


        markerOptions.add(new MarkerOptions().title("Klinik & Surgeri Sedhu Ram")
                .position(new LatLng(6.436802562539154, 100.30248732901065))
                .snippet("Opens Everyday: 6:30am - 11pm"));

        markerOptions.add(new MarkerOptions().title("NAURAH CLINIC")
                .position(new LatLng(6.4355509254587115, 100.29921579150096))
                .snippet("Opens Everyday: 9am - 10pm\nClose: Sunday"));

        markerOptions.add(new MarkerOptions().title("Pauh Health Clinic")
                .position(new LatLng(6.438450771406514, 100.30797807794505))
                .snippet("Contact: 049864737"));

        markerOptions.add(new MarkerOptions().title("Arau Health Clinic")
                .position(new LatLng(6.432583799037437, 100.27127914021968))
                .snippet("Opens Everyday: 8am - 5pm\nClose: Saturday & Sunday"));

        markerOptions.add(new MarkerOptions().title("KLINIK REMEDIC PAUH")
                .position(new LatLng(6.438791928664587, 100.30613146087))
                .snippet("Opens Everyday: 8am - 10pm"));

        markerOptions.add(new MarkerOptions().title("Arau Dental Clinic")
                .position(new LatLng(6.4329574593258165, 100.27086256388054))
                .snippet("Opens Everyday: 8am - 5pm\nClose: Saturday & Sunday"));

        markerOptions.add(new MarkerOptions().title("KLINIK PERGIGIAN KHOO DAN ED CHEONG")
                .position(new LatLng(6.427462993143705, 100.27253820468756))
                .snippet("Opens Everyday: 9am - 2pm\n3pm - 9pm"));

        markerOptions.add(new MarkerOptions().title("Klinik Pergigian Izznara Jejawi")
                .position(new LatLng(6.4497581579705585, 100.24117474335226))
                .snippet("Opens Everyday: 9:30am - 1pm\n2pm - 6:30pm"));

        markerOptions.add(new MarkerOptions().title("KLINIK PERGIGIAN UTC KANGAR")
                .position(new LatLng(6.437433776891138, 100.2045983030879))
                .snippet("Opens Everyday: 8am - 5pm"));

        markerOptions.add(new MarkerOptions().title("Kampung Gial Health Clinic")
                .position(new LatLng(6.465361259438286, 100.27555471665289))
                .snippet("Opens Everyday: 8am - 5pm\nClose: Saturday & Sunday"));

        markerOptions.add(new MarkerOptions().title("Pusat Rawatan Pakar Pergigian Kangar")
                .position(new LatLng(6.448393557060528, 100.216970440746))
                .snippet("Opens Everyday: 10am - 6pm\nClose: Sunday"));

        markerOptions.add(new MarkerOptions().title("Klinik Haiwan Calico")
                .position(new LatLng(6.427064969485852, 100.27501366491674))
                .snippet("Opens Everyday: 11am - 6:30pm"));

        // Add markers to the map
        for (MarkerOptions option : markerOptions) {
            mMap.addMarker(option);
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Got last known location
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        // Check if the map is zoomed out
                        if (mMap.getCameraPosition().zoom < 15) {
                            // If the map is zoomed out, zoom in on the user's location
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        } else {
                            // If the map is already zoomed in, simply move the camera to the user's location
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        }

                        // Write the user's location to Firebase
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            String fullName = user.getDisplayName(); // Get user's full name
                            String userAgent = System.getProperty("http.agent"); // Get user's user agent
                            writeLocationToFirebase(userId, location.getLatitude(), location.getLongitude(), fullName, userAgent);
                        }
                    }
                });
    }



    private void stopLocationUpdates() {
        // No need to stop location updates for FusedLocationProviderClient in this case
    }

    private void writeLocationToFirebase(String userId, double latitude, double longitude, String fullName, String userAgent) {
        DatabaseReference userLocationRef = locationRef.child(userId);
        userLocationRef.child("latitude").setValue(latitude);
        userLocationRef.child("longitude").setValue(longitude);
        userLocationRef.child("fullName").setValue(fullName); // Store full name
        userLocationRef.child("userAgent").setValue(userAgent); // Store user agent

        // Get current date and time
        String dateTime = getCurrentDateTime();
        userLocationRef.child("dateTime").setValue(dateTime); // Store date and time
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

}
