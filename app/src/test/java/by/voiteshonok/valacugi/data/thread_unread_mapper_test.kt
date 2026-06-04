package by.voiteshonok.valacugi.data

import by.voiteshonok.valacugi.data.room.ThreadEntity
import by.voiteshonok.valacugi.data.room.ThreadWithUnreadEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThreadUnreadMapperTest {
    @Test
    fun threadWithUnreadEntity_toDomain_mapsUnreadFlag() {
        val inputEntity: ThreadWithUnreadEntity = ThreadWithUnreadEntity(
            thread = ThreadEntity(
                threadId = "thread_hanoi",
                tripId = "trip_hanoi",
                title = "HANOI TRIP",
                lastMessagePreview = "Preview",
                lastMessageAt = "2026-07-14T19:00:00"
            ),
            hasUnread = true
        )
        val actualThread = inputEntity.toDomain()
        assertTrue(actualThread.hasUnread)
        assertEquals("thread_hanoi", actualThread.id)
    }

    @Test
    fun threadEntity_toDomain_defaultsHasUnreadToFalse() {
        val inputEntity: ThreadEntity = ThreadEntity(
            threadId = "thread_london",
            tripId = "trip_london",
            title = "LONDON EXPEDITION",
            lastMessagePreview = "Preview",
            lastMessageAt = "2026-06-03T09:15:00"
        )
        val actualThread = inputEntity.toDomain()
        assertFalse(actualThread.hasUnread)
    }
}
