package com.learnoset.chatapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User_profile extends AppCompatActivity {

    private TextView profileNameTextView;
    private TextView profileEmailTextView;
    private TextView profileRoleTextView;
    private TextView profilePhoneTextView;
    private ImageButton logoutButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ValueEventListener userListener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profileNameTextView = findViewById(R.id.profile_view_name);
        profileRoleTextView=findViewById(R.id.profile_view_role);
        profileEmailTextView=findViewById(R.id.profile_view_email);
        profilePhoneTextView=findViewById(R.id.profile_view_phone);
        logoutButton = findViewById(R.id.button_logout);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.database_url)).child("users");

        //databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.database_url)).child("users");

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return;
    }


    @Override
    protected void onStart() {
        super.onStart();

        String phoneNumber = getPhoneNumber();
        if (phoneNumber != null) {
            getUserData(phoneNumber);
        }
    }

    // Method to retrieve the phone number from shared preferences
    private String getPhoneNumber() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("phone_number", null);
    }




    @Override
    protected void onStop() {
        super.onStop();

        // Remove the ValueEventListener when the activity is stopped to prevent memory leaks
        if (userListener != null) {
            databaseReference.removeEventListener(userListener);
        }
    }

    private void getUserData(String mobile) {
        userListener = databaseReference.child(mobile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("name").getValue(String.class);
                    if (userName != null) {
                        profileNameTextView.setText(userName);
                    }
                } else {
                    Toast.makeText(User_profile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }

                if (snapshot.exists()) {
                    String userRole = snapshot.child("role").getValue(String.class);
                    if (userRole != null) {
                        profileRoleTextView.setText(userRole);
                    }
                } else {
                    Toast.makeText(User_profile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }

                if (snapshot.exists()) {
                    String userEmail = snapshot.child("email").getValue(String.class);
                    if (userEmail != null) {
                        profileEmailTextView.setText(userEmail);
                    }
                } else {
                    Toast.makeText(User_profile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }

                profilePhoneTextView.setText(mobile);
                return ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_profile.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        firebaseAuth.signOut();
        Intent intent = new Intent(User_profile.this, Login.class);
        startActivity(intent);
        finish();
    }
}
