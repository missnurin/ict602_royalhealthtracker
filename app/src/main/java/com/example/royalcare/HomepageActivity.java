package com.example.royalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomepageActivity extends AppCompatActivity {

    private CardView cardProfile;
    private CardView cardMap;
    private CardView cardAbout;
    private CardView cardLogout;
    private TextView tvGreetings;

    private FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Check authentication state
        checkAuthenticationState();

        firebaseAuth = FirebaseAuth.getInstance();

        cardProfile = findViewById(R.id.cardProfile);
        cardMap = findViewById(R.id.cardMap);
        cardAbout = findViewById(R.id.cardAbout);
        cardLogout = findViewById(R.id.cardLogout);
        tvGreetings = findViewById(R.id.tvGreetings);

        updateWelcomeMessage();

        cardProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, ProfileActivity.class));

                showToast("Profile Clicked!");
            }
        });
        cardMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HomepageActivity", "Map button clicked");
                // Start MapsActivity
                startActivity(new Intent(HomepageActivity.this, MapsActivity.class));
            }
        });

        cardAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, AboutActivity.class));
                showToast("About Clicked!");
            }
        });
        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Toast.makeText(HomepageActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
                Intent intent = new Intent (HomepageActivity.this, MainActivity.class);

                //Clear stack to prevent user coming back to UserProfileActivity on pressing back button after Logging out
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();   //Close UserProfileActivity
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateWelcomeMessage() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // User is signed in
            String fullName = user.getDisplayName();

            if (fullName != null && !fullName.isEmpty()) {
                String welcomeMessage = "Greetings, " + fullName + "!";
                tvGreetings.setText(welcomeMessage);
            } else {
                // If full name is not available, you can fallback to the email
                String welcomeMessage = "Greetings, " + user.getEmail() + "!";
                tvGreetings.setText(welcomeMessage);
            }
        } else {
            // User is not signed in
            tvGreetings.setText("Welcome! Please sign in.");
        }
    }

    private void checkAuthenticationState() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // User is not signed in, redirect to login activity
            startActivity(new Intent(HomepageActivity.this, MainActivity.class));
            finish(); // Close current activity to prevent user from coming back
        }
    }



}