package com.example.user.weatherapp;

import android.app.Application;
import android.util.Log;

import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class WeatherApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this)
                .addDatabaseConfig(
                        new DatabaseConfig.Builder(AppDatabase.class)
                                .build())
                .build());

        Log.d("WeatherApplication", "onCreate");
    }
}
