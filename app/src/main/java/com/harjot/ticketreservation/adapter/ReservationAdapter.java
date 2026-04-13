package com.harjot.ticketreservation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harjot.ticketreservation.R;
import com.harjot.ticketreservation.model.ReservationItem;
import com.harjot.ticketreservation.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {
    public interface Listener {
        void onCancel(ReservationItem item);
    }

    private final List<ReservationItem> items = new ArrayList<>();
    private final Listener listener;

    public ReservationAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<ReservationItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReservationItem item = items.get(position);
        holder.title.setText(item.getEventTitle());
        holder.subtitle.setText(item.getEventLocation() + " | " + DateUtils.dateTime(item.getEventDateTimeMillis()));
        holder.status.setText("Status: " + item.getStatus() + " | Tickets: " + item.getTickets());
        holder.cancelButton.setEnabled(!"cancelled".equals(item.getStatus()));
        holder.cancelButton.setOnClickListener(v -> listener.onCancel(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        TextView status;
        Button cancelButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvReservationTitle);
            subtitle = itemView.findViewById(R.id.tvReservationSubtitle);
            status = itemView.findViewById(R.id.tvReservationStatus);
            cancelButton = itemView.findViewById(R.id.btnCancelReservation);
        }
    }
}
