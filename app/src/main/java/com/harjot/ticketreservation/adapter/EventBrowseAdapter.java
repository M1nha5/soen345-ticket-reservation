package com.harjot.ticketreservation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harjot.ticketreservation.R;
import com.harjot.ticketreservation.model.EventItem;
import com.harjot.ticketreservation.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class EventBrowseAdapter extends RecyclerView.Adapter<EventBrowseAdapter.ViewHolder> {
    public interface OnReserveClick {
        void onReserve(EventItem item);
    }

    private final List<EventItem> items = new ArrayList<>();
    private final OnReserveClick listener;

    public EventBrowseAdapter(OnReserveClick listener) {
        this.listener = listener;
    }

    public void setItems(List<EventItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_browse, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getCategory() + " | " + item.getLocation());
        holder.datetime.setText(DateUtils.dateTime(item.getDateTimeMillis()));
        holder.availability.setText("Available: " + item.getAvailableTickets());
        holder.reserveButton.setOnClickListener(v -> listener.onReserve(item));
        holder.reserveButton.setEnabled(item.getAvailableTickets() > 0);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        TextView datetime;
        TextView availability;
        Button reserveButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvEventTitle);
            subtitle = itemView.findViewById(R.id.tvEventSubtitle);
            datetime = itemView.findViewById(R.id.tvEventDateTime);
            availability = itemView.findViewById(R.id.tvEventAvailability);
            reserveButton = itemView.findViewById(R.id.btnReserve);
        }
    }
}
