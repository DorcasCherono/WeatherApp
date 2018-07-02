package com.example.user.weatherapp;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    public static final String NAME = "ApplicationDatabase";
    public static final int VERSION = 2;

}
