package by.voiteshonok.valacugi.ui.atlas

import by.voiteshonok.valacugi.domain.TripItinerary

data class TripDetailsUiState(
    val isLoading: Boolean,
    val itinerary: TripItinerary?
)

