package com.dfrobot.angelo.blunobasicdemo

import android.app.Application
import androidx.room.Room

class MyApp : Application() {

    companion object DatabaseSetup {
        var database: AppDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()
        MyApp.database =  Room.databaseBuilder(this, AppDatabase::class.java, "temperature-db").build()
    }
}