package com.harjot.ticketreservation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harjot.ticketreservation.R;
import com.harjot.ticketreservation.model.EventItem;
import com.harjot.ticketreservation.util.DateUtils;
import com.harjot.ticketreservation.util.EventImageResolver;

import java.util.ArrayList;
import java.util.List;

public class ManageEventAdapter extends RecyclerView.Adapter<ManageEventAdapter.ViewHolder> {
    public interface Listener {
        void onEdit(EventItem item);
        void onCancel(EventItem item);
    }

    private final List<EventItem> items = new ArrayList<>();
    private final Listener listener;

    public ManageEventAdapter(Listener listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_manage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventItem item = items.get(position);
        holder.image.setImageResource(EventImageResolver.resolveByCategory(item.getCategory()));
        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getCategory() + " • " + item.getLocation());
        holder.datetime.setText(DateUtils.dateTime(item.getDateTimeMillis()));
        String statusLabel = "active".equals(item.getStatus()) ? "Active" : "Cancelled";
        holder.status.setText(statusLabel + " • Available: " + item.getAvailableTickets());
        holder.editButton.setOnClickListener(v -> listener.onEdit(item));
        holder.cancelButton.setOnClickListener(v -> listener.onCancel(item));
        holder.cancelButton.setEnabled(!"cancelled".equals(item.getStatus()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView subtitle;
        TextView datetime;
        TextView status;
        Button editButton;
        Button cancelButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivManageEventImage);
            title = itemView.findViewById(R.id.tvManageEventTitle);
            subtitle = itemView.findViewById(R.id.tvManageEventSubtitle);
            datetime = itemView.findViewById(R.id.tvManageEventDateTime);
            status = itemView.findViewById(R.id.tvManageEventStatus);
            editButton = itemView.findViewById(R.id.btnEditEvent);
            cancelButton = itemView.findViewById(R.id.btnCancelEvent);
        }
    }
}
