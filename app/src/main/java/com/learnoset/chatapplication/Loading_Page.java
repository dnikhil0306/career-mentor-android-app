package com.learnoset.chatapplication;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class Loading_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        ProgressBar progress_bar = findViewById(R.id.progress_bar); // Initialize the progress bar

        // Show the progress bar
        progress_bar.setVisibility(ProgressBar.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your main activity or perform any other necessary actions
                startActivity(new Intent(Loading_Page.this, Login.class));
                finish();
            }
        }, 3000); // 3000 milliseconds = 3 seconds

        return ;
    }
}