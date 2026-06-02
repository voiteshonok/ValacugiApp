package by.voiteshonok.valacugi.ui.trips

import by.voiteshonok.valacugi.domain.Trip

data class TripsUiState(
    val title: String,
    val subtitle: String,
    val trips: List<Trip>
)

