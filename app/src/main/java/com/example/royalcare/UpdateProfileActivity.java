package com.example.royalcare;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDoB, editTextUpdateMobile;
    private TextView editText_update_profile_email;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textFullName, textBirth, textGender, textPhone, textEmail;
    private FirebaseAuth authProfile;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Retrieve user ID from intent
        String userID = getIntent().getStringExtra("userID");

        Toolbar welcomeToolbar = (Toolbar) findViewById(R.id.converterToolbar);
        setSupportActionBar(welcomeToolbar);
        getSupportActionBar().setTitle("PROFILE UPDATE");


        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(UpdateProfileActivity.this, "Something went wrong! User's details are not available at the moment", Toast.LENGTH_LONG).show();
        } else {
            showProfile(firebaseUser);
        }

        swipeToRefresh();
        findViews();
        setUpDatePicker();


        /*/Upload Profile Pic
        TextView textViewUploadProfilePic = findViewById(R.id.textView_profile_upload_pic);
        textViewUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UploadProfilePicActivity.class);
                startActivity(intent);
                finish();
            }
        }); */


        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void findViews() {
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);
        radioGroupUpdateGender = findViewById(R.id.radio_group_update_profile_gender);
        editText_update_profile_email = findViewById(R.id.editText_update_profile_email);
    }

    private void setUpDatePicker() {
        //Setting up DatePicker
        ImageView imageViewDatePicker = findViewById(R.id.imageView_date_picker);
        imageViewDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Extracting saved dd, mm and yyyy into different variables by creating an array delimited by "/"
                String[] textSADoB = textBirth.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) - 1;     //to take care of month index starting from 0
                int year = Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;
                //setting up DatePicker according to the extracted date
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
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
                startActivity(getIntent());
                finish();
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

    private void showProfile(@NonNull FirebaseUser firebaseUser) {

        String userID = firebaseUser.getUid();                               //extracting unique UID

        // Extracting User reference from Database for "Registered_User"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered_User").child(userID);
        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ReadwriteUserDetails ReadUserDetails = snapshot.getValue(ReadwriteUserDetails.class);
                    if (ReadUserDetails != null) {
                        textFullName = firebaseUser.getDisplayName();
                        textBirth = ReadUserDetails.birth;
                        textGender = ReadUserDetails.gender;
                        textPhone = ReadUserDetails.phone;
                        textEmail = ReadUserDetails.email;

                        editTextUpdateName.setText(textFullName);
                        editTextUpdateDoB.setText((textBirth));

                        //Show Gender through radio button
                        if (textGender.equals("Male")) {
                            radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                        } else {
                            radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);
                        }
                        radioButtonUpdateGenderSelected.setChecked(true);

                        editTextUpdateMobile.setText(textPhone);
                        editText_update_profile_email.setText(textEmail);

                    } else {
                        Toast.makeText(UpdateProfileActivity.this, "mo read write!", Toast.LENGTH_LONG).show();
                    }
                }else {
                    // Showing toast message if user details not found in the database
                    Toast.makeText(UpdateProfileActivity.this, "User details not found", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void updateProfile(FirebaseUser firebaseUser) {
        // Get the email from the editText_update_profile_email field
        textEmail = editText_update_profile_email.getText().toString();

        //to update user profile
        int selectedGenderId = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderId);

        //Validate Mobile Number using Regular Expression
        String mobileRegex = "[0][0-9]{9}";
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        Matcher mobileMatcher = mobilePattern.matcher(textPhone);

        if (TextUtils.isEmpty(textFullName)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            editTextUpdateName.setError("Full Name is required!");
            editTextUpdateName.requestFocus();
        } else if (TextUtils.isEmpty(textBirth)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
            editTextUpdateDoB.setError("Date of Birth is required!");
            editTextUpdateDoB.requestFocus();
        } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfileActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
            radioButtonUpdateGenderSelected.setError("Gender is required!");
            radioButtonUpdateGenderSelected.requestFocus();
        } else if (TextUtils.isEmpty(textPhone)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter mobile no.", Toast.LENGTH_SHORT).show();
            editTextUpdateMobile.setError("Mobile No. is required!");
            editTextUpdateMobile.requestFocus();
        } else if (textPhone.length() > 12) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter mobile no.", Toast.LENGTH_SHORT).show();
            editTextUpdateMobile.setError("Mobile No. should be lees than 12 digits!");
            editTextUpdateMobile.requestFocus();
        } else if (!mobileMatcher.find()) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter mobile no.", Toast.LENGTH_SHORT).show();
            editTextUpdateMobile.setError("Mobile No. is not valid!");
            editTextUpdateMobile.requestFocus();
        } else {
            textGender = radioButtonUpdateGenderSelected.getText().toString();
            textFullName = editTextUpdateName.getText().toString();
            textBirth = editTextUpdateDoB.getText().toString();
            textPhone = editTextUpdateMobile.getText().toString();
            textEmail = editText_update_profile_email.getText().toString();

            //Enter User Data into the Firebase Realtime Database. Set up dependencies
            ReadwriteUserDetails writeUserDetails = new ReadwriteUserDetails(textFullName, textEmail, textBirth, textGender, textPhone);

            //Extracting User reference from Database for "Registered_User"
            String userID = firebaseUser.getUid();
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered_User").child(userID);

            referenceProfile.setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Setting Display Name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdates);
                        Toast.makeText(UpdateProfileActivity.this, "Updation Successful!", Toast.LENGTH_LONG).show();
                        // Open UserProfileActivity after Updation is done
                        Intent intent = new Intent(UpdateProfileActivity.this, ProfileActivity.class);
                        // To stop User from going back to UpdateProfileActivity after Updation is done
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UpdateProfileActivity.this, "Updation Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.update_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);

            //clear stack to prevent using back button
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}