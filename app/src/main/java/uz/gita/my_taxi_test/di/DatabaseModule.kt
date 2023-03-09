package uz.gita.my_taxi_test.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.gita.my_taxi_test.data.local.AppDatabase
import uz.gita.my_taxi_test.data.local.dao.LocationDao
import javax.inject.Singleton

// Created by Jamshid Isoqov on 3/9/2023
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @[Provides Singleton]
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "locations.db")
            .build()

    @[Provides Singleton]
    fun provideLocationDao(database: AppDatabase):LocationDao = database.getLocationDao()

}