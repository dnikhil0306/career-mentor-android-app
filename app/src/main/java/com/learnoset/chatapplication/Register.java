package com.learnoset.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private String selectedRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.database_url));
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        final EditText nameEditText = findViewById(R.id.r_name);
        final EditText mobileEditText = findViewById(R.id.r_mobile);
        final EditText emailEditText = findViewById(R.id.r_email);
        final EditText passwordEditText = findViewById(R.id.r_password);
        Button registerButton = findViewById(R.id.r_registerBtn);
        TextView loginNowTextView = findViewById(R.id.r_loginNowBtn);

        Spinner roleSpinner = findViewById(R.id.role);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
        roleSpinner.setOnItemSelectedListener(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String mobile = mobileEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();

                databaseReference.child("users").child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();

                        if (snapshot.exists()) {
                            Toast.makeText(Register.this, "Mobile number already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            saveUserToDatabase(name, mobile, email, password);
                        }
                        return ;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Register.this, "Database error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        loginNowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });
        return ;
    }

    private void saveUserToDatabase(String name, String mobile, String email, String password) {
        DatabaseReference userReference = databaseReference.child("users").child(mobile);
        userReference.child("name").setValue(name);
        userReference.child("email").setValue(email);
        userReference.child("password").setValue(password);
        userReference.child("role").setValue(selectedRole); // Save the selected role in the database

        Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_SHORT).show();

        MemoryData.saveMobile(mobile, Register.this);
        startActivity(new Intent(Register.this, Login.class));
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedRole = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
}
