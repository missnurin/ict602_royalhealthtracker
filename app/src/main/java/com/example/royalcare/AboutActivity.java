package com.example.royalcare;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button btnGithub;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        btnGithub = findViewById(R.id.btnGithub);

        btnGithub.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openLink("https://github.com/missnurin/ict602_royalhealthtracker");
            }
        });
    }
    private void openLink(String url) {
        // Create an Intent to open a web page
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}