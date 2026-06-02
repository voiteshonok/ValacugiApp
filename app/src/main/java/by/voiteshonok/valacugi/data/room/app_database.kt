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
        ItineraryDayEntity::class,
        ItineraryStepEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun tripsDao(): TripsDao

    companion object {
        fun create(context: Context): AppDatabase {
            val database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, DatabaseName)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
            CoroutineScope(Dispatchers.IO).launch {
                val tripsCount: Int = database.tripsDao().getTripsCount()
                if (tripsCount > 0) return@launch
                ValacugiSeeder.seed(database = database)
            }
            return database
        }
    }
}

private object ValacugiSeeder {
    suspend fun seed(database: AppDatabase) {
        val users: List<UserEntity> = listOf(
            UserEntity(userId = "user_admin", login = "admin", displayName = "Administrator"),
            UserEntity(userId = "user_operator", login = "operator", displayName = "Operator")
        )
        val tokyoTripId: String = "trip_tokyo"
        val londonTripId: String = "trip_london"
        val trips: List<TripEntity> = listOf(
            TripEntity(
                tripId = londonTripId,
                title = "LONDON",
                dateStart = null,
                dateEnd = null,
                pax = 2,
                budgetText = "£2400",
                assignedCount = 2,
                assignedTotal = 5
            ),
            TripEntity(
                tripId = tokyoTripId,
                title = "TOKYO",
                dateStart = "2026-08-24",
                dateEnd = "2026-08-30",
                pax = 4,
                budgetText = "¥500k",
                assignedCount = 4,
                assignedTotal = null
            )
        )
        val tripId: String = tokyoTripId
        val day1: ItineraryDayEntity = ItineraryDayEntity(dayId = "day_001", tripId = tripId, dayIndex = 1, title = "DAY 01 — ARRIVAL")
        val day2: ItineraryDayEntity = ItineraryDayEntity(dayId = "day_002", tripId = tripId, dayIndex = 2, title = "DAY 02 — CORE LOOP")
        val day3: ItineraryDayEntity = ItineraryDayEntity(dayId = "day_003", tripId = tripId, dayIndex = 3, title = "DAY 03 — DEPARTURE")
        val steps: List<ItineraryStepEntity> = listOf(
            ItineraryStepEntity(stepId = "step_001", dayId = day1.dayId, stepIndex = 1, title = "TRANSFER TO CITY", timeText = "09:30", budgetText = null, maxPeople = null),
            ItineraryStepEntity(stepId = "step_002", dayId = day1.dayId, stepIndex = 2, title = "CHECK-IN", timeText = "11:00", budgetText = "≈ ¥18,000", maxPeople = 2),
            ItineraryStepEntity(stepId = "step_003", dayId = day2.dayId, stepIndex = 1, title = "MUSEUM WINDOW", timeText = null, budgetText = null, maxPeople = 4),
            ItineraryStepEntity(stepId = "step_004", dayId = day2.dayId, stepIndex = 2, title = "DINNER SLOT", timeText = "19:00", budgetText = null, maxPeople = null),
            ItineraryStepEntity(stepId = "step_005", dayId = day3.dayId, stepIndex = 1, title = "CHECK-OUT", timeText = "08:00", budgetText = null, maxPeople = 2),
            ItineraryStepEntity(stepId = "step_006", dayId = day3.dayId, stepIndex = 2, title = "AIRPORT TRANSIT", timeText = "10:30", budgetText = "≈ ¥3,200", maxPeople = null)
        )
        database.usersDao().insertAll(users)
        database.tripsDao().insertTrips(trips)
        database.tripsDao().insertDays(listOf(day1, day2, day3))
        database.tripsDao().insertSteps(steps)
    }
}

