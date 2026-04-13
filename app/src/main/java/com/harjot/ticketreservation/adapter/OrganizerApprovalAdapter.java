package com.harjot.ticketreservation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harjot.ticketreservation.R;
import com.harjot.ticketreservation.model.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class OrganizerApprovalAdapter extends RecyclerView.Adapter<OrganizerApprovalAdapter.ViewHolder> {
    public interface Listener {
        void onApprove(UserProfile item);
        void onReject(UserProfile item);
    }

    private final List<UserProfile> items = new ArrayList<>();
    private final Listener listener;

    public OrganizerApprovalAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<UserProfile> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organizer_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserProfile item = items.get(position);
        holder.name.setText(item.getName());
        holder.info.setText(item.getEmail() + " | " + item.getPhone());
        holder.approveButton.setOnClickListener(v -> listener.onApprove(item));
        holder.rejectButton.setOnClickListener(v -> listener.onReject(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView info;
        Button approveButton;
        Button rejectButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvOrganizerName);
            info = itemView.findViewById(R.id.tvOrganizerInfo);
            approveButton = itemView.findViewById(R.id.btnApproveOrganizer);
            rejectButton = itemView.findViewById(R.id.btnRejectOrganizer);
        }
    }
}
