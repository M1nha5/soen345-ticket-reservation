package com.harjot.ticketreservation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
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
    private TextView tvEmptyEvents;

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
        tvEmptyEvents = findViewById(R.id.tvEmptyEvents);
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
        tvEmptyEvents.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showReserveDialog(EventItem item) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reserve_tickets, null);
        NumberPicker numberPicker = dialogView.findViewById(R.id.npTickets);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(Math.max(1, item.getAvailableTickets()));
        numberPicker.setWrapSelectorWheel(false);
        new AlertDialog.Builder(this)
                .setTitle("Reserve Tickets")
                .setView(dialogView)
                .setPositiveButton("Reserve", (dialog, which) -> {
                    int tickets = numberPicker.getValue();
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
