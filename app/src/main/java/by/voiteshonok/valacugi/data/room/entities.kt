package by.voiteshonok.valacugi.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.Embedded

@Entity(
    tableName = "users"
)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "login")
    val login: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "push_notifications_enabled", defaultValue = "1")
    val pushNotificationsEnabled: Boolean = true
)

@Entity(
    tableName = "trips"
)
data class TripEntity(
    @PrimaryKey
    @ColumnInfo(name = "trip_id")
    val tripId: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "date_start")
    val dateStart: String?,
    @ColumnInfo(name = "date_end")
    val dateEnd: String?,
    @ColumnInfo(name = "pax")
    val pax: Int?,
    @ColumnInfo(name = "budget_text")
    val budgetText: String?,
    @ColumnInfo(name = "created_by_id")
    val createdById: String
)

@Entity(
    tableName = "threads",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["trip_id"],
            childColumns = ["trip_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["trip_id"], unique = true)
    ]
)
data class ThreadEntity(
    @PrimaryKey
    @ColumnInfo(name = "thread_id")
    val threadId: String,
    @ColumnInfo(name = "trip_id")
    val tripId: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "last_message_preview")
    val lastMessagePreview: String,
    @ColumnInfo(name = "last_message_at")
    val lastMessageAt: String
)

data class ThreadWithUnreadEntity(
    @Embedded
    val thread: ThreadEntity,
    @ColumnInfo(name = "has_unread")
    val hasUnread: Boolean
)

@Entity(
    tableName = "last_read_messages",
    primaryKeys = ["thread_id", "user_id"],
    foreignKeys = [
        ForeignKey(
            entity = ThreadEntity::class,
            parentColumns = ["thread_id"],
            childColumns = ["thread_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["message_id"],
            childColumns = ["message_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["thread_id"]),
        Index(value = ["user_id"]),
        Index(value = ["message_id"])
    ]
)
data class LastReadMessageEntity(
    @ColumnInfo(name = "thread_id")
    val threadId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "message_id")
    val messageId: String,
    @ColumnInfo(name = "seen_at")
    val seenAt: String
)

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ThreadEntity::class,
            parentColumns = ["thread_id"],
            childColumns = ["thread_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["sender_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["thread_id"]),
        Index(value = ["sender_id"])
    ]
)
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "message_id")
    val messageId: String,
    @ColumnInfo(name = "thread_id")
    val threadId: String,
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    @ColumnInfo(name = "body")
    val body: String,
    @ColumnInfo(name = "sent_at")
    val sentAt: String
)

@Entity(
    tableName = "trip_assignments",
    primaryKeys = ["trip_id", "person_id"],
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["trip_id"],
            childColumns = ["trip_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["person_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["trip_id"]),
        Index(value = ["person_id"])
    ]
)
data class TripAssignmentEntity(
    @ColumnInfo(name = "trip_id")
    val tripId: String,
    @ColumnInfo(name = "person_id")
    val personId: String
)

data class TripWithAssignedCountEntity(
    @Embedded
    val trip: TripEntity,
    @ColumnInfo(name = "assigned_count")
    val assignedCount: Int
)

@Entity(
    tableName = "itinerary_days",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["trip_id"],
            childColumns = ["trip_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["trip_id"])
    ]
)
data class ItineraryDayEntity(
    @PrimaryKey
    @ColumnInfo(name = "day_id")
    val dayId: String,
    @ColumnInfo(name = "trip_id")
    val tripId: String,
    @ColumnInfo(name = "day_index")
    val dayIndex: Int,
    @ColumnInfo(name = "title")
    val title: String
)

@Entity(
    tableName = "itinerary_steps",
    foreignKeys = [
        ForeignKey(
            entity = ItineraryDayEntity::class,
            parentColumns = ["day_id"],
            childColumns = ["day_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["day_id"])
    ]
)
data class ItineraryStepEntity(
    @PrimaryKey
    @ColumnInfo(name = "step_id")
    val stepId: String,
    @ColumnInfo(name = "day_id")
    val dayId: String,
    @ColumnInfo(name = "step_index")
    val stepIndex: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "time_text")
    val timeText: String?,
    @ColumnInfo(name = "budget_text")
    val budgetText: String?,
    @ColumnInfo(name = "max_people")
    val maxPeople: Int?
)

data class ItineraryDayWithStepsEntity(
    @Embedded
    val day: ItineraryDayEntity,
    @Relation(
        parentColumn = "day_id",
        entityColumn = "day_id"
    )
    val steps: List<ItineraryStepEntity>
)

