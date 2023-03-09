package uz.gita.my_taxi_test.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.gita.my_taxi_test.data.local.entity.LocationEntity

// Created by Jamshid Isoqov on 3/9/2023
@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(locationEntity: LocationEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLocation(locationEntity: LocationEntity)

    @Query("SELECT*FROM location_entity")
    fun getAllLocation(): Flow<List<LocationEntity>>

}