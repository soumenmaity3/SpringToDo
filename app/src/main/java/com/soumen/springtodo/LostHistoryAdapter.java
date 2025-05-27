package com.soumen.springtodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LostHistoryAdapter extends RecyclerView.Adapter<LostHistoryAdapter.viewHolder> {
    List<CompleteToDoItem> completeToDoItemList;
    String email;

    public LostHistoryAdapter(List<CompleteToDoItem> completeToDoItemList) {
        this.completeToDoItemList = completeToDoItemList;
    }

    @NonNull
    @Override
    public LostHistoryAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lost_task_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostHistoryAdapter.viewHolder holder, int position) {
        CompleteToDoItem list=completeToDoItemList.get(position);
        holder.txtTitle.setText(list.getTitle());
        holder.txtDescription.setText(list.getDescription());
    }

    @Override
    public int getItemCount() {
        return completeToDoItemList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle,txtDescription;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle=itemView.findViewById(R.id.textTitle);
            txtDescription=itemView.findViewById(R.id.textDescription);
        }
    }
}
