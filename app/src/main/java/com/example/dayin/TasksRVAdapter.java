package com.example.dayin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class TasksRVAdapter extends RecyclerView.Adapter<TasksRVAdapter.ViewHolder> {
    private ArrayList<Task> tasks;

    TasksRVAdapter(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText nameView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.task_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.nameView.setText(task.getGoal());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
