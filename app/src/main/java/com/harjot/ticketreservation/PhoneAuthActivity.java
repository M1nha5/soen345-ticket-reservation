package com.harjot.ticketreservation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.harjot.ticketreservation.model.UserProfile;
import com.harjot.ticketreservation.service.DataCallback;
import com.harjot.ticketreservation.service.FirebaseDataSource;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {
    private FirebaseDataSource dataSource;
    private EditText etPhone;
    private EditText etCode;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        dataSource = new FirebaseDataSource();
        etPhone = findViewById(R.id.etPhone);
        etCode = findViewById(R.id.etCode);
        Button btnSendCode = findViewById(R.id.btnSendCode);
        Button btnVerify = findViewById(R.id.btnVerifyCode);

        btnSendCode.setOnClickListener(v -> sendCode());
        btnVerify.setOnClickListener(v -> verifyCode());
    }

    private void sendCode() {
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Phone number required", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(dataSource.getAuth())
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode() {
        String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(code) || TextUtils.isEmpty(verificationId)) {
            Toast.makeText(this, "Enter verification code", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneCredential(credential);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            signInWithPhoneCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneAuthActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            verificationId = id;
            Toast.makeText(PhoneAuthActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
        }
    };

    private void signInWithPhoneCredential(PhoneAuthCredential credential) {
        dataSource.signInWithCredential(credential, new DataCallback<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser user) {
                String phone = etPhone.getText().toString().trim();
                dataSource.upsertPhoneUser(user, phone, null, new DataCallback<UserProfile>() {
                    @Override
                    public void onSuccess(UserProfile data) {
                        startActivity(new Intent(PhoneAuthActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(PhoneAuthActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (credential.getSmsCode() != null) {
                    Toast.makeText(PhoneAuthActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PhoneAuthActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
