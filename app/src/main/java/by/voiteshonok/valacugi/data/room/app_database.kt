package by.voiteshonok.valacugi.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val DatabaseName: String = "valacugi.db"

@Database(
    entities = [
        UserEntity::class,
        TripEntity::class,
        TripAssignmentEntity::class,
        ItineraryDayEntity::class,
        ItineraryStepEntity::class,
        ThreadEntity::class,
        MessageEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun tripsDao(): TripsDao
    abstract fun threadsDao(): ThreadsDao
    abstract fun messagesDao(): MessagesDao

    companion object {
        fun create(context: Context): AppDatabase {
            val database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, DatabaseName)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
            CoroutineScope(Dispatchers.IO).launch {
                ValacugiSeeder.ensureSeedAndPatches(database = database)
            }
            return database
        }
    }
}

private object ValacugiSeeder {
    private const val HanoiTripId: String = "trip_hanoi"
    private const val RegularUserId: String = "user_user"

    suspend fun ensureSeedAndPatches(database: AppDatabase) {
        val tripsCount: Int = database.tripsDao().getTripsCount()
        if (tripsCount == 0) {
            seed(database = database)
        }
        applyPatches(database = database)
    }

    suspend fun applyPatches(database: AppDatabase) {
        database.tripsDao().insertAssignments(
            listOf(
                TripAssignmentEntity(tripId = HanoiTripId, personId = RegularUserId)
            )
        )
        ensureThreadsSeeded(database = database)
        ensureHanoiMessagesSeeded(database = database)
    }

    private suspend fun ensureHanoiMessagesSeeded(database: AppDatabase) {
        val messageCount: Int = database.messagesDao().getMessageCountForThread(threadId = HanoiThreadId)
        if (messageCount > 0) {
            return
        }
        database.messagesDao().insertAll(messages = buildHanoiThreadMessages())
    }

    private suspend fun ensureThreadsSeeded(database: AppDatabase) {
        val threadsCount: Int = database.threadsDao().getThreadsCount()
        if (threadsCount > 0) {
            return
        }
        val trips: List<TripEntity> = database.tripsDao().getAllTrips()
        if (trips.isEmpty()) {
            return
        }
        database.threadsDao().insertAll(threads = buildThreadsForTrips(trips = trips))
    }

