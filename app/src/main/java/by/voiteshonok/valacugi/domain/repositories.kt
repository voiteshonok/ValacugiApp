package by.voiteshonok.valacugi.domain

import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun observeUsers(): Flow<List<User>>
    fun observeUser(userId: String): Flow<User?>
    suspend fun authenticate(login: String, password: String): User?
    suspend fun setPushNotificationsEnabled(userId: String, isEnabled: Boolean)
}

interface ThreadsRepository {
    fun observeThreads(): Flow<List<MessageThread>>
    fun observeThreadsForUser(userId: String): Flow<List<MessageThread>>
    fun observeThread(threadId: String): Flow<MessageThread?>
}

interface MessagesRepository {
    fun observeMessages(threadId: String): Flow<List<Message>>
    suspend fun sendMessage(threadId: String, senderId: String, body: String)
    suspend fun markThreadAsRead(threadId: String, userId: String)
}

interface TripsRepository {
    fun observeTrips(): Flow<List<Trip>>
    fun observeTripItinerary(tripId: String): Flow<TripItinerary?>
    fun observeIsUserAssignedToTrip(tripId: String, userId: String): Flow<Boolean>
    suspend fun assignUserToTrip(tripId: String, userId: String)
    suspend fun unassignUserFromTrip(tripId: String, userId: String)
}

