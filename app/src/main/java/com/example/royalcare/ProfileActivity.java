package com.example.royalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
public class ProfileActivity extends AppCompatActivity {

    private TextView textViewFullName, textViewEmail, textViewBirth, textViewGender, textViewPhone;
    private String fullName, email, birth, gender, phone;
    private ImageView imageView;
    private FirebaseAuth firebaseAuth;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Retrieve user ID from intent
        String userID = getIntent().getStringExtra("userID");


        Toolbar welcomeToolbar = findViewById(R.id.converterToolbar);
        setSupportActionBar(welcomeToolbar);
        getSupportActionBar().setTitle("PROFILE");

        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewBirth = findViewById(R.id.textView_show_dob);
        textViewGender = findViewById(R.id.textView_show_gender);
        textViewPhone = findViewById(R.id.textView_show_mobile);

        //Set OnCLickListener on ImageView to Open UploadProfilePicActivity
        imageView = findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, UploadProfilePicActivity.class);
            startActivity(intent);
        });

        swipeToRefresh();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(ProfileActivity.this, "Something went wrong! User's details are not available at the moment", Toast.LENGTH_LONG).show();
        } else {
            showUserProfile(firebaseUser);
        }

    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //extracting user reference from database for "registered user"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered_User").child(userID);
        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ReadwriteUserDetails ReadUserDetails = snapshot.getValue(ReadwriteUserDetails.class);
                    if(ReadUserDetails != null){
                        fullName = ReadUserDetails.fullName;
                        email = ReadUserDetails.email;
                        birth = ReadUserDetails.birth;
                        gender = ReadUserDetails.gender;
                        phone = ReadUserDetails.phone;

                        textViewFullName.setText( fullName );
                        textViewEmail.setText( email );
                        textViewBirth.setText( birth );
                        textViewGender.setText( gender );
                        textViewPhone.setText( phone );

                        //Set User DP (After user has uploaded)
                        Uri uri = firebaseUser.getPhotoUrl();

                        //ImageViewer setImagerURI() should not be used with regular URIs. So we are using Picasso
                        Picasso.with(ProfileActivity.this).load(uri).into(imageView);

                    }else {
                        Toast.makeText(ProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ProfileActivity.this, "DataSnapshot does not exist", Toast.LENGTH_SHORT).show();
                }}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Database Error", Toast.LENGTH_LONG).show();

            }
        });
    }
    private void swipeToRefresh() {
        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here. Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                overridePendingTransition(0, 0);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }



    //create action bar menu
    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu item
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_menu, menu);
        return true;
    }

    //when any menu selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh) { // Corrected ID for refresh menu item
            // Handle refresh action here
            // For example, you can call the swipeToRefresh() method or any other action you want to perform when refreshing
            swipeContainer.setRefreshing(true); // Assuming swipeContainer is correctly initialized
            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.menu_update_profile){
            Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout){
            firebaseAuth.signOut();
            Toast.makeText(ProfileActivity.this,"Logged Out", Toast.LENGTH_LONG).show();
            Intent intent= new Intent (ProfileActivity.this, LoginActivity.class);

            //clear stackk to prevent user coming to logged in
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

