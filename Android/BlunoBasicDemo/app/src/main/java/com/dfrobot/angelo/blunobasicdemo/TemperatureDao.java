package com.dfrobot.angelo.blunobasicdemo;

import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Delete;

import java.sql.Timestamp;
import java.util.List;

@Dao
public interface TemperatureDao {
    @Query("SELECT * FROM temperature")
    List<Temperature> getAll();

    @Query("SELECT * FROM temperature WHERE uid IN (:temperatureIds)")
    List<Temperature> loadAllByIds(int[] temperatureIds);

    @Query("SELECT * FROM temperature WHERE timestamp LIKE :time LIMIT 1")
    Temperature findByTimestamp(long time);


    // TODO: proper insert statement
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertTemperature(Temperature temperature);

    @Insert
    void insertAll(Temperature... temperatures);

    @Delete
    void delete(Temperature temperature);
}

