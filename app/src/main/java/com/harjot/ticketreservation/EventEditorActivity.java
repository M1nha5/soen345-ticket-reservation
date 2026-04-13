package com.harjot.ticketreservation;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.harjot.ticketreservation.model.EventItem;
import com.harjot.ticketreservation.service.FirebaseDataSource;
import com.harjot.ticketreservation.service.SimpleCallback;
import com.harjot.ticketreservation.util.AppConstants;
import com.harjot.ticketreservation.util.DateUtils;

import java.text.ParseException;

public class EventEditorActivity extends AppCompatActivity {
    private FirebaseDataSource dataSource;
    private EditText etTitle;
    private EditText etCategory;
    private EditText etLocation;
    private EditText etDate;
    private EditText etTime;
    private EditText etTotalTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);
        dataSource = new FirebaseDataSource();

        etTitle = findViewById(R.id.etTitle);
        etCategory = findViewById(R.id.etCategory);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etTotalTickets = findViewById(R.id.etTotalTickets);
        Button btnSave = findViewById(R.id.btnSaveEvent);

        bindIncomingEvent();
        btnSave.setOnClickListener(v -> saveEvent());
    }

    private void bindIncomingEvent() {
        if (!getIntent().hasExtra("eventId")) {
            return;
        }
        etTitle.setText(getIntent().getStringExtra("title"));
        etCategory.setText(getIntent().getStringExtra("category"));
        etLocation.setText(getIntent().getStringExtra("location"));
        long dateTime = getIntent().getLongExtra("dateTime", 0);
        String[] split = DateUtils.dateTime(dateTime).split(" ");
        if (split.length == 2) {
            etDate.setText(split[0]);
            etTime.setText(split[1]);
        }
        etTotalTickets.setText(String.valueOf(getIntent().getIntExtra("totalTickets", 0)));
    }

    private void saveEvent() {
        String title = etTitle.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String ticketsText = etTotalTickets.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(category) || TextUtils.isEmpty(location)
                || TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(ticketsText)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalTickets;
        try {
            totalTickets = Integer.parseInt(ticketsText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid ticket count", Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalTickets <= 0) {
            Toast.makeText(this, "Ticket count must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }
        long dateTime;
        try {
            dateTime = DateUtils.toEpochMillis(date, time);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date/time", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = getIntent().getStringExtra("eventId");
        int previousTotalTickets = getIntent().getIntExtra("totalTickets", totalTickets);
        int previousAvailableTickets = getIntent().getIntExtra("availableTickets", totalTickets);
        int soldTickets = Math.max(0, previousTotalTickets - previousAvailableTickets);
        int availableTickets = Math.max(0, totalTickets - soldTickets);
        String status = getIntent().getStringExtra("status");
        String organizerId = dataSource.getCurrentUid();
        EventItem event = new EventItem(
                id,
                title,
                category,
                location,
                dateTime,
                totalTickets,
                availableTickets,
                organizerId,
                status == null ? AppConstants.STATUS_ACTIVE : status,
                System.currentTimeMillis()
        );

        dataSource.saveEvent(event, new SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EventEditorActivity.this, "Event saved", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EventEditorActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
