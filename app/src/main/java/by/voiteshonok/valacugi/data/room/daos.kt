package by.voiteshonok.valacugi.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {
    @Query("SELECT * FROM users ORDER BY login ASC")
    fun observeUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    fun observeUser(userId: String): Flow<UserEntity?>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int

    @Query(
        """
        SELECT * FROM users
        WHERE LOWER(login) = LOWER(:login) AND password = :password
        LIMIT 1
        """
    )
    suspend fun findByCredentials(login: String, password: String): UserEntity?

    @Query(
        """
        UPDATE users
        SET push_notifications_enabled = :isEnabled
        WHERE user_id = :userId
        """
    )
    suspend fun updatePushNotificationsEnabled(userId: String, isEnabled: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)
}

@Dao
interface TripsDao {
    @Query(
        """
        SELECT trips.*,
               (SELECT COUNT(*) FROM trip_assignments WHERE trip_assignments.trip_id = trips.trip_id) AS assigned_count
        FROM trips
        ORDER BY title ASC
        """
    )
    fun observeTrips(): Flow<List<TripWithAssignedCountEntity>>

    @Query("SELECT * FROM trips ORDER BY title ASC")
    fun observeTripEntities(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE trip_id = :tripId LIMIT 1")
    fun observeTripEntity(tripId: String): Flow<TripEntity?>

    @Query("SELECT * FROM trip_assignments")
    fun observeTripAssignments(): Flow<List<TripAssignmentEntity>>

    @Query("SELECT COUNT(*) FROM trips")
    suspend fun getTripsCount(): Int

    @Query("SELECT * FROM trips ORDER BY title ASC")
    suspend fun getAllTrips(): List<TripEntity>

    @Query(
        """
        SELECT trips.*,
               (SELECT COUNT(*) FROM trip_assignments WHERE trip_assignments.trip_id = trips.trip_id) AS assigned_count
        FROM trips
        WHERE trip_id = :tripId
        LIMIT 1
        """
    )
    fun observeTrip(tripId: String): Flow<TripWithAssignedCountEntity?>

    @Query("SELECT * FROM itinerary_days WHERE trip_id = :tripId ORDER BY day_index ASC")
    fun observeDays(tripId: String): Flow<List<ItineraryDayEntity>>

    @androidx.room.Transaction
    @Query("SELECT * FROM itinerary_days WHERE trip_id = :tripId ORDER BY day_index ASC")
    fun observeItineraryDays(tripId: String): Flow<List<ItineraryDayWithStepsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAssignments(assignments: List<TripAssignmentEntity>)

    @Query(
        """
        SELECT COUNT(*) FROM trip_assignments
        WHERE trip_id = :tripId AND person_id = :userId
        """
    )
    fun observeUserAssignmentCount(tripId: String, userId: String): Flow<Int>

    @Query(
        """
        DELETE FROM trip_assignments
        WHERE trip_id = :tripId AND person_id = :userId
        """
    )
    suspend fun deleteAssignment(tripId: String, userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(days: List<ItineraryDayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<ItineraryStepEntity>)
}

@Dao
interface ThreadsDao {
    @Query("SELECT * FROM threads ORDER BY last_message_at DESC")
    fun observeThreads(): Flow<List<ThreadEntity>>

    @Query(
        """
        SELECT threads.*,
        CASE WHEN EXISTS (
            SELECT 1 FROM messages
            WHERE messages.thread_id = threads.thread_id
            AND messages.sent_at > COALESCE(
                (
                    SELECT last_read_messages.seen_at
                    FROM last_read_messages
                    WHERE last_read_messages.thread_id = threads.thread_id
                    AND last_read_messages.user_id = :userId
                ),
                ''
            )
        ) THEN 1 ELSE 0 END AS has_unread
        FROM threads
        INNER JOIN trip_assignments ON trip_assignments.trip_id = threads.trip_id
        WHERE trip_assignments.person_id = :userId
        ORDER BY threads.last_message_at DESC
        """
    )
    fun observeThreadsForUser(userId: String): Flow<List<ThreadWithUnreadEntity>>

    @Query("SELECT * FROM threads WHERE thread_id = :threadId LIMIT 1")
    fun observeThread(threadId: String): Flow<ThreadEntity?>

    @Query("SELECT COUNT(*) FROM threads")
    suspend fun getThreadsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(threads: List<ThreadEntity>)

    @Query(
        """
        UPDATE threads
        SET last_message_preview = :preview,
            last_message_at = :sentAt
        WHERE thread_id = :threadId
        """
    )
    suspend fun updateLastMessage(
        threadId: String,
        preview: String,
        sentAt: String
    )
}

@Dao
interface LastReadMessagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lastRead: LastReadMessageEntity)

    @Query("SELECT COUNT(*) FROM last_read_messages WHERE thread_id = :threadId")
    suspend fun getLastReadCountForThread(threadId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lastReadMessages: List<LastReadMessageEntity>)

    @Query(
        """
        SELECT * FROM last_read_messages
        WHERE thread_id = :threadId AND user_id = :userId
        LIMIT 1
        """
    )
    suspend fun getLastReadForUser(threadId: String, userId: String): LastReadMessageEntity?
}

@Dao
interface MessagesDao {
    @Query("SELECT * FROM messages WHERE thread_id = :threadId ORDER BY sent_at ASC")
    fun observeMessages(threadId: String): Flow<List<MessageEntity>>

    @Query("SELECT COUNT(*) FROM messages WHERE thread_id = :threadId")
    suspend fun getMessageCountForThread(threadId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query(
        """
        SELECT * FROM messages
        WHERE thread_id = :threadId
        ORDER BY sent_at DESC
        LIMIT 1
        """
    )
    suspend fun getLatestMessageForThread(threadId: String): MessageEntity?
}

