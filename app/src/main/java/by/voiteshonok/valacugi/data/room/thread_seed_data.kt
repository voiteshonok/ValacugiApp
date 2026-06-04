package by.voiteshonok.valacugi.data.room

internal data class ThreadSeedData(
    val title: String,
    val lastMessagePreview: String,
    val lastMessageAt: String
)

internal fun threadIdForTrip(tripId: String): String {
    val tripSlug: String = tripId.removePrefix(prefix = "trip_")
    return "thread_$tripSlug"
}

internal fun buildThreadEntity(trip: TripEntity): ThreadEntity {
    val seedData: ThreadSeedData = threadSeedByTripId[trip.tripId] ?: ThreadSeedData(
        title = "${trip.title} TRIP",
        lastMessagePreview = "Awaiting first transmission...",
        lastMessageAt = "2026-01-01T09:00:00"
    )
    return ThreadEntity(
        threadId = threadIdForTrip(tripId = trip.tripId),
        tripId = trip.tripId,
        title = seedData.title,
        lastMessagePreview = seedData.lastMessagePreview,
        lastMessageAt = seedData.lastMessageAt
    )
}

internal fun buildThreadsForTrips(trips: List<TripEntity>): List<ThreadEntity> {
    return trips.map { trip: TripEntity -> buildThreadEntity(trip = trip) }
}

private val threadSeedByTripId: Map<String, ThreadSeedData> = mapOf(
    "trip_london" to ThreadSeedData(
        title = "LONDON EXPEDITION",
        lastMessagePreview = "Hotel booked at The Savoy.",
        lastMessageAt = "2026-06-03T09:15:00"
    ),
    "trip_tokyo" to ThreadSeedData(
        title = "TOKYO TRIP",
        lastMessagePreview = "Flight NH7 confirms departure...",
        lastMessageAt = "2026-08-24T14:02:00"
    ),
    "trip_berlin" to ThreadSeedData(
        title = "BERLIN LOGISTICS",
        lastMessagePreview = "Transfer complete. See attachment.",
        lastMessageAt = "2026-09-02T11:40:00"
    ),
    "trip_oslo" to ThreadSeedData(
        title = "OSLO TRIP",
        lastMessagePreview = "Weather window closing Friday.",
        lastMessageAt = "2026-07-01T16:20:00"
    ),
    "trip_lisbon" to ThreadSeedData(
        title = "LISBON TRIP",
        lastMessagePreview = "Tile museum tickets confirmed.",
        lastMessageAt = "2026-06-18T08:05:00"
    ),
    "trip_reykjavik" to ThreadSeedData(
        title = "REYKJAVIK EXPEDITION",
        lastMessagePreview = "Aurora alert active tonight.",
        lastMessageAt = "2026-11-02T22:30:00"
    ),
    "trip_seoul" to ThreadSeedData(
        title = "SEOUL TRIP",
        lastMessagePreview = "Palace slot locked for 15:00.",
        lastMessageAt = "2026-10-10T13:12:00"
    ),
    "trip_hanoi" to ThreadSeedData(
        title = "HANOI TRIP",
        lastMessagePreview = "Street food tour meets at 19:00.",
        lastMessageAt = "2026-07-14T19:00:00"
    ),
    "trip_nairobi" to ThreadSeedData(
        title = "NAIROBI PRODUCTION",
        lastMessagePreview = "Equipment list updated for tomorrow.",
        lastMessageAt = "2026-06-02T07:45:00"
    ),
    "trip_istanbul" to ThreadSeedData(
        title = "ISTANBUL TRIP",
        lastMessagePreview = "Bosphorus ferry departs 10:00.",
        lastMessageAt = "2026-05-28T10:00:00"
    ),
    "trip_mexico_city" to ThreadSeedData(
        title = "MEXICO CITY TRIP",
        lastMessagePreview = "Host confirmed rooftop check-in.",
        lastMessageAt = "2026-09-21T18:22:00"
    ),
    "trip_singapore" to ThreadSeedData(
        title = "SINGAPORE TRIP",
        lastMessagePreview = "Changi transfer synced to roster.",
        lastMessageAt = "2026-08-03T06:55:00"
    )
)
