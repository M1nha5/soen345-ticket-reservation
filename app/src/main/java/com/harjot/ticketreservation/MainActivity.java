package com.harjot.ticketreservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.harjot.ticketreservation.model.UserProfile;
import com.harjot.ticketreservation.service.DataCallback;
import com.harjot.ticketreservation.service.FirebaseDataSource;
import com.harjot.ticketreservation.service.RolePolicyService;

public class MainActivity extends AppCompatActivity {
    private static final String ADMIN_EMAIL = "harjot.wic@gmail.com";
    private static final String ADMIN_PHONE = "+14385217222";

    private FirebaseDataSource dataSource;
    private RolePolicyService rolePolicyService;
    private TextView tvWelcome;
    private Button btnBrowseEvents;
    private Button btnReservations;
    private Button btnManageEvents;
    private Button btnApprovals;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataSource = new FirebaseDataSource();
        rolePolicyService = new RolePolicyService();
        tvWelcome = findViewById(R.id.tvWelcome);
        btnBrowseEvents = findViewById(R.id.btnBrowseEvents);
        btnReservations = findViewById(R.id.btnReservations);
        btnManageEvents = findViewById(R.id.btnManageEvents);
        btnApprovals = findViewById(R.id.btnApprovals);
        btnLogout = findViewById(R.id.btnLogout);

        btnBrowseEvents.setOnClickListener(v -> startActivity(new Intent(this, EventListActivity.class)));
        btnReservations.setOnClickListener(v -> startActivity(new Intent(this, ReservationListActivity.class)));
        btnManageEvents.setOnClickListener(v -> startActivity(new Intent(this, ManageEventsActivity.class)));
        btnApprovals.setOnClickListener(v -> startActivity(new Intent(this, OrganizerApprovalActivity.class)));
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        dataSource.bootstrapAdmin(ADMIN_EMAIL, ADMIN_PHONE, new com.harjot.ticketreservation.service.SimpleCallback() {
            @Override
            public void onSuccess() {
                loadProfile();
            }

            @Override
            public void onError(String error) {
                loadProfile();
            }
        });
    }

    private void loadProfile() {
        dataSource.fetchCurrentProfile(new DataCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                if (profile == null) {
                    Toast.makeText(MainActivity.this, "Profile missing", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return;
                }
                tvWelcome.setText("Welcome, " + profile.getName() + " (" + profile.getRole() + ")");
                boolean canManageEvents = rolePolicyService.canManageEvents(profile);
                boolean canApprove = rolePolicyService.canApproveOrganizers(profile);
                btnManageEvents.setVisibility(canManageEvents ? View.VISIBLE : View.GONE);
                btnApprovals.setVisibility(canApprove ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
