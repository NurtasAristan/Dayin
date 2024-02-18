package com.example.dayin;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ProjectActivity extends AppCompatActivity {
    Project project;
    ArrayList<Task> tasks;

    EditText editProjectName, editProjectGoal;
    Spinner categorySpinner;
    RecyclerView recyclerviewTasks;
    TasksRVAdapter adapter;

    TextInputLayout taskField, startField, endField;
    TextInputEditText task_name, startAt, endAt;

    DatabaseHelper helper;
    SQLiteDatabase db;

    DateTimeFormatter formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_project);

        editProjectName = findViewById(R.id.editProjectName);
        editProjectGoal = findViewById(R.id.editProjectGoal);
        categorySpinner = findViewById(R.id.categorySpinner);

        startField = findViewById(R.id.startAt);
        startAt = findViewById(R.id.startAtTime);
        endField = findViewById(R.id.endAt);
        endAt = findViewById(R.id.endAtTime);

        recyclerviewTasks = findViewById(R.id.recyclerviewTasks);
        taskField = findViewById(R.id.taskField);
        task_name = findViewById(R.id.task_name);

        helper = new DatabaseHelper(this);
        db = helper.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        }

        // getProject(id) or new Project
        Bundle arguments = getIntent().getExtras();
        if (arguments!=null) {
            getProject(arguments.getInt("id"));
            editProjectName.setText(project.getName());
            editProjectGoal.setText(project.getGoal());

        } else {
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startTime = LocalDateTime.of(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                endTime = startTime.plusHours(1);
            }
            project = new Project(0, "", 1, 1,"",
                    startTime, endTime);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startAt.setText(project.getStartTime().format(formatter));
            endAt.setText(project.getEndTime().format(formatter));
        }

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listCategories());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setSelection(project.getCategory_id()-1);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getItemAtPosition(position);
                project.setCategory_id(category.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        MaterialToolbar materialToolbar = findViewById(R.id.materialToolbar);
        materialToolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
        materialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.delete) {
                db.delete(DatabaseHelper.TABLE_PROJECTS, "id = ?", new String[]{String.valueOf(project.getId())});
                db.delete(DatabaseHelper.TABLE_TASKS, "project_id = ?", new String[]{String.valueOf(project.getId())});
                goHome();
                return true;
            }
            return false;
        });

        tasks = listTasks();
        recyclerviewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerviewTasks.setAdapter(new TasksRVAdapter(tasks));

        Button saveProjectButton = findViewById(R.id.saveProjectButton);
        saveProjectButton.setOnClickListener(v -> {
            project.setName(editProjectName.getText().toString());
            project.setGoal(editProjectGoal.getText().toString());

            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_PROJECTS_NAME, project.getName());
            cv.put(DatabaseHelper.COLUMN_PROJECTS_CATEGORY_ID, project.getCategory_id());
            cv.put(DatabaseHelper.COLUMN_PROJECTS_GOAL, project.getGoal());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cv.put(DatabaseHelper.COLUMN_PROJECTS_STARTTIME, project.getStartTime().format(formatter));
                cv.put(DatabaseHelper.COLUMN_PROJECTS_ENDTIME, project.getEndTime().format(formatter));
            }

            if (project.getId()>0) {
                db.update(DatabaseHelper.TABLE_PROJECTS, cv,
                        DatabaseHelper.COLUMN_PROJECTS_ID + " = ?",
                        new String[]{String.valueOf(project.getId())});
            } else {
                project.setId((int) db.insert(DatabaseHelper.TABLE_PROJECTS, null, cv));
            }
            saveTasks();
            goHome();
        });

        taskField.setEndIconOnClickListener(v -> {
            tasks.add(new Task(0, project.getId(), task_name.getText().toString()));
            recyclerviewTasks.setAdapter(new TasksRVAdapter(tasks));
        });

        // start
        startAt.setOnClickListener(v -> {
            MaterialDatePicker.Builder datePickerBuilder = MaterialDatePicker
                    .Builder.datePicker().setTitleText("Күн таңдаңыз");
            MaterialDatePicker materialDatePicker = datePickerBuilder.build();
            materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis((Long) selection);

                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setTitleText("Select Appointment time")
                        .build();
                materialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
                materialTimePicker.addOnPositiveButtonClickListener(v1 -> {
                    calendar.set(Calendar.HOUR, materialTimePicker.getHour());
                    calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        project.setStartTime(LocalDateTime.of(calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
                        startAt.setText(project.getStartTime().format(formatter));
                    }
                });
            });
        });
        /*
        startField.setEndIconOnClickListener(v -> {
            project.setTime("");
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_PROJECTS_STARTTIME, project.getTime());
            db.update(DatabaseHelper.TABLE_PROJECTS, cv,
                    DatabaseHelper.COLUMN_PROJECTS_ID + " = ?",
                    new String[]{String.valueOf(project.getId())});
            startAt.setText("");
        });*/

        // end
        endAt.setOnClickListener(v -> {
            MaterialDatePicker.Builder datePickerBuilder = MaterialDatePicker
                    .Builder.datePicker().setTitleText("Күн таңдаңыз");
            MaterialDatePicker materialDatePicker = datePickerBuilder.build();
            materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis((Long) selection);

                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setTitleText("Select Appointment time")
                        .build();
                materialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
                materialTimePicker.addOnPositiveButtonClickListener(v1 -> {
                    calendar.set(Calendar.HOUR, materialTimePicker.getHour());
                    calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        project.setEndTime(LocalDateTime.of(calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
                        endAt.setText(project.getEndTime().format(formatter));
                    }
                });
            });
        });
        /*
        startField.setEndIconOnClickListener(v -> {
            project.setTime("");
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_PROJECTS_STARTTIME, project.getTime());
            db.update(DatabaseHelper.TABLE_PROJECTS, cv,
                    DatabaseHelper.COLUMN_PROJECTS_ID + " = ?",
                    new String[]{String.valueOf(project.getId())});
            startAt.setText("");
        });*/

        // finish
        Button projectDone = findViewById(R.id.projectDone);
        projectDone.setOnClickListener(v -> {
            project.setStatus_id(2);
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_PROJECTS_STATUS_ID, project.getStatus_id());
            db.update(DatabaseHelper.TABLE_PROJECTS, cv,
                    DatabaseHelper.COLUMN_PROJECTS_ID + " = ?",
                    new String[]{String.valueOf(project.getId())});
            goHome();
        });

    }


    public void getProject(int project_id) {
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_PROJECTS + " WHERE " +
                DatabaseHelper.COLUMN_PROJECTS_ID + " = " + project_id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        project = new Project();
        project.setId(Integer.parseInt(cursor.getString(0)));
        project.setName(cursor.getString(1));
        project.setCategory_id(Integer.parseInt(cursor.getString(2)));
        project.setStatus_id(Integer.parseInt(cursor.getString(3)));
        project.setGoal(cursor.getString(4));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            project.setStartTime(LocalDateTime.parse(cursor.getString(5), formatter));
            project.setEndTime(LocalDateTime.parse(cursor.getString(6), formatter));
        };
        cursor.close();
    }

    public void saveTasks() {
        for (int i=0; i<tasks.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_TASKS_PROJECT_ID, project.getId());
            cv.put(DatabaseHelper.COLUMN_TASKS_GOAL, tasks.get(i).getGoal());
            if (tasks.get(i).getId()>0) {
                db.update(DatabaseHelper.TABLE_TASKS, cv,
                        DatabaseHelper.COLUMN_TASKS_ID + " = ?",
                        new String[]{String.valueOf(tasks.get(i).getId())});
            } else {
                db.insert(DatabaseHelper.TABLE_TASKS, null, cv);
            }
        }
    }

    public ArrayList<Category> listCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                categories.add(new Category(id,name));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public ArrayList<Task> listTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS + " WHERE " +
                DatabaseHelper.COLUMN_TASKS_PROJECT_ID + " = " + project.getId();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                int project_id = Integer.parseInt(cursor.getString(1));
                String goal = cursor.getString(2);
                tasks.add(new Task(id,project_id,goal));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

}