    suspend fun seed(database: AppDatabase) {
        val adminUserId: String = "user_admin"
        val regularUserId: String = "user_user"
        val users: List<UserEntity> = listOf(
            UserEntity(
                userId = adminUserId,
                login = "admin",
                password = "admin",
                displayName = "Administrator",
                pushNotificationsEnabled = true
            ),
            UserEntity(
                userId = regularUserId,
                login = "user",
                password = "user",
                displayName = "User",
                pushNotificationsEnabled = true
            )
        )
        val tokyoTripId: String = "trip_tokyo"
        val londonTripId: String = "trip_london"
        val hanoiTripId: String = HanoiTripId
        val trips: List<TripEntity> = listOf(
            TripEntity(
                tripId = londonTripId,
                title = "LONDON",
                dateStart = null,
                dateEnd = null,
                pax = 2,
                budgetText = "£2400",
                createdById = adminUserId
            ),
            TripEntity(
                tripId = tokyoTripId,
                title = "TOKYO",
                dateStart = "2026-08-24",
                dateEnd = "2026-08-30",
                pax = 4,
                budgetText = "¥500k",
                createdById = adminUserId
            ),
            TripEntity(
                tripId = "trip_berlin",
                title = "BERLIN",
                dateStart = "2026-09-03",
                dateEnd = "2026-09-06",
                pax = 1,
                budgetText = "€980",
                createdById = adminUserId
            ),
            TripEntity(
                tripId = "trip_oslo",
                title = "OSLO",
                dateStart = null,
                dateEnd = null,
                pax = 3,
                budgetText = null,
                createdById = adminUserId
            ),
            TripEntity(
                tripId = "trip_lisbon",
                title = "LISBON",
                dateStart = "2026-06-18",
                dateEnd = "2026-06-22",
                pax = 2,
                budgetText = "€1450",
                createdById = adminUserId
            ),
            TripEntity(
                tripId = "trip_reykjavik",
                title = "REYKJAVIK",
                dateStart = "2026-11-02",
                dateEnd = "2026-11-08",
                pax = null,
                budgetText = "≈ €3,400",
                createdById = adminUserId
            ),
            TripEntity(
                tripId = "trip_seoul",
                title = "SEOUL",
                dateStart = "2026-10-10",
                dateEnd = "2026-10-17",
                pax = 4,
                budgetText = "₩4.2m",
                createdById = adminUserId
            ),
            TripEntity(
                tripId = hanoiTripId,
                title = "HANOI",
                dateStart = "2026-07-12",
                dateEnd = "2026-07-16",
                pax = 2,
                budgetText = null,
                createdById = adminUserId
            ),
            TripEntity(
                tripId = "trip_nairobi",
                title = "NAIROBI",
                dateStart = null,
                dateEnd = "2026-12-03",
                pax = 6,
                budgetText = "$6,500",
                createdById = adminUserId
            ),
            TripEntity(
                tripId = "trip_istanbul",
                title = "ISTANBUL",
                dateStart = "2026-05-27",
                dateEnd = "2026-06-01",
                pax = 2,
                budgetText = "₺38k",
                createdById = adminUserId
            ),
            TripEntity(
                tripId = "trip_mexico_city",
                title = "MEXICO CITY",
                dateStart = "2026-09-20",
                dateEnd = "2026-09-28",
                pax = 5,
                budgetText = "$3,900",
                createdById = adminUserId
            ),
            TripEntity(
                tripId = "trip_singapore",
                title = "SINGAPORE",
                dateStart = "2026-08-02",
                dateEnd = "2026-08-05",
                pax = 2,
                budgetText = "S$2,100",
                createdById = adminUserId
            )
        )
        val assignments: List<TripAssignmentEntity> = trips.flatMap { trip ->
            buildList {
                add(TripAssignmentEntity(tripId = trip.tripId, personId = adminUserId))
                if (trip.tripId == hanoiTripId) {
                    add(TripAssignmentEntity(tripId = trip.tripId, personId = regularUserId))
                }
            }
        }
        val itineraryDays: List<ItineraryDayEntity> = listOf(
            ItineraryDayEntity(dayId = "day_tokyo_01", tripId = tokyoTripId, dayIndex = 1, title = "DAY 01 — ARRIVAL"),
            ItineraryDayEntity(dayId = "day_tokyo_02", tripId = tokyoTripId, dayIndex = 2, title = "DAY 02 — CORE LOOP"),
            ItineraryDayEntity(dayId = "day_tokyo_03", tripId = tokyoTripId, dayIndex = 3, title = "DAY 03 — DEPARTURE"),
            ItineraryDayEntity(dayId = "day_london_01", tripId = londonTripId, dayIndex = 1, title = "DAY 01 — ARRIVE"),
            ItineraryDayEntity(dayId = "day_london_02", tripId = londonTripId, dayIndex = 2, title = "DAY 02 — MUSEUMS"),
            ItineraryDayEntity(dayId = "day_berlin_01", tripId = "trip_berlin", dayIndex = 1, title = "DAY 01 — CHECKPOINTS"),
            ItineraryDayEntity(dayId = "day_berlin_02", tripId = "trip_berlin", dayIndex = 2, title = "DAY 02 — GRID WALK"),
            ItineraryDayEntity(dayId = "day_seoul_01", tripId = "trip_seoul", dayIndex = 1, title = "DAY 01 — ORIENTATION"),
            ItineraryDayEntity(dayId = "day_seoul_02", tripId = "trip_seoul", dayIndex = 2, title = "DAY 02 — NIGHT MARKET")
        )
        val itinerarySteps: List<ItineraryStepEntity> = listOf(
            ItineraryStepEntity(stepId = "step_tokyo_01_01", dayId = "day_tokyo_01", stepIndex = 1, title = "TRANSFER TO CITY", timeText = "09:30", budgetText = null, maxPeople = null),
            ItineraryStepEntity(stepId = "step_tokyo_01_02", dayId = "day_tokyo_01", stepIndex = 2, title = "CHECK-IN", timeText = "11:00", budgetText = "≈ ¥18,000", maxPeople = 2),
            ItineraryStepEntity(stepId = "step_tokyo_02_01", dayId = "day_tokyo_02", stepIndex = 1, title = "MUSEUM WINDOW", timeText = null, budgetText = null, maxPeople = 4),
            ItineraryStepEntity(stepId = "step_tokyo_02_02", dayId = "day_tokyo_02", stepIndex = 2, title = "DINNER SLOT", timeText = "19:00", budgetText = null, maxPeople = null),
            ItineraryStepEntity(stepId = "step_tokyo_03_01", dayId = "day_tokyo_03", stepIndex = 1, title = "CHECK-OUT", timeText = "08:00", budgetText = null, maxPeople = 2),
            ItineraryStepEntity(stepId = "step_tokyo_03_02", dayId = "day_tokyo_03", stepIndex = 2, title = "AIRPORT TRANSIT", timeText = "10:30", budgetText = "≈ ¥3,200", maxPeople = null),
            ItineraryStepEntity(stepId = "step_london_01_01", dayId = "day_london_01", stepIndex = 1, title = "HEATHROW TRANSFER", timeText = "10:15", budgetText = "£35", maxPeople = null),
            ItineraryStepEntity(stepId = "step_london_01_02", dayId = "day_london_01", stepIndex = 2, title = "CHECK-IN WINDOW", timeText = null, budgetText = null, maxPeople = 2),
            ItineraryStepEntity(stepId = "step_london_02_01", dayId = "day_london_02", stepIndex = 1, title = "TATE MODERN", timeText = "13:00", budgetText = "£0", maxPeople = 4),
            ItineraryStepEntity(stepId = "step_london_02_02", dayId = "day_london_02", stepIndex = 2, title = "NIGHT WALK", timeText = "21:10", budgetText = null, maxPeople = null),
            ItineraryStepEntity(stepId = "step_berlin_01_01", dayId = "day_berlin_01", stepIndex = 1, title = "EAST SIDE GALLERY", timeText = "12:00", budgetText = null, maxPeople = 6),
            ItineraryStepEntity(stepId = "step_berlin_01_02", dayId = "day_berlin_01", stepIndex = 2, title = "BRANDENBURG GATE", timeText = "15:30", budgetText = null, maxPeople = null),
            ItineraryStepEntity(stepId = "step_berlin_02_01", dayId = "day_berlin_02", stepIndex = 1, title = "U-BAHN LOOP", timeText = "09:20", budgetText = "€9.90", maxPeople = 2),
            ItineraryStepEntity(stepId = "step_berlin_02_02", dayId = "day_berlin_02", stepIndex = 2, title = "DINNER CELL", timeText = "20:00", budgetText = "≈ €25", maxPeople = null),
            ItineraryStepEntity(stepId = "step_seoul_01_01", dayId = "day_seoul_01", stepIndex = 1, title = "HOTEL DROP", timeText = "11:40", budgetText = null, maxPeople = null),
            ItineraryStepEntity(stepId = "step_seoul_01_02", dayId = "day_seoul_01", stepIndex = 2, title = "PALACE SLOT", timeText = "15:00", budgetText = "₩3,000", maxPeople = 4),
            ItineraryStepEntity(stepId = "step_seoul_02_01", dayId = "day_seoul_02", stepIndex = 1, title = "MARKET RUN", timeText = "19:00", budgetText = null, maxPeople = null),
            ItineraryStepEntity(stepId = "step_seoul_02_02", dayId = "day_seoul_02", stepIndex = 2, title = "ROOFTOP VIEW", timeText = null, budgetText = null, maxPeople = 10)
        )
        database.usersDao().insertAll(users)
        database.tripsDao().insertTrips(trips)
        database.tripsDao().insertAssignments(assignments)
        database.tripsDao().insertDays(itineraryDays)
        database.tripsDao().insertSteps(itinerarySteps)
        database.threadsDao().insertAll(threads = buildThreadsForTrips(trips = trips))
        database.messagesDao().insertAll(messages = buildHanoiThreadMessages())
    }
}

