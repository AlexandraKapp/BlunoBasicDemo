package com.dfrobot.angelo.blunobasicdemo;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Temperature.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TemperatureDao temperatureDao();
}

