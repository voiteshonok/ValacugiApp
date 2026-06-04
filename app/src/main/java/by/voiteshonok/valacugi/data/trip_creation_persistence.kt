package by.voiteshonok.valacugi.data

import by.voiteshonok.valacugi.core.trip_creation.TripCreationDraft
import by.voiteshonok.valacugi.core.trip_creation.TripStepDraft
import by.voiteshonok.valacugi.data.room.ItineraryDayEntity
import by.voiteshonok.valacugi.data.room.ItineraryStepEntity
import by.voiteshonok.valacugi.data.room.ThreadEntity
import by.voiteshonok.valacugi.data.room.TripAssignmentEntity
import by.voiteshonok.valacugi.data.room.TripEntity
import by.voiteshonok.valacugi.data.room.createSentAtIsoTimestamp
import java.util.UUID

data class PersistedTripBundle(
    val trip: TripEntity,
    val day: ItineraryDayEntity,
    val steps: List<ItineraryStepEntity>,
    val thread: ThreadEntity,
    val assignments: List<TripAssignmentEntity>
)

fun mapDraftToPersistedTripBundle(
    draft: TripCreationDraft,
    steps: List<TripStepDraft>,
    createdByUserId: String
): PersistedTripBundle {
    val tripId: String = createTripIdFromLocation(location = draft.location)
    val tripTitle: String = resolveTripTitle(location = draft.location)
    val dateRange: Pair<String?, String?> = parseDateRange(dateRangeText = draft.dateRangeText)
    val tripEntity: TripEntity = TripEntity(
        tripId = tripId,
        title = tripTitle,
        dateStart = dateRange.first,
        dateEnd = dateRange.second,
        pax = parsePax(paxText = draft.pax),
        budgetText = draft.budget.trim().ifEmpty { null },
        createdById = createdByUserId
    )
    val dayId: String = "day_${tripId}_01"
    val dayEntity: ItineraryDayEntity = ItineraryDayEntity(
        dayId = dayId,
        tripId = tripId,
        dayIndex = 1,
        title = "DAY 01 — ACTIVE SEQUENCE"
    )
    val stepEntities: List<ItineraryStepEntity> = steps.map { step: TripStepDraft ->
        ItineraryStepEntity(
            stepId = "step_${tripId}_${step.index}",
            dayId = dayId,
            stepIndex = step.index,
            title = step.title,
            timeText = step.timeOffset,
            budgetText = "${step.coordinates} · ${step.actionType}",
            maxPeople = null
        )
    }
    val threadEntity: ThreadEntity = ThreadEntity(
        threadId = threadIdForTrip(tripId = tripId),
        tripId = tripId,
        title = "$tripTitle TRIP",
        lastMessagePreview = "Awaiting first transmission...",
        lastMessageAt = createSentAtIsoTimestamp()
    )
    val assignments: List<TripAssignmentEntity> = listOf(
        TripAssignmentEntity(tripId = tripId, personId = createdByUserId)
    )
    return PersistedTripBundle(
        trip = tripEntity,
        day = dayEntity,
        steps = stepEntities,
        thread = threadEntity,
        assignments = assignments
    )
}

fun createTripIdFromLocation(location: String): String {
    val slug: String = location
        .trim()
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')
        .take(n = 24)
        .ifEmpty { "expedition" }
    val suffix: String = UUID.randomUUID().toString().replace(oldValue = "-", newValue = "").take(n = 4)
    return "trip_${slug}_$suffix"
}

fun threadIdForTrip(tripId: String): String {
    val tripSlug: String = tripId.removePrefix(prefix = "trip_")
    return "thread_$tripSlug"
}

fun resolveTripTitle(location: String): String {
    val trimmedLocation: String = location.trim()
    if (trimmedLocation.isEmpty()) {
        return "EXPEDITION"
    }
    return trimmedLocation.uppercase()
}

fun parseDateRange(dateRangeText: String): Pair<String?, String?> {
    val rangeSeparator: String = " - "
    val separatorIndex: Int = dateRangeText.indexOf(string = rangeSeparator)
    if (separatorIndex < 0) {
        return null to null
    }
    val startToken: String = dateRangeText.substring(startIndex = 0, endIndex = separatorIndex)
    val endToken: String = dateRangeText.substring(startIndex = separatorIndex + rangeSeparator.length)
    val dateStart: String? = parseDateToken(token = startToken)
    val dateEnd: String? = parseDateToken(token = endToken)
    return dateStart to dateEnd
}

private fun parseDateToken(token: String): String? {
    if (token.contains('?')) {
        return null
    }
    val trimmedToken: String = token.trim()
    if (trimmedToken.isEmpty()) {
        return null
    }
    return trimmedToken
}

fun parsePax(paxText: String): Int? {
    val trimmedPax: String = paxText.trim()
    if (trimmedPax.isEmpty() || trimmedPax.contains('?')) {
        return null
    }
    return trimmedPax.toIntOrNull()
}
