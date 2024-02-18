package com.example.dayin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarGridAdapter extends BaseAdapter {
    private Context context;
    private Calendar calendar;
    private List<CalendarDay> calendarDays;
    private LayoutInflater inflater;
    private CalendarDay selectedDay;

    public CalendarGridAdapter(Context context, Calendar calendar, CalendarDay selectedDay) {
        this.context = context;
        this.calendar = calendar;
        this.calendarDays = generateCalendarDays();
        this.inflater = LayoutInflater.from(context);
        this.selectedDay = selectedDay;
    }

    @Override
    public int getCount() {
        return calendarDays.size();
    }

    @Override
    public CalendarDay getItem(int position) {
        return calendarDays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.calendar_grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.dayTextView = convertView.findViewById(R.id.dayTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CalendarDay day = getItem(position);
        viewHolder.dayTextView.setText(String.valueOf(day.getDay()));


        // Check if the day is the selected day and change the background color accordingly
        if (day.equals(selectedDay)) {
            viewHolder.dayTextView.setBackgroundResource(R.drawable.selected_day_bg);
        } else if (day.isCurrentMonth()) {
            viewHolder.dayTextView.setBackgroundResource(R.drawable.normal_day_bg);
        } else {
            viewHolder.dayTextView.setBackgroundResource(R.drawable.other_month_day_bg);
        }

        return convertView;
    }

    public void setSelectedDay(CalendarDay selectedDay) {
        this.selectedDay = selectedDay;
        notifyDataSetChanged();
    }

    private List<CalendarDay> generateCalendarDays() {
        List<CalendarDay> calendarDays = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int previousMonthDays = firstDayOfWeek - 2;
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        // Add days from previous month
        Calendar previousMonthCalendar = (Calendar) calendar.clone();
        previousMonthCalendar.add(Calendar.MONTH, -1);
        int previousMonthMaxDay = previousMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = previousMonthMaxDay - previousMonthDays + 1; i <= previousMonthMaxDay; i++) {
            calendarDays.add(new CalendarDay(i, currentMonth - 1, currentYear, false));
        }

        // Add days from current month
        for (int i = 1; i <= maxDayOfMonth; i++) {
            calendarDays.add(new CalendarDay(i, currentMonth, currentYear, true));
        }

        // Add days from next month
        int remainingDays = 42 - calendarDays.size();
        Calendar nextMonthCalendar = (Calendar) calendar.clone();
        nextMonthCalendar.add(Calendar.MONTH, 1);
        for (int i = 1; i <= remainingDays; i++) {
            calendarDays.add(new CalendarDay(i, currentMonth + 1, currentYear, false));
        }

        return calendarDays;
    }
    /*
    private ArrayList<Project> listCalendarDayProjects(int year, int month, int day) {
        ArrayList<Project> allProjects = listProjects();
        ArrayList<Project> filteredList = new ArrayList<>();

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date(year, month, day));

        for (Project project : allProjects) {
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
    private ArrayList<Project> listProjects() {
        ArrayList<Project> projects = new ArrayList<>();
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        }

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_PROJECTS;
        selectQuery += " WHERE " + DatabaseHelper.COLUMN_PROJECTS_STATUS_ID + " = 1";


        SQLiteDatabase db = new SQLiteDatabase();
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
    */
    private static class ViewHolder {
        TextView dayTextView;
    }
}
