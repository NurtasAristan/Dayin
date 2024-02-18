package com.example.dayin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dayin_db.db";
    private static final int SCHEMA = 1;

    static final String TABLE_PROJECTS = "projects";
    static final String COLUMN_PROJECTS_ID = "id";
    static final String COLUMN_PROJECTS_NAME = "name";
    static final String COLUMN_PROJECTS_CATEGORY_ID = "category_id";
    static final String COLUMN_PROJECTS_STATUS_ID = "status_id";
    static final String COLUMN_PROJECTS_GOAL = "goal";
    static final String COLUMN_PROJECTS_STARTTIME = "start";
    static final String COLUMN_PROJECTS_ENDTIME = "end";

    static final String TABLE_TASKS = "tasks";
    static final String COLUMN_TASKS_ID = "id";
    static final String COLUMN_TASKS_PROJECT_ID = "project_id";
    static final String COLUMN_TASKS_GOAL = "goal";

    static final String TABLE_METHODS = "methods";
    static final String COLUMN_METHODS_ID = "id";
    static final String COLUMN_METHODS_NAME = "name";
    static final String COLUMN_METHODS_CATEGORY_ID = "category_id";
    static final String COLUMN_METHODS_DESCRIPTION = "description";
    static final String COLUMN_METHODS_GPT = "gpt";

    static final String TABLE_CATEGORIES = "categories";
    static final String COLUMN_CATEGORIES_ID = "id";
    static final String COLUMN_CATEGORIES_NAME = "category";

    static final String TABLE_PRIORITIES = "priorities";
    static final String COLUMN_PRIORITIES_ID = "id";
    static final String COLUMN_PRIORITIES_NAME = "priority";

    static final String TABLE_STATUSES = "statuses";
    static final String COLUMN_STATUSES_ID = "id";
    static final String COLUMN_STATUSES_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // projects
        db.execSQL("CREATE TABLE " + TABLE_PROJECTS + "(" +
                COLUMN_PROJECTS_ID + " INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PROJECTS_NAME + " TEXT NOT NULL, " +
                COLUMN_PROJECTS_CATEGORY_ID + " INTEGER DEFAULT 1, " +
                COLUMN_PROJECTS_STATUS_ID + " INTEGER DEFAULT 1, " +
                COLUMN_PROJECTS_GOAL + " TEXT NOT NULL, " +
                COLUMN_PROJECTS_STARTTIME + " DATETIME, " +
                COLUMN_PROJECTS_ENDTIME + " DATETIME)");

        // tasks
        db.execSQL("CREATE TABLE " + TABLE_TASKS + "(" + COLUMN_TASKS_ID +
                " INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT, " + COLUMN_TASKS_PROJECT_ID +
                " INTEGER, " + COLUMN_TASKS_GOAL + " TEXT NOT NULL)");

        // methods
        db.execSQL("CREATE TABLE " + TABLE_METHODS + "(" + COLUMN_METHODS_ID +
                " INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT, " + COLUMN_METHODS_NAME +
                " TEXT NOT NULL, " + COLUMN_METHODS_CATEGORY_ID +
                " INTEGER DEFAULT 1, " + COLUMN_METHODS_DESCRIPTION +
                " TEXT NOT NULL, " + COLUMN_METHODS_GPT +
                " TEXT)");

        // categories
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORIES_ID +
                " INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT, " + COLUMN_CATEGORIES_NAME +
                " TEXT NOT NULL)");
        db.execSQL("INSERT INTO "+ TABLE_CATEGORIES +" (" + COLUMN_CATEGORIES_NAME
                + ") VALUES ('Категориясыз'), ('Тұрмыс'), ('Оқу'), ('Жұмыс');");

        // priorities
        db.execSQL("CREATE TABLE " + TABLE_PRIORITIES + "(" + COLUMN_PRIORITIES_ID +
                " INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT, " + COLUMN_PRIORITIES_NAME +
                " TEXT NOT NULL)");
        db.execSQL("INSERT INTO "+ TABLE_PRIORITIES +" (" + COLUMN_PRIORITIES_NAME
                + ") VALUES ('Төмен'), ('Орташа'), ('Жоғары');");

        // statuses
        db.execSQL("CREATE TABLE " + TABLE_STATUSES + "(" + COLUMN_STATUSES_ID +
                " INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT, " + COLUMN_STATUSES_STATUS +
                " TEXT NOT NULL)");
        db.execSQL("INSERT INTO "+ TABLE_STATUSES +" (" + COLUMN_STATUSES_STATUS
                + ") VALUES ('Болашақта'), ('Істелуде'), ('Орындалды'), ('Тасталынды');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_METHODS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }
}
