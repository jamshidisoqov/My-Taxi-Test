package uz.gita.my_taxi_test.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.gita.my_taxi_test.data.local.dao.LocationDao
import uz.gita.my_taxi_test.data.local.entity.LocationEntity

// Created by Jamshid Isoqov on 3/9/2023
@Database(entities = [LocationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getLocationDao(): LocationDao

}