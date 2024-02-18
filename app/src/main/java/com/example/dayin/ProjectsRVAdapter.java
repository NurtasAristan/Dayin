package com.example.dayin;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProjectsRVAdapter extends RecyclerView.Adapter<ProjectsRVAdapter.ViewHolder> {
    interface OnProjectClickListener{
        void onProjectClick(Project project, int position);
    }

    private OnProjectClickListener onClickListener;
    private ArrayList<Project> projects;

    ProjectsRVAdapter(ArrayList<Project> projects) {
        this.projects = projects;
    }

    ProjectsRVAdapter(ArrayList<Project> projects, OnProjectClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.projects = projects;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, goalView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.project_name);
            goalView = itemView.findViewById(R.id.project_goal);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.project_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Project project = projects.get(position);
        viewHolder.nameView.setText(project.getName());
        viewHolder.goalView.setText(project.getGoal());

        viewHolder.itemView.setOnClickListener(v -> onClickListener.onProjectClick(project, position));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public void setProjects(ArrayList<Project> newProjects) {
        this.projects = newProjects;
    }

    public ArrayList<Project> getProjects(DatabaseHelper databaseHelper) {
        ArrayList<Project> projects = new ArrayList<>();
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        }
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_PROJECTS;
        selectQuery += " WHERE " + DatabaseHelper.COLUMN_PROJECTS_STATUS_ID + " = 1";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                int category_id = Integer.parseInt(cursor.getString(2));
                int status_id = Integer.parseInt(cursor.getString(3));
                String goal = cursor.getString(4);
                String start = cursor.getString(5);
                String end = cursor.getString(6);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    projects.add(new Project(id,name,category_id,status_id,goal,
                            LocalDateTime.parse(start, formatter),
                            LocalDateTime.parse(end, formatter)));
                }
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return projects;
    }

}
