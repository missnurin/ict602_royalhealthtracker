package com.example.royalcare;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterBirth,
            editTextRegisterPhone, editTextRegisterPassword, editTextRegisterConfirmPassword;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker ;
    private static final String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar welcomeToolbar = (Toolbar) findViewById(R.id.converterToolbar);
        setSupportActionBar(welcomeToolbar);
        getSupportActionBar().setTitle("REGISTRATION");

        Toast.makeText(RegisterActivity.this, "You can register now",Toast.LENGTH_LONG).show();

        //find view
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterBirth = findViewById(R.id.editText_register_birth);
        editTextRegisterPhone = findViewById(R.id.editText_register_phone);
        editTextRegisterPassword = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPassword = findViewById(R.id.editText_register_confirm_password);

        //RADIO BUTTON
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //setting up date picker on edit text
        editTextRegisterBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();  //ca = instance , Ca abstract
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //date picker dialog
                picker =  new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year );
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                //OBTAIN THE ENTERED DATA
                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textBirth = editTextRegisterBirth.getText().toString();
                String textPhone = editTextRegisterPhone.getText().toString();
                String textPassword = editTextRegisterPassword.getText().toString();
                String textConfirmPassword = editTextRegisterConfirmPassword.getText().toString();
                String textGender; //cant obtain value bfor verify if any button was selected or not

                //validate mobile number using matcher and pattern
                String phoneRegex = "[0][0-9]{9}";
                Matcher phoneMatcher;
                Pattern phonePattern = Pattern.compile(phoneRegex);
                phoneMatcher = phonePattern.matcher(textPhone);



                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full Name is required");
                    editTextRegisterFullName.requestFocus();

                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();

                } else if (TextUtils.isEmpty(textBirth)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    editTextRegisterBirth.setError("Date of Birth is required");
                    editTextRegisterBirth.requestFocus();

                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();

                } else if (TextUtils.isEmpty(textPhone)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your phone", Toast.LENGTH_LONG).show();
                    editTextRegisterPhone.setError("Phone number is required");
                    editTextRegisterPhone.requestFocus();
                } else if (textPhone.length() >11){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your phone number", Toast.LENGTH_LONG).show();
                    editTextRegisterPhone.setError("Phone number should less than 12 digits");
                    editTextRegisterPhone.requestFocus();
                } else if (!phoneMatcher.find()){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your phone number", Toast.LENGTH_LONG).show();
                    editTextRegisterPhone.setError("Phone number is invalid");
                    editTextRegisterPhone.requestFocus();

                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editTextRegisterPassword.setError("Password is required");
                    editTextRegisterPassword.requestFocus();
                }else if (textPassword.length() <6){
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegisterPassword.setError("Password too weak");
                    editTextRegisterPassword.requestFocus();

                }else if (TextUtils.isEmpty(textConfirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPassword.setError("Password Confirmation is required");
                    editTextRegisterConfirmPassword.requestFocus();

                }else if (!textPassword.equals(textConfirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please enter same password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPassword.setError("The password is different");
                    editTextRegisterConfirmPassword.requestFocus();
                    //clear the entered password
                    editTextRegisterPassword.clearComposingText();
                    editTextRegisterConfirmPassword.clearComposingText();
                } else{
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                   /* progressBar.setVisibility(View.VISIBLE);*/
                    registerUser(textFullName, textEmail, textBirth, textGender, textPhone, textPassword);

                }
            }
        });
    }

    //Register User using credentials given
    private void registerUser(String textFullName, String textEmail, String textBirth, String textGender, String textPhone, String textPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //create login
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            //Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_LONG).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            //update display name of user
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            //Enter user data into the firebase realtime database
                            ReadwriteUserDetails writeUserDetails = new ReadwriteUserDetails ( textFullName, textEmail, textBirth, textGender, textPhone);

                            //extract user reference from database for "Registered user"
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered_User");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        //send verification email
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(RegisterActivity.this, "User registered successfully. Please verify your email",
                                                Toast.LENGTH_LONG).show();

                                        //go to login after successful register
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish(); //to close register activity

                                    }else{
                                        Toast.makeText(RegisterActivity.this, "User registered failed. Please try again",
                                                Toast.LENGTH_LONG).show();
                                    }progressBar.setVisibility(View.GONE);

                                }
                            });


                        }else{
                            try{
                                throw task.getException();
                            }catch(FirebaseAuthWeakPasswordException e){
                                editTextRegisterPassword.setError("Your password is too weak. Kindly use a mix of alphabets, number and special character");
                                editTextRegisterPassword.requestFocus();
                            }catch(FirebaseAuthInvalidCredentialsException e){
                                editTextRegisterPassword.setError("Your email is invalid or already use. Kindly re-enter.");
                                editTextRegisterPassword.requestFocus();
                            }catch(FirebaseAuthUserCollisionException e){
                                editTextRegisterPassword.setError("User is already registered with this email. Use another email. ");
                                editTextRegisterPassword.requestFocus();
                            }catch(Exception e){
                                Log.e (TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(GONE);
                            }

                        }
                    }
                });
    }
}