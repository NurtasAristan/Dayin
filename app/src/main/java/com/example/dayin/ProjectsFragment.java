package com.example.dayin;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProjectsFragment extends Fragment {
    ArrayList<Project> projects;
    private ChipGroup chipGroup;
    private RecyclerView recyclerView;
    private ProjectsRVAdapter adapter;
    private FloatingActionButton addProjectButton;
    private DatabaseHelper databaseHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);
        chipGroup = view.findViewById(R.id.chipGroup);
        recyclerView = view.findViewById(R.id.recyclerviewProjects);
        addProjectButton = view.findViewById(R.id.addProjectButton);

        ProjectsRVAdapter.OnProjectClickListener projectClickListener = (project, position) -> {
            Intent intent = new Intent(getContext(), ProjectActivity.class);
            intent.putExtra("id", project.getId());
            startActivity(intent);
        };

        databaseHelper = new DatabaseHelper(getContext());
        projects = listProjects(chipGroup.getCheckedChipId());
        adapter = new ProjectsRVAdapter(projects, projectClickListener);

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            projects = listProjects(chipGroup.getCheckedChipId());
            adapter.setProjects(projects);
            adapter.notifyDataSetChanged();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        addProjectButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProjectActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private ArrayList<Project> listProjects(int category_chip_id) {
        ArrayList<Project> projects = new ArrayList<>();
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        }

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_PROJECTS;
        selectQuery += " WHERE " + DatabaseHelper.COLUMN_PROJECTS_STATUS_ID + " = 1";
        if (category_chip_id==R.id.chipHome) {
            selectQuery += " AND " + DatabaseHelper.COLUMN_PROJECTS_CATEGORY_ID + " = 2";
        } else if (category_chip_id==R.id.chipEducation) {
            selectQuery += " AND " + DatabaseHelper.COLUMN_PROJECTS_CATEGORY_ID + " = 3";
        } else if (category_chip_id==R.id.chipWork) {
            selectQuery += " AND " + DatabaseHelper.COLUMN_PROJECTS_CATEGORY_ID + " = 4";
        }


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
