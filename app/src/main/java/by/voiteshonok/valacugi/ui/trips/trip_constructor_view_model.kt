package by.voiteshonok.valacugi.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import by.voiteshonok.valacugi.core.session.SessionRepository
import by.voiteshonok.valacugi.core.trip_creation.TripCreationDraft
import by.voiteshonok.valacugi.core.trip_creation.TripStepDraft
import by.voiteshonok.valacugi.domain.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripConstructorViewModel(
    private val draft: TripCreationDraft,
    private val tripsRepository: TripsRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<TripConstructorUiState> = MutableStateFlow(TripConstructorUiState())
    val uiState: StateFlow<TripConstructorUiState> = _uiState.asStateFlow()

    fun finalizeItinerary(steps: List<TripStepDraft>, onSuccess: () -> Unit) {
        if (_uiState.value.isFinalizing) {
            return
        }
        viewModelScope.launch {
            _uiState.update { previousState -> previousState.copy(isFinalizing = true, errorMessage = null) }
            val createdByUserId: String? = sessionRepository.observeSession().first()?.identification
            if (createdByUserId.isNullOrBlank()) {
                _uiState.update { previousState ->
                    previousState.copy(
                        isFinalizing = false,
                        errorMessage = "SESSION REQUIRED"
                    )
                }
                return@launch
            }
            if (draft.isEditMode) {
                tripsRepository.updateTripFromDraft(draft = draft, steps = steps)
            } else {
                tripsRepository.createTripFromDraft(
                    draft = draft,
                    steps = steps,
                    createdByUserId = createdByUserId
                )
            }
            _uiState.update { previousState -> previousState.copy(isFinalizing = false) }
            onSuccess()
        }
    }
}

class TripConstructorViewModelFactory(
    private val draft: TripCreationDraft,
    private val tripsRepository: TripsRepository,
    private val sessionRepository: SessionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripConstructorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripConstructorViewModel(
                draft = draft,
                tripsRepository = tripsRepository,
                sessionRepository = sessionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
