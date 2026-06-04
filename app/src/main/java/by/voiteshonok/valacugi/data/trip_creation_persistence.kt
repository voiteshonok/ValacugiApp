package by.voiteshonok.valacugi.data

import by.voiteshonok.valacugi.core.trip_creation.TripCreationDraft
import by.voiteshonok.valacugi.core.trip_creation.TripStepDraft
import by.voiteshonok.valacugi.data.room.ItineraryDayEntity
import by.voiteshonok.valacugi.data.room.ItineraryStepEntity
import by.voiteshonok.valacugi.data.room.ThreadEntity
import by.voiteshonok.valacugi.data.room.TripAssignmentEntity
import by.voiteshonok.valacugi.data.room.TripEntity
import by.voiteshonok.valacugi.data.room.createSentAtIsoTimestamp
import by.voiteshonok.valacugi.domain.ItineraryStep
import by.voiteshonok.valacugi.domain.Trip
import by.voiteshonok.valacugi.domain.TripItinerary
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
    val tripId: String = draft.editingTripId ?: createTripIdFromLocation(location = draft.location)
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
            budgetText = formatStepBudgetText(coordinates = step.coordinates, actionType = step.actionType),
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

fun mapDraftToTripEntityForUpdate(
    draft: TripCreationDraft,
    createdByUserId: String
): TripEntity {
    val tripId: String = draft.editingTripId ?: error("editingTripId is required for update")
    val dateRange: Pair<String?, String?> = parseDateRange(dateRangeText = draft.dateRangeText)
    return TripEntity(
        tripId = tripId,
        title = resolveTripTitle(location = draft.location),
        dateStart = dateRange.first,
        dateEnd = dateRange.second,
        pax = parsePax(paxText = draft.pax),
        budgetText = draft.budget.trim().ifEmpty { null },
        createdById = createdByUserId
    )
}

fun mapDraftToItineraryEntities(
    tripId: String,
    steps: List<TripStepDraft>
): Pair<ItineraryDayEntity, List<ItineraryStepEntity>> {
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
            budgetText = formatStepBudgetText(coordinates = step.coordinates, actionType = step.actionType),
            maxPeople = null
        )
    }
    return dayEntity to stepEntities
}

fun mapTripItineraryToCreationDraft(itinerary: TripItinerary): TripCreationDraft {
    val trip: Trip = itinerary.trip
    return TripCreationDraft(
        expeditionId = trip.id.uppercase(),
        location = trip.title,
        dateRangeText = formatDateRangeForDraft(dateStart = trip.dateStart, dateEnd = trip.dateEnd),
        pax = formatPaxForDraft(pax = trip.pax),
        budget = trip.budgetText.orEmpty(),
        roster = "",
        editingTripId = trip.id
    )
}

fun mapItineraryStepsToDrafts(itinerary: TripItinerary): List<TripStepDraft> {
    return itinerary.days
        .flatMap { dayWithSteps -> dayWithSteps.steps }
        .sortedBy { step: ItineraryStep -> step.stepIndex }
        .map { step: ItineraryStep ->
            val parsedStepDetails: Pair<String, String> = parseStepBudgetText(budgetText = step.budgetText)
            TripStepDraft(
                index = step.stepIndex,
                title = step.title,
                coordinates = parsedStepDetails.first,
                timeOffset = step.timeText ?: "+00:00 HR",
                actionType = parsedStepDetails.second
            )
        }
}

fun formatDateRangeForDraft(dateStart: String?, dateEnd: String?): String {
    val startLabel: String = dateStart?.trim()?.takeIf { value: String -> value.isNotEmpty() } ?: "[ ? ]"
    val endLabel: String = dateEnd?.trim()?.takeIf { value: String -> value.isNotEmpty() } ?: "[ ? ]"
    return "$startLabel - $endLabel"
}

fun formatPaxForDraft(pax: Int?): String {
    if (pax == null) {
        return ""
    }
    return pax.toString()
}

fun formatStepBudgetText(coordinates: String, actionType: String): String {
    return "${coordinates.trim()} · ${actionType.trim()}"
}

fun parseStepBudgetText(budgetText: String?): Pair<String, String> {
    if (budgetText.isNullOrBlank()) {
        return "[ ? ]" to StepActionTypesDefault.first()
    }
    val parts: List<String> = budgetText.split(" · ").map { part: String -> part.trim() }
    if (parts.size < 2) {
        return budgetText to StepActionTypesDefault.first()
    }
    return parts.first() to parts.last()
}

private val StepActionTypesDefault: List<String> = listOf(
    "Transit",
    "Deployment",
    "Logistics",
    "Standby"
)

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
