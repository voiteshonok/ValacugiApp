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

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)
}

@Dao
interface TripsDao {
    @Query("SELECT * FROM trips ORDER BY title ASC")
    fun observeTrips(): Flow<List<TripEntity>>

    @Query("SELECT COUNT(*) FROM trips")
    suspend fun getTripsCount(): Int

    @Query("SELECT * FROM trips WHERE trip_id = :tripId LIMIT 1")
    fun observeTrip(tripId: String): Flow<TripEntity?>

    @Query("SELECT * FROM itinerary_days WHERE trip_id = :tripId ORDER BY day_index ASC")
    fun observeDays(tripId: String): Flow<List<ItineraryDayEntity>>

    @androidx.room.Transaction
    @Query("SELECT * FROM itinerary_days WHERE trip_id = :tripId ORDER BY day_index ASC")
    fun observeItineraryDays(tripId: String): Flow<List<ItineraryDayWithStepsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(days: List<ItineraryDayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<ItineraryStepEntity>)
}

