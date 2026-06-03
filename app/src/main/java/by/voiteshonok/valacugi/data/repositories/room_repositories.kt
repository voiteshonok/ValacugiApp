package by.voiteshonok.valacugi.data.repositories

import by.voiteshonok.valacugi.data.toDomain
import by.voiteshonok.valacugi.data.room.MessageEntity
import by.voiteshonok.valacugi.data.room.MessagesDao
import by.voiteshonok.valacugi.data.room.ThreadsDao
import by.voiteshonok.valacugi.data.room.createMessageId
import by.voiteshonok.valacugi.data.room.createSentAtIsoTimestamp
import by.voiteshonok.valacugi.data.room.truncateMessagePreview
import by.voiteshonok.valacugi.data.room.TripAssignmentEntity
import by.voiteshonok.valacugi.data.room.TripsDao
import by.voiteshonok.valacugi.data.room.UsersDao
import by.voiteshonok.valacugi.domain.ItineraryDayWithSteps
import by.voiteshonok.valacugi.domain.Message
import by.voiteshonok.valacugi.domain.MessagesRepository
import by.voiteshonok.valacugi.domain.MessageThread
import by.voiteshonok.valacugi.domain.ThreadsRepository
import by.voiteshonok.valacugi.domain.TripItinerary
import by.voiteshonok.valacugi.domain.TripsRepository
import by.voiteshonok.valacugi.domain.UsersRepository
import by.voiteshonok.valacugi.domain.User
import by.voiteshonok.valacugi.domain.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class RoomUsersRepository(
    private val usersDao: UsersDao
) : UsersRepository {
    override fun observeUsers(): Flow<List<User>> {
        return usersDao.observeUsers().map { entities -> entities.map { it.toDomain() } }
    }
    override fun observeUser(userId: String): Flow<User?> {
        return usersDao.observeUser(userId = userId).map { entity -> entity?.toDomain() }
    }
    override suspend fun authenticate(login: String, password: String): User? {
        val entity = usersDao.findByCredentials(login = login.trim(), password = password)
        return entity?.toDomain()
    }
    override suspend fun setPushNotificationsEnabled(userId: String, isEnabled: Boolean) {
        usersDao.updatePushNotificationsEnabled(userId = userId, isEnabled = isEnabled)
    }
}

class RoomMessagesRepository(
    private val messagesDao: MessagesDao,
    private val threadsDao: ThreadsDao
) : MessagesRepository {
    override fun observeMessages(threadId: String): Flow<List<Message>> {
        return messagesDao.observeMessages(threadId = threadId).map { entities ->
            entities.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun sendMessage(threadId: String, senderId: String, body: String) {
        val trimmedBody: String = body.trim()
        if (trimmedBody.isEmpty()) {
            return
        }
        val sentAt: String = createSentAtIsoTimestamp()
        val messageEntity: MessageEntity = MessageEntity(
            messageId = createMessageId(threadId = threadId),
            threadId = threadId,
            senderId = senderId,
            body = trimmedBody,
            sentAt = sentAt
        )
        messagesDao.insertMessage(message = messageEntity)
        threadsDao.updateLastMessage(
            threadId = threadId,
            preview = truncateMessagePreview(body = trimmedBody),
            sentAt = sentAt,
            hasUnread = false
        )
    }
}

class RoomThreadsRepository(
    private val threadsDao: ThreadsDao
) : ThreadsRepository {
    override fun observeThreads(): Flow<List<MessageThread>> {
        return threadsDao.observeThreads().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeThreadsForUser(userId: String): Flow<List<MessageThread>> {
        return threadsDao.observeThreadsForUser(userId = userId).map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeThread(threadId: String): Flow<MessageThread?> {
        return threadsDao.observeThread(threadId = threadId).map { entity -> entity?.toDomain() }
    }
}

class RoomTripsRepository(
    private val tripsDao: TripsDao
) : TripsRepository {
    override fun observeTrips(): Flow<List<Trip>> {
        return tripsDao.observeTrips().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeTripItinerary(tripId: String): Flow<TripItinerary?> {
        return tripsDao.observeTrip(tripId).combine(tripsDao.observeItineraryDays(tripId)) { tripEntity, dayWithSteps ->
            if (tripEntity == null) return@combine null
            TripItinerary(
                trip = tripEntity.toDomain(),
                days = dayWithSteps.map { dayWithStepsEntity ->
                    ItineraryDayWithSteps(
                        day = dayWithStepsEntity.day.toDomain(),
                        steps = dayWithStepsEntity.steps.map { it.toDomain() }
                    )
                }
            )
        }
    }

    override fun observeIsUserAssignedToTrip(tripId: String, userId: String): Flow<Boolean> {
        return tripsDao.observeUserAssignmentCount(tripId = tripId, userId = userId)
            .map { assignmentCount: Int -> assignmentCount > 0 }
    }

    override suspend fun assignUserToTrip(tripId: String, userId: String) {
        tripsDao.insertAssignments(
            assignments = listOf(
                TripAssignmentEntity(tripId = tripId, personId = userId)
            )
        )
    }

    override suspend fun unassignUserFromTrip(tripId: String, userId: String) {
        tripsDao.deleteAssignment(tripId = tripId, userId = userId)
    }
}

