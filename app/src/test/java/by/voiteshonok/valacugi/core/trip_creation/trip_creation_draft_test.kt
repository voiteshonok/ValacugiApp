package by.voiteshonok.valacugi.core.trip_creation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TripCreationDraftTest {
    @Test
    fun generateExpeditionId_matchesExpectedPattern() {
        val actualId: String = generateExpeditionId()
        assertTrue(actualId.matches(Regex("""0X[A-F0-9]{6}\.A1""")))
    }

    @Test
    fun locationHeaderLabel_whenLocationEmpty_returnsPending() {
        val draft: TripCreationDraft = TripCreationDraft(
            expeditionId = "0XABC123.A1",
            location = "  ",
            dateRangeText = "",
            pax = "",
            budget = "",
            roster = ""
        )
        assertEquals("PENDING", draft.locationHeaderLabel)
    }

    @Test
    fun locationHeaderLabel_whenLocationSet_returnsUppercase() {
        val draft: TripCreationDraft = TripCreationDraft(
            expeditionId = "0XABC123.A1",
            location = "zermatt, ch",
            dateRangeText = "",
            pax = "",
            budget = "",
            roster = ""
        )
        assertEquals("ZERMATT, CH", draft.locationHeaderLabel)
    }
}
