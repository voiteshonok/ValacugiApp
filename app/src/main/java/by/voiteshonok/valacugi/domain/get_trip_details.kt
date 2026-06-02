package by.voiteshonok.valacugi.domain

import kotlinx.coroutines.flow.Flow

class GetTripDetails(
    private val tripsRepository: TripsRepository
) {
    fun execute(tripId: String): Flow<TripItinerary?> {
        return tripsRepository.observeTripItinerary(tripId = tripId)
    }
}

