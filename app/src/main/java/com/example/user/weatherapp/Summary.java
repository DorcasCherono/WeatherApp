package com.example.user.weatherapp;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Table(database = AppDatabase.class, allFields = true)
public class Summary extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private int id;
    private double latitude;
    private double longitude;
    private double temperature;
    private String summary;
    private long time;

    public Summary() {
    }

    public Summary(double latitude, double longitude, double temperature, String summary, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.summary = summary;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%s\n%s, %s\n%s\n%s\n%s",
                this.id,
                this.latitude, this.longitude,
                this.temperature,
                this.summary,
                new SimpleDateFormat("EEEEEEEEEE MMMMMMMMMM, yyyy 'at' HH:mm", Locale.getDefault()).format(new Date(this.time * 1000))
        );
    }
}
