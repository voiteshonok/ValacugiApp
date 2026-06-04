package by.voiteshonok.valacugi.data.room

import android.content.Context
import androidx.room.Room

internal fun createInMemoryTestDatabase(context: Context): AppDatabase {
    return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()
}

internal suspend fun seedHanoiUnreadScenario(database: AppDatabase) {
    val adminUserId: String = AdminUserId
    val regularUserId: String = RegularUserId
    val hanoiTripId: String = "trip_hanoi"
    database.usersDao().insertAll(
        users = listOf(
            UserEntity(
                userId = adminUserId,
                login = "admin",
                password = "admin",
                displayName = "Administrator"
            ),
            UserEntity(
                userId = regularUserId,
                login = "user",
                password = "user",
                displayName = "User"
            )
        )
    )
    database.tripsDao().insertTrips(
        trips = listOf(
            TripEntity(
                tripId = hanoiTripId,
                title = "HANOI",
                dateStart = "2026-07-12",
                dateEnd = "2026-07-16",
                pax = 2,
                budgetText = null,
                createdById = adminUserId
            )
        )
    )
    database.tripsDao().insertAssignments(
        assignments = listOf(
            TripAssignmentEntity(tripId = hanoiTripId, personId = adminUserId),
            TripAssignmentEntity(tripId = hanoiTripId, personId = regularUserId)
        )
    )
    database.threadsDao().insertAll(
        threads = listOf(
            ThreadEntity(
                threadId = HanoiThreadId,
                tripId = hanoiTripId,
                title = "HANOI TRIP",
                lastMessagePreview = "Street food tour meets at 19:00.",
                lastMessageAt = "2026-07-14T19:00:00"
            )
        )
    )
    database.messagesDao().insertAll(messages = buildHanoiThreadMessages())
    database.lastReadMessagesDao().insertAll(lastReadMessages = buildHanoiLastReadMessages())
}

internal suspend fun seedThreadWithoutMessages(database: AppDatabase, userId: String) {
    val tripId: String = "trip_london"
    database.tripsDao().insertTrips(
        trips = listOf(
            TripEntity(
                tripId = tripId,
                title = "LONDON",
                dateStart = null,
                dateEnd = null,
                pax = 2,
                budgetText = "£2400",
                createdById = AdminUserId
            )
        )
    )
    database.tripsDao().insertAssignments(
        assignments = listOf(TripAssignmentEntity(tripId = tripId, personId = userId))
    )
    database.threadsDao().insertAll(
        threads = listOf(
            ThreadEntity(
                threadId = "thread_london",
                tripId = tripId,
                title = "LONDON EXPEDITION",
                lastMessagePreview = "Hotel booked at The Savoy.",
                lastMessageAt = "2026-06-03T09:15:00"
            )
        )
    )
}
