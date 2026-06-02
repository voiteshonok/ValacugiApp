package by.voiteshonok.valacugi.domain

import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun observeUsers(): Flow<List<User>>
}

interface TripsRepository {
    fun observeTrips(): Flow<List<Trip>>
    fun observeTripItinerary(tripId: String): Flow<TripItinerary?>
}

