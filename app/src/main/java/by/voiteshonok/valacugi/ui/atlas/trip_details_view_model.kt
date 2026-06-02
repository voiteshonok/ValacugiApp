package by.voiteshonok.valacugi.ui.atlas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import by.voiteshonok.valacugi.domain.GetTripDetails
import by.voiteshonok.valacugi.domain.TripItinerary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripDetailsViewModel(
    private val tripId: String,
    private val getTripDetails: GetTripDetails
) : ViewModel() {
    private val _uiState: MutableStateFlow<TripDetailsUiState> = MutableStateFlow(
        TripDetailsUiState(
            isLoading = true,
            itinerary = null
        )
    )
    val uiState: StateFlow<TripDetailsUiState> = _uiState.asStateFlow()

    init {
        observeTrip()
    }

    private fun observeTrip() {
        viewModelScope.launch {
            getTripDetails.execute(tripId = tripId).collect { itinerary: TripItinerary? ->
                _uiState.update { previousState ->
                    previousState.copy(
                        isLoading = false,
                        itinerary = itinerary
                    )
                }
            }
        }
    }
}

class TripDetailsViewModelFactory(
    private val tripId: String,
    private val getTripDetails: GetTripDetails
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripDetailsViewModel(tripId = tripId, getTripDetails = getTripDetails) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

