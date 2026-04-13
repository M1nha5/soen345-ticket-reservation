package com.harjot.ticketreservation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.harjot.ticketreservation.adapter.ManageEventAdapter;
import com.harjot.ticketreservation.model.EventItem;
import com.harjot.ticketreservation.model.UserProfile;
import com.harjot.ticketreservation.service.DataCallback;
import com.harjot.ticketreservation.service.FirebaseDataSource;
import com.harjot.ticketreservation.service.ListCallback;
import com.harjot.ticketreservation.service.RolePolicyService;
import com.harjot.ticketreservation.service.SimpleCallback;

import java.util.List;

public class ManageEventsActivity extends AppCompatActivity {
    private FirebaseDataSource dataSource;
    private RolePolicyService rolePolicyService;
    private ManageEventAdapter adapter;
    private UserProfile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);
        dataSource = new FirebaseDataSource();
        rolePolicyService = new RolePolicyService();
        Button btnAddEvent = findViewById(R.id.btnAddEvent);
        RecyclerView recyclerView = findViewById(R.id.rvManageEvents);

        adapter = new ManageEventAdapter(new ManageEventAdapter.Listener() {
            @Override
            public void onEdit(EventItem item) {
                Intent intent = new Intent(ManageEventsActivity.this, EventEditorActivity.class);
                intent.putExtra("eventId", item.getId());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("category", item.getCategory());
                intent.putExtra("location", item.getLocation());
                intent.putExtra("dateTime", item.getDateTimeMillis());
                intent.putExtra("totalTickets", item.getTotalTickets());
                intent.putExtra("availableTickets", item.getAvailableTickets());
                intent.putExtra("status", item.getStatus());
                startActivity(intent);
            }

            @Override
            public void onCancel(EventItem item) {
                dataSource.cancelEvent(item.getId(), new SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ManageEventsActivity.this, "Event cancelled", Toast.LENGTH_SHORT).show();
                        loadEvents();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ManageEventsActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddEvent.setOnClickListener(v -> startActivity(new Intent(this, EventEditorActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.fetchCurrentProfile(new DataCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile data) {
                profile = data;
                if (!rolePolicyService.canManageEvents(profile)) {
                    Toast.makeText(ManageEventsActivity.this, "Access denied", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                loadEvents();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ManageEventsActivity.this, error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadEvents() {
        dataSource.fetchManageableEvents(profile, new ListCallback<EventItem>() {
            @Override
            public void onSuccess(List<EventItem> items) {
                adapter.setItems(items);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ManageEventsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
