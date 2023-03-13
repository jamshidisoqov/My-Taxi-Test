package uz.gita.my_taxi_test.presentation.presenter.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.gita.my_taxi_test.data.local.entity.LocationEntity
import uz.gita.my_taxi_test.domain.useCase.GetLastLocationUseCase
import uz.gita.my_taxi_test.presentation.presenter.MainViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModelImpl @Inject constructor(
    private val getLastLocationUseCase: GetLastLocationUseCase
) : MainViewModel, ViewModel() {

    override val lastLocationFlow = MutableSharedFlow<LocationEntity>()

    init {
        viewModelScope.launch {
            getLastLocationUseCase.getLastLocation().onEach {
                println("TTTTT")
                lastLocationFlow.emit(it)
            }.launchIn(viewModelScope)
        }
    }

}