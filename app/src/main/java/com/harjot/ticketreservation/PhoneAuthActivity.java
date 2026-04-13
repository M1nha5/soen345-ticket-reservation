package com.harjot.ticketreservation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.telephony.PhoneNumberUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
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
    private String normalizedPhone;

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
        String rawPhone = etPhone.getText().toString().trim();
        normalizedPhone = normalizeNorthAmericaPhone(rawPhone);
        if (TextUtils.isEmpty(normalizedPhone)) {
            Toast.makeText(this, "Phone number required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!PhoneNumberUtils.isGlobalPhoneNumber(normalizedPhone)) {
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        etPhone.setText(normalizedPhone);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(dataSource.getAuth())
                .setPhoneNumber(normalizedPhone)
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
            String message = e.getMessage() == null ? "Phone verification failed" : e.getMessage();
            String lower = message.toLowerCase();
            if (lower.contains("not authorized") || lower.contains("configuration not found")) {
                message = "Phone auth is not authorized yet. Reinstall the app, then confirm Play Integrity API is enabled in Firebase project settings.";
            }
            Toast.makeText(PhoneAuthActivity.this, message, Toast.LENGTH_LONG).show();
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
                String phone = normalizedPhone;
                if (TextUtils.isEmpty(phone)) {
                    phone = normalizeNorthAmericaPhone(etPhone.getText().toString().trim());
                }
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

    private String normalizeNorthAmericaPhone(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        String trimmed = input.trim();
        if (trimmed.startsWith("+")) {
            return "+" + trimmed.substring(1).replaceAll("[^0-9]", "");
        }
        String digits = trimmed.replaceAll("[^0-9]", "");
        if (digits.length() == 10) {
            return "+1" + digits;
        }
        if (digits.length() == 11 && digits.startsWith("1")) {
            return "+" + digits;
        }
        return "+" + digits;
    }
}
