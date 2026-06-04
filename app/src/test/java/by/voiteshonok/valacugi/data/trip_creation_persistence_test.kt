package by.voiteshonok.valacugi.data

import by.voiteshonok.valacugi.core.trip_creation.TripCreationDraft
import by.voiteshonok.valacugi.core.trip_creation.TripStepDraft
import by.voiteshonok.valacugi.domain.ItineraryDay
import by.voiteshonok.valacugi.domain.ItineraryDayWithSteps
import by.voiteshonok.valacugi.domain.ItineraryStep
import by.voiteshonok.valacugi.domain.Trip
import by.voiteshonok.valacugi.domain.TripItinerary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TripCreationPersistenceTest {
    @Test
    fun mapDraftToPersistedTripBundle_mapsTripStepsAndThread() {
        val draft: TripCreationDraft = TripCreationDraft(
            expeditionId = "0XABC123.A1",
            location = "Zermatt, CH",
            dateRangeText = "2026-08-01 - 2026-08-10",
            pax = "4",
            budget = "CHF 12k",
            roster = "Team Alpha"
        )
        val steps: List<TripStepDraft> = listOf(
            TripStepDraft(
                index = 1,
                title = "BASECAMP",
                coordinates = "46.0207° N",
                timeOffset = "+02:00 HR",
                actionType = "Transit"
            )
        )
        val bundle: PersistedTripBundle = mapDraftToPersistedTripBundle(
            draft = draft,
            steps = steps,
            createdByUserId = "user_admin"
        )
        assertEquals("ZERMATT, CH", bundle.trip.title)
        assertEquals("2026-08-01", bundle.trip.dateStart)
        assertEquals("2026-08-10", bundle.trip.dateEnd)
        assertEquals(4, bundle.trip.pax)
        assertEquals("CHF 12k", bundle.trip.budgetText)
        assertEquals("user_admin", bundle.trip.createdById)
        assertEquals(1, bundle.steps.size)
        assertEquals("BASECAMP", bundle.steps.first().title)
        assertTrue(bundle.trip.tripId.startsWith("trip_zermatt_ch_"))
        assertEquals("thread_${bundle.trip.tripId.removePrefix("trip_")}", bundle.thread.threadId)
        assertEquals(1, bundle.assignments.size)
    }

    @Test
    fun mapTripItineraryToCreationDraft_setsEditingTripId() {
        val itinerary: TripItinerary = TripItinerary(
            trip = Trip(
                id = "trip_zermatt_ab12",
                title = "ZERMATT, CH",
                dateStart = "2026-08-01",
                dateEnd = null,
                pax = 2,
                budgetText = "CHF 12k",
                createdById = "user_admin",
                assignedCount = 1
            ),
            days = emptyList()
        )
        val actualDraft: TripCreationDraft = mapTripItineraryToCreationDraft(itinerary = itinerary)
        assertEquals("trip_zermatt_ab12", actualDraft.editingTripId)
        assertEquals("ZERMATT, CH", actualDraft.location)
        assertEquals("2026-08-01 - [ ? ]", actualDraft.dateRangeText)
    }

    @Test
    fun mapItineraryStepsToDrafts_parsesStoredStepDetails() {
        val itinerary: TripItinerary = TripItinerary(
            trip = Trip(
                id = "trip_test",
                title = "TEST",
                dateStart = null,
                dateEnd = null,
                pax = null,
                budgetText = null,
                createdById = "user_admin",
                assignedCount = 0
            ),
            days = listOf(
                ItineraryDayWithSteps(
                    day = ItineraryDay(id = "day_1", dayIndex = 1, title = "DAY 01"),
                    steps = listOf(
                        ItineraryStep(
                            id = "step_1",
                            stepIndex = 1,
                            title = "BASECAMP",
                            timeText = "+01:00 HR",
                            budgetText = "46.0° N · Transit",
                            maxPeople = null
                        )
                    )
                )
            )
        )
        val actualSteps: List<TripStepDraft> = mapItineraryStepsToDrafts(itinerary = itinerary)
        assertEquals(1, actualSteps.size)
        assertEquals("BASECAMP", actualSteps.first().title)
        assertEquals("46.0° N", actualSteps.first().coordinates)
        assertEquals("Transit", actualSteps.first().actionType)
    }

    @Test
    fun parseDateRange_whenPlaceholder_returnsNullDates() {
        val actualRange: Pair<String?, String?> = parseDateRange(dateRangeText = "[ ? ] - [ ? ]")
        assertEquals(null, actualRange.first)
        assertEquals(null, actualRange.second)
    }
}
