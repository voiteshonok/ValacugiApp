package by.voiteshonok.valacugi.domain

import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun observeUsers(): Flow<List<User>>
    fun observeUser(userId: String): Flow<User?>
    suspend fun authenticate(login: String, password: String): User?
    suspend fun setPushNotificationsEnabled(userId: String, isEnabled: Boolean)
}

interface TripsRepository {
    fun observeTrips(): Flow<List<Trip>>
    fun observeTripItinerary(tripId: String): Flow<TripItinerary?>
    fun observeIsUserAssignedToTrip(tripId: String, userId: String): Flow<Boolean>
    suspend fun assignUserToTrip(tripId: String, userId: String)
    suspend fun unassignUserFromTrip(tripId: String, userId: String)
}

