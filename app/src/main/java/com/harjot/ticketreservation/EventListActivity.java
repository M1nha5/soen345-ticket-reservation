package com.harjot.ticketreservation;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.harjot.ticketreservation.adapter.EventBrowseAdapter;
import com.harjot.ticketreservation.model.EventItem;
import com.harjot.ticketreservation.service.EventFilterService;
import com.harjot.ticketreservation.service.FirebaseDataSource;
import com.harjot.ticketreservation.service.ListCallback;
import com.harjot.ticketreservation.service.ReservationPolicyService;
import com.harjot.ticketreservation.service.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {
    private FirebaseDataSource dataSource;
    private EventFilterService filterService;
    private ReservationPolicyService reservationPolicyService;
    private EventBrowseAdapter adapter;
    private List<EventItem> allEvents;
    private EditText etSearch;
    private EditText etDate;
    private EditText etLocation;
    private EditText etCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        dataSource = new FirebaseDataSource();
        filterService = new EventFilterService();
        reservationPolicyService = new ReservationPolicyService();
        allEvents = new ArrayList<>();

        etSearch = findViewById(R.id.etSearch);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etCategory = findViewById(R.id.etCategory);
        Button btnApplyFilter = findViewById(R.id.btnApplyFilter);
        RecyclerView recyclerView = findViewById(R.id.rvEvents);

        adapter = new EventBrowseAdapter(this::showReserveDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnApplyFilter.setOnClickListener(v -> applyFilter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        dataSource.fetchEvents(true, new ListCallback<EventItem>() {
            @Override
            public void onSuccess(List<EventItem> items) {
                allEvents = items;
                applyFilter();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EventListActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter() {
        List<EventItem> filtered = filterService.filter(
                allEvents,
                etSearch.getText().toString(),
                etDate.getText().toString(),
                etLocation.getText().toString(),
                etCategory.getText().toString()
        );
        adapter.setItems(filtered);
    }

    private void showReserveDialog(EventItem item) {
        EditText input = new EditText(this);
        input.setHint("Tickets");
        new AlertDialog.Builder(this)
                .setTitle("Reserve Tickets")
                .setView(input)
                .setPositiveButton("Reserve", (dialog, which) -> {
                    String value = input.getText().toString().trim();
                    if (TextUtils.isEmpty(value)) {
                        Toast.makeText(this, "Enter ticket quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int tickets = Integer.parseInt(value);
                    if (!reservationPolicyService.canReserve(item, tickets)) {
                        Toast.makeText(this, "Invalid ticket quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dataSource.reserveEvent(item, tickets, new SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(EventListActivity.this, "Reservation successful", Toast.LENGTH_SHORT).show();
                            loadEvents();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(EventListActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
