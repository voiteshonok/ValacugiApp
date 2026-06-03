package by.voiteshonok.valacugi.ui.atlas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import by.voiteshonok.valacugi.core.session.SessionRepository
import by.voiteshonok.valacugi.domain.GetTripDetails
import by.voiteshonok.valacugi.domain.TripItinerary
import by.voiteshonok.valacugi.domain.TripsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TripDetailsViewModel(
    private val tripId: String,
    private val getTripDetails: GetTripDetails,
    private val tripsRepository: TripsRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<TripDetailsUiState> = MutableStateFlow(TripDetailsUiState())
    val uiState: StateFlow<TripDetailsUiState> = _uiState.asStateFlow()

    init {
        observeTrip()
    }

    fun onMembershipButtonClick() {
        val userId: String = _uiState.value.currentUserId ?: return
        if (_uiState.value.isMembershipActionInProgress) {
            return
        }
        viewModelScope.launch {
            _uiState.update { previousState ->
                previousState.copy(isMembershipActionInProgress = true)
            }
            if (_uiState.value.isCurrentUserAssigned) {
                tripsRepository.unassignUserFromTrip(tripId = tripId, userId = userId)
            } else {
                tripsRepository.assignUserToTrip(tripId = tripId, userId = userId)
            }
            _uiState.update { previousState ->
                previousState.copy(isMembershipActionInProgress = false)
            }
        }
    }

    private fun observeTrip() {
        viewModelScope.launch {
            sessionRepository.observeSession()
                .map { session -> session?.identification }
                .flatMapLatest { userId: String? ->
                    val itineraryFlow = getTripDetails.execute(tripId = tripId)
                    if (userId.isNullOrBlank()) {
                        itineraryFlow.map { itinerary: TripItinerary? ->
                            buildUiState(
                                itinerary = itinerary,
                                currentUserId = null,
                                isCurrentUserAssigned = false
                            )
                        }
                    } else {
                        combine(
                            itineraryFlow,
                            tripsRepository.observeIsUserAssignedToTrip(tripId = tripId, userId = userId)
                        ) { itinerary: TripItinerary?, isCurrentUserAssigned: Boolean ->
                            buildUiState(
                                itinerary = itinerary,
                                currentUserId = userId,
                                isCurrentUserAssigned = isCurrentUserAssigned
                            )
                        }
                    }
                }
                .collect { nextState: TripDetailsUiState ->
                    _uiState.update { previousState ->
                        nextState.copy(isMembershipActionInProgress = previousState.isMembershipActionInProgress)
                    }
                }
        }
    }

    private fun buildUiState(
        itinerary: TripItinerary?,
        currentUserId: String?,
        isCurrentUserAssigned: Boolean
    ): TripDetailsUiState {
        return TripDetailsUiState(
            isLoading = false,
            itinerary = itinerary,
            currentUserId = currentUserId,
            isCurrentUserAssigned = isCurrentUserAssigned
        )
    }
}

class TripDetailsViewModelFactory(
    private val tripId: String,
    private val getTripDetails: GetTripDetails,
    private val tripsRepository: TripsRepository,
    private val sessionRepository: SessionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripDetailsViewModel(
                tripId = tripId,
                getTripDetails = getTripDetails,
                tripsRepository = tripsRepository,
                sessionRepository = sessionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
