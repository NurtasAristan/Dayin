package com.example.dayin;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomCalendarView extends LinearLayout {
    private Calendar calendar = Calendar.getInstance();
    private ArrayList<Project> projects;
    private CalendarDay selectedDay = new CalendarDay(calendar.DAY_OF_MONTH, calendar.MONTH,
            calendar.YEAR);

    private ImageButton previousButton;
    private ImageButton nextButton;
    private TextView currentDateTextView;

    private GridView calendarGridView;
    private CalendarGridAdapter gridAdapter;

    private RecyclerView projectsList;
    private ProjectsRVAdapter listAdapter;
    private DatabaseHelper databaseHelper;

    public CustomCalendarView(Context context) {
        super(context);
        init(context);
    }

    // Summoned
    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_calendar_view, this, true);

        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        currentDateTextView = findViewById(R.id.currentDateTextView);
        calendarGridView = findViewById(R.id.calendarGridView);
        projectsList = findViewById(R.id.projectsOnDate);

        previousButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });
        nextButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        updateCalendar();
    }

    private void updateCalendar() {
        // Подготовка адаптера и подключение к грид
        gridAdapter = new CalendarGridAdapter(getContext(), calendar, selectedDay);
        calendarGridView.setAdapter(gridAdapter);

        databaseHelper = new DatabaseHelper(getContext());
        projects = listProjects(new CalendarDay(Calendar.DAY_OF_MONTH,
                Calendar.MONTH, Calendar.YEAR));
        listAdapter = new ProjectsRVAdapter(projects);
        projectsList.setLayoutManager(new LinearLayoutManager(getContext()));
        projectsList.setAdapter(listAdapter);

        // Показ текущего месяца
        currentDateTextView.setText(getFormattedCurrentDate());

        // Установка клика грида
        calendarGridView.setOnItemClickListener((parent, view, position, id) -> {
            // get calendarday
            CalendarDay selectedDay = gridAdapter.getItem(position);

            // set and notify grid
            gridAdapter.setSelectedDay(selectedDay);
            gridAdapter.notifyDataSetChanged();

            // get projects by selectedday
            projects = listProjects(selectedDay);

            // set and notify rv
            listAdapter.setProjects(projects);
            listAdapter.notifyDataSetChanged();
        });
    }

    // Показ КЗ название месяца
    private String getFormattedCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("kk",
                "KZ"));
        return sdf.format(calendar.getTime());
    }

    // used
    private ArrayList<Project> listProjects(CalendarDay selectedDay) {
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
        Date sd = calendarDayToDate(selectedDay);
        projects = filterProjectsByDate(projects,sd);
        return projects;
    }

    public ArrayList<Project> filterProjectsByDate(ArrayList<Project> projectList,
                                                   Date selectedDate) {
        ArrayList<Project> filteredList = new ArrayList<>();
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(selectedDate);

        for (Project project : projectList) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Date c1date = Date.from(project.getStartTime().atZone(ZoneId.systemDefault()).toInstant());
                calendar1.setTime(c1date);
            }
            if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                    calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                    calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)) {
                filteredList.add(project);
            }
        }

        return filteredList;
    }

    public Date calendarDayToDate(CalendarDay calendarDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDay());
        return calendar.getTime();
    }
}
