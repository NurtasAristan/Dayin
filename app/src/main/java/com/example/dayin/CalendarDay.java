package com.example.dayin;

import java.util.ArrayList;

public class CalendarDay {
    private int day;
    private int month;
    private int year;
    private boolean currentMonth;
    private ArrayList<Project> dayProjects;

    public CalendarDay(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public CalendarDay(int day, int month, int year, boolean currentMonth) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.currentMonth = currentMonth;
    }

    public CalendarDay(int day, int month, int year, boolean currentMonth, ArrayList<Project> projects) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.currentMonth = currentMonth;
        this.dayProjects = projects;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public boolean isCurrentMonth() {
        return currentMonth;
    }
}