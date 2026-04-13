package com.harjot.ticketreservation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.harjot.ticketreservation.service.DataCallback;
import com.harjot.ticketreservation.service.FirebaseDataSource;

public class LoginActivity extends AppCompatActivity {
    private FirebaseDataSource dataSource;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dataSource = new FirebaseDataSource();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnPhone = findViewById(R.id.btnPhoneLogin);

        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        btnPhone.setOnClickListener(v -> startActivity(new Intent(this, PhoneAuthActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataSource.getAuth().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show();
            return;
        }
        dataSource.signInWithEmail(email, password, new DataCallback<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser data) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
