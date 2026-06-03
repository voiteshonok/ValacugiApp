package by.voiteshonok.valacugi.domain

data class User(
    val id: String,
    val login: String,
    val displayName: String,
    val isPushNotificationsEnabled: Boolean
)

data class Trip(
    val id: String,
    val title: String,
    val dateStart: String?,
    val dateEnd: String?,
    val pax: Int?,
    val budgetText: String?,
    val createdById: String,
    val assignedCount: Int?
)

data class ItineraryDay(
    val id: String,
    val dayIndex: Int,
    val title: String
)

data class ItineraryStep(
    val id: String,
    val stepIndex: Int,
    val title: String,
    val timeText: String?,
    val budgetText: String?,
    val maxPeople: Int?
)

data class MessageThread(
    val id: String,
    val tripId: String,
    val title: String,
    val lastMessagePreview: String,
    val lastMessageAt: String,
    val hasUnread: Boolean
)

data class Message(
    val id: String,
    val threadId: String,
    val senderId: String,
    val body: String,
    val sentAt: String
)

data class TripItinerary(
    val trip: Trip,
    val days: List<ItineraryDayWithSteps>
)

data class ItineraryDayWithSteps(
    val day: ItineraryDay,
    val steps: List<ItineraryStep>
)

