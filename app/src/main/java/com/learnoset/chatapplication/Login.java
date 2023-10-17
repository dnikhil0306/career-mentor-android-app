package com.learnoset.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText mobileEditText = findViewById(R.id.l_mobile);
        final EditText passwordEditText = findViewById(R.id.l_password);
        Button loginButton = findViewById(R.id.l_LoginBtn);
        TextView registerNowTextView = findViewById(R.id.l_registerNowBtn);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.database_url));
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Check if the user is already logged in. If the mobile number is empty, the user is not logged in.
        // If already logged in, open MainActivity; otherwise, the user needs to register first.
        /*if (!getPhoneNumber().isEmpty()) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }*/

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNumber = mobileEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(mobileNumber) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Please enter mobile number and password", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(mobileNumber, password);
                }
            }
        });

        registerNowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });
        return ;
    }

    private void loginUser(String mobileNumber, String password) {
        progressDialog.show();

        databaseReference.child("users").child(mobileNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();

                if (snapshot.exists()) {
                    if (snapshot.hasChild("password")) {
                        String storedPassword = snapshot.child("password").getValue(String.class);

                        if (storedPassword != null && storedPassword.equals(password)) {
                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            savePhoneNumber(mobileNumber);
                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, "User password not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Mobile number does not exist", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone_number", phoneNumber);
        editor.apply();
    }

    private String getPhoneNumber() {
        return sharedPreferences.getString("phone_number", "");
    }
}
