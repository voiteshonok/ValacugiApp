package by.voiteshonok.valacugi.data

import by.voiteshonok.valacugi.core.trip_creation.TripCreationDraft
import by.voiteshonok.valacugi.core.trip_creation.TripStepDraft
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
    fun parseDateRange_whenPlaceholder_returnsNullDates() {
        val actualRange: Pair<String?, String?> = parseDateRange(dateRangeText = "[ ? ] - [ ? ]")
        assertEquals(null, actualRange.first)
        assertEquals(null, actualRange.second)
    }
}
