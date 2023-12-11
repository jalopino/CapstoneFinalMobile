package com.example.ticketingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ViolationAdapter extends RecyclerView.Adapter<ViolationAdapter.ViolationViewHolder> {

    private List<Violation> violationList;

    public ViolationAdapter(List<Violation> violationList) {
        this.violationList = violationList;
    }

    @NonNull
    @Override
    public ViolationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_violation, parent, false);
        return new ViolationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViolationViewHolder holder, int position) {
        Violation violation = violationList.get(position);
        holder.textViewViolationDetail.setText(
                "ID: " + violation.getViolationId() +
                        "\nUser: " + violation.getUserId() +
                        "\nName: " + violation.getName() +
                        "\nViolation: " + violation.getViolation() +
                        "\nDate: " + violation.getDate()
        );
        // Bind other fields similarly
    }

    @Override
    public int getItemCount() {
        return violationList.size();
    }

    static class ViolationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewViolationDetail;

        ViolationViewHolder(View view) {
            super(view);
            textViewViolationDetail = view.findViewById(R.id.textViewViolationDetail);
            // Initialize other views
        }
    }
}