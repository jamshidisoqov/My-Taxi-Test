package uz.gita.my_taxi_test.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

// Created by Jamshid Isoqov on 3/9/2023
@Parcelize
@Entity(tableName = "location_entity")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val lat: Double,
    val lng: Double,
    val bearing: Double
) : Parcelable
