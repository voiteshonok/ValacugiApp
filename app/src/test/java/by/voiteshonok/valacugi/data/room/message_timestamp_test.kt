package by.voiteshonok.valacugi.data.room

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MessageTimestampTest {
    @Test
    fun resolveSentAtForNewMessage_whenNowIsBeforeLatest_usesOneSecondAfterLatest() {
        val latestSentAt: String = "2026-07-14T19:04:00"
        val actualSentAt: String = resolveSentAtForNewMessage(latestSentAtInThread = latestSentAt)
        assertEquals("2026-07-14T19:04:01", actualSentAt)
        assertTrue(actualSentAt > latestSentAt)
    }

    @Test
    fun incrementIsoDateTimeByOneSecond_advancesTimestamp() {
        val inputIsoDateTime: String = "2026-07-14T18:05:00"
        val actualSentAt: String = incrementIsoDateTimeByOneSecond(isoDateTime = inputIsoDateTime)
        assertEquals("2026-07-14T18:05:01", actualSentAt)
    }

    @Test
    fun resolveSentAtForNewMessage_whenThreadIsEmpty_returnsNowTimestamp() {
        val actualSentAt: String = resolveSentAtForNewMessage(latestSentAtInThread = null)
        assertTrue(actualSentAt.matches(Regex("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}""")))
    }
}
