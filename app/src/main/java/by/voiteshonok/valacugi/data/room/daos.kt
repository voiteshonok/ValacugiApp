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

    @Query(
        """
        SELECT * FROM users
        WHERE LOWER(login) = LOWER(:login) AND password = :password
        LIMIT 1
        """
    )
    suspend fun findByCredentials(login: String, password: String): UserEntity?

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

    @Query("SELECT COUNT(*) FROM trips")
    suspend fun getTripsCount(): Int

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(days: List<ItineraryDayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<ItineraryStepEntity>)
}

