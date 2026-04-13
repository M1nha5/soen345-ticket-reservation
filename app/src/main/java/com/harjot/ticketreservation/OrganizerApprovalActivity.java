package com.harjot.ticketreservation;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.harjot.ticketreservation.adapter.OrganizerApprovalAdapter;
import com.harjot.ticketreservation.model.UserProfile;
import com.harjot.ticketreservation.service.DataCallback;
import com.harjot.ticketreservation.service.FirebaseDataSource;
import com.harjot.ticketreservation.service.ListCallback;
import com.harjot.ticketreservation.service.RolePolicyService;
import com.harjot.ticketreservation.service.SimpleCallback;
import com.harjot.ticketreservation.util.AppConstants;

import java.util.List;

public class OrganizerApprovalActivity extends AppCompatActivity {
    private FirebaseDataSource dataSource;
    private RolePolicyService rolePolicyService;
    private OrganizerApprovalAdapter adapter;
    private TextView tvEmptyApprovals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_approval);
        dataSource = new FirebaseDataSource();
        rolePolicyService = new RolePolicyService();
        tvEmptyApprovals = findViewById(R.id.tvEmptyApprovals);

        RecyclerView recyclerView = findViewById(R.id.rvOrganizerApprovals);
        adapter = new OrganizerApprovalAdapter(new OrganizerApprovalAdapter.Listener() {
            @Override
            public void onApprove(UserProfile item) {
                updateStatus(item, AppConstants.STATUS_APPROVED);
            }

            @Override
            public void onReject(UserProfile item) {
                updateStatus(item, AppConstants.STATUS_REJECTED);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.fetchCurrentProfile(new DataCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile data) {
                if (!rolePolicyService.canApproveOrganizers(data)) {
                    Toast.makeText(OrganizerApprovalActivity.this, "Access denied", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                loadPending();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OrganizerApprovalActivity.this, error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadPending() {
        dataSource.fetchPendingOrganizers(new ListCallback<UserProfile>() {
            @Override
            public void onSuccess(List<UserProfile> items) {
                adapter.setItems(items);
                tvEmptyApprovals.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OrganizerApprovalActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(UserProfile profile, String status) {
        dataSource.updateOrganizerStatus(profile.getUid(), status, new SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(OrganizerApprovalActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                loadPending();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OrganizerApprovalActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
