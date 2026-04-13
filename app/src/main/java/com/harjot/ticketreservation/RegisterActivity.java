package com.harjot.ticketreservation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.harjot.ticketreservation.service.DataCallback;
import com.harjot.ticketreservation.service.FirebaseDataSource;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseDataSource dataSource;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPhone;
    private Spinner spRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dataSource = new FirebaseDataSource();
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        spRole = findViewById(R.id.spRole);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);

        btnCreateAccount.setOnClickListener(v -> register());
    }

    private void register() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String phone = etPhone.getText().toString().trim();
        String roleValue = String.valueOf(spRole.getSelectedItem());
        boolean organizer = "Organizer".equalsIgnoreCase(roleValue);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        dataSource.registerWithEmail(name, email, password, phone, organizer, new DataCallback<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser data) {
                Toast.makeText(RegisterActivity.this, organizer ? "Organizer account pending admin approval" : "Registration successful", Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
