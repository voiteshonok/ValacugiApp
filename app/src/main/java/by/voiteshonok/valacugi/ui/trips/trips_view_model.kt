package by.voiteshonok.valacugi.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import by.voiteshonok.valacugi.domain.Trip
import by.voiteshonok.valacugi.domain.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripsViewModel(
    private val tripsRepository: TripsRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<TripsUiState> = MutableStateFlow(
        TripsUiState(
            title = "TRIPS",
            subtitle = "",
            trips = emptyList()
        )
    )
    val uiState: StateFlow<TripsUiState> = _uiState.asStateFlow()

    init {
        observeTrips()
    }

    private fun observeTrips() {
        viewModelScope.launch {
            tripsRepository.observeTrips().collect { trips: List<Trip> ->
                _uiState.update { previousState ->
                    previousState.copy(trips = trips)
                }
            }
        }
    }
}

class TripsViewModelFactory(
    private val tripsRepository: TripsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripsViewModel(tripsRepository = tripsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

