package uz.gita.my_taxi_test.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uz.gita.my_taxi_test.domain.useCase.AddLocationUseCase
import uz.gita.my_taxi_test.domain.useCase.GetLastLocationUseCase
import uz.gita.my_taxi_test.domain.useCase.impl.AddLocationUseCaseImpl
import uz.gita.my_taxi_test.domain.useCase.impl.GetLastLocationUseCaseImpl

// Created by Jamshid Isoqov on 3/9/2023
@Module
@InstallIn(ViewModelComponent::class)
interface UseCaseModule {

    @Binds
    fun bindAddLocation(impl: AddLocationUseCaseImpl): AddLocationUseCase

    @Binds
    fun bindGetLastLocation(impl: GetLastLocationUseCaseImpl): GetLastLocationUseCase

}