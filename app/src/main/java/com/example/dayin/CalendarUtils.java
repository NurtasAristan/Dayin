package com.example.dayin;

import android.os.Build;

import androidx.annotation.NonNull;
import com.example.dayin.CalendarDay;
import java.time.LocalDate;
import java.time.ZoneId;

public class CalendarUtils {
    public static LocalDate calendarDayToLocalDate(CalendarDay calendarDay) {
        int year = calendarDay.getYear();
        int month = calendarDay.getMonth();
        int day = calendarDay.getDay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDate.of(year, month, day);
        }
        return null;
    }
}