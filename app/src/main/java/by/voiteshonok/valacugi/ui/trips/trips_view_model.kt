package by.voiteshonok.valacugi.ui.trips

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TripsViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<TripsUiState> = MutableStateFlow(
        TripsUiState(
            title = "TRIPS",
            subtitle = "Placeholder screen. Next: seeded list from Room."
        )
    )
    val uiState: StateFlow<TripsUiState> = _uiState.asStateFlow()
}

