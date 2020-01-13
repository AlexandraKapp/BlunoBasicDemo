package com.dfrobot.angelo.blunobasicdemo;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.Entity;

import java.sql.Timestamp;

@Entity
public class Temperature {

    @PrimaryKey(autoGenerate = true)
    public Integer uid;

    @ColumnInfo(name = "timestamp")
    public long timestamp;

    // todo: change to double
    @ColumnInfo(name = "value")
    public double value;


    public Temperature(long timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }
}