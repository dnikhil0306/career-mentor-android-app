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

public class User_Information extends AppCompatActivity {

    private TextView profileNameTextView1;
    private TextView profileEmailTextView1;
    private TextView profileRoleTextView1;
    private TextView profilePhoneTextView1;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ValueEventListener userListener;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        profileNameTextView1 = findViewById(R.id.profile_view_name1);
        profileRoleTextView1=findViewById(R.id.profile_view_role1);
        profileEmailTextView1=findViewById(R.id.profile_view_email1);
        profilePhoneTextView1=findViewById(R.id.profile_view_phone1);




        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.database_url)).child("users");

        //databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.database_url)).child("users");


        return;
    }


    @Override
    protected void onStart() {
        super.onStart();

        String phoneNumber = getPhoneNumber();
        String mobileNumber = MemoryData.getMobile(this);
        if (phoneNumber != null && !phoneNumber.equals(mobileNumber)) {
            getUserData(phoneNumber);
        }
    }

    // Method to retrieve the phone number from shared preferences
    private String getPhoneNumber() {
        SharedPreferences sharedPreferences1 = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences1.getString("get_mobile", null);
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
                        profileNameTextView1.setText(userName);
                    }
                } else {
                    Toast.makeText(User_Information.this, "User data not found", Toast.LENGTH_SHORT).show();
                }

                if (snapshot.exists()) {
                    String userRole = snapshot.child("role").getValue(String.class);
                    if (userRole != null) {
                        profileRoleTextView1.setText(userRole);
                    }
                } else {
                    Toast.makeText(User_Information.this, "User data not found", Toast.LENGTH_SHORT).show();
                }

                if (snapshot.exists()) {
                    String userEmail = snapshot.child("email").getValue(String.class);
                    if (userEmail != null) {
                        profileEmailTextView1.setText(userEmail);
                    }
                } else {
                    Toast.makeText(User_Information.this, "User data not found", Toast.LENGTH_SHORT).show();
                }

                profilePhoneTextView1.setText(mobile);
                return ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_Information.this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
