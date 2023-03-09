package uz.gita.my_taxi_test.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.my_taxi_test.domain.repository.LocationRepository
import uz.gita.my_taxi_test.domain.repository.impl.LocationRepositoryImpl
import javax.inject.Singleton

// Created by Jamshid Isoqov on 3/9/2023
@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @[Binds Singleton]
    fun bindLocation(impl: LocationRepositoryImpl): LocationRepository
}