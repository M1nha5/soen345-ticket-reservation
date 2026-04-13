package com.harjot.ticketreservation;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.harjot.ticketreservation.adapter.ReservationAdapter;
import com.harjot.ticketreservation.model.ReservationItem;
import com.harjot.ticketreservation.service.FirebaseDataSource;
import com.harjot.ticketreservation.service.ListCallback;
import com.harjot.ticketreservation.service.SimpleCallback;

import java.util.List;

public class ReservationListActivity extends AppCompatActivity {
    private FirebaseDataSource dataSource;
    private ReservationAdapter adapter;
    private TextView tvEmptyReservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);
        dataSource = new FirebaseDataSource();
        tvEmptyReservations = findViewById(R.id.tvEmptyReservations);
        RecyclerView recyclerView = findViewById(R.id.rvReservations);
        adapter = new ReservationAdapter(item -> dataSource.cancelReservation(item, new SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ReservationListActivity.this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                loadReservations();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ReservationListActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        }));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
    }

    private void loadReservations() {
        String uid = dataSource.getCurrentUid();
        if (uid == null) {
            finish();
            return;
        }
        dataSource.fetchReservations(uid, new ListCallback<ReservationItem>() {
            @Override
            public void onSuccess(List<ReservationItem> items) {
                adapter.setItems(items);
                tvEmptyReservations.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ReservationListActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
