package by.voiteshonok.valacugi.data.room

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import by.voiteshonok.valacugi.data.repositories.RoomMessagesRepository
import by.voiteshonok.valacugi.data.repositories.RoomThreadsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LastReadUnreadDaoTest {
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        database = createInMemoryTestDatabase(context = context)
        runBlocking {
            seedHanoiUnreadScenario(database = database)
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeThreadsForUser_regularUser_hanoiThreadIsUnread() = runBlocking {
        val threadsRepository = RoomThreadsRepository(threadsDao = database.threadsDao())
        val actualThreads = threadsRepository.observeThreadsForUser(userId = RegularUserId).first()
        val hanoiThread = actualThreads.first { thread -> thread.id == HanoiThreadId }
        assertTrue(hanoiThread.hasUnread)
    }

    @Test
    fun observeThreadsForUser_adminUser_hanoiThreadIsRead() = runBlocking {
        val threadsRepository = RoomThreadsRepository(threadsDao = database.threadsDao())
        val actualThreads = threadsRepository.observeThreadsForUser(userId = AdminUserId).first()
        val hanoiThread = actualThreads.first { thread -> thread.id == HanoiThreadId }
        assertFalse(hanoiThread.hasUnread)
    }

    @Test
    fun observeThreadsForUser_threadWithoutMessages_isNotUnread() = runBlocking {
        seedThreadWithoutMessages(database = database, userId = RegularUserId)
        val threadsRepository = RoomThreadsRepository(threadsDao = database.threadsDao())
        val actualThreads = threadsRepository.observeThreadsForUser(userId = RegularUserId).first()
        val londonThread = actualThreads.first { thread -> thread.id == "thread_london" }
        assertFalse(londonThread.hasUnread)
    }

    @Test
    fun markThreadAsRead_regularUser_upsertsLatestMessageAndClearsUnread() = runBlocking {
        val messagesRepository = RoomMessagesRepository(
            messagesDao = database.messagesDao(),
            threadsDao = database.threadsDao(),
            lastReadMessagesDao = database.lastReadMessagesDao()
        )
        messagesRepository.markThreadAsRead(threadId = HanoiThreadId, userId = RegularUserId)
        val actualLastRead: LastReadMessageEntity = database.lastReadMessagesDao()
            .getLastReadForUser(threadId = HanoiThreadId, userId = RegularUserId)
            ?: error("Expected last read row after markThreadAsRead")
        assertEquals("message_hanoi_05", actualLastRead.messageId)
        assertEquals("2026-07-14T19:04:00", actualLastRead.seenAt)
        val threadsRepository = RoomThreadsRepository(threadsDao = database.threadsDao())
        val hanoiThread = threadsRepository.observeThreadsForUser(userId = RegularUserId).first()
            .first { thread -> thread.id == HanoiThreadId }
        assertFalse(hanoiThread.hasUnread)
    }

    @Test
    fun newMessageAfterLastRead_marksThreadUnreadAgain() = runBlocking {
        val messagesRepository = RoomMessagesRepository(
            messagesDao = database.messagesDao(),
            threadsDao = database.threadsDao(),
            lastReadMessagesDao = database.lastReadMessagesDao()
        )
        messagesRepository.markThreadAsRead(threadId = HanoiThreadId, userId = RegularUserId)
        messagesRepository.sendMessage(
            threadId = HanoiThreadId,
            senderId = RegularUserId,
            body = "Weather shift — confirm meet time."
        )
        val threadsRepository = RoomThreadsRepository(threadsDao = database.threadsDao())
        val hanoiThread = threadsRepository.observeThreadsForUser(userId = RegularUserId).first()
            .first { thread -> thread.id == HanoiThreadId }
        assertTrue(hanoiThread.hasUnread)
    }

    @Test
    fun hanoiSeedData_regularUserLastReadMatchesMessageThree() = runBlocking {
        val actualLastRead: LastReadMessageEntity? = database.lastReadMessagesDao()
            .getLastReadForUser(threadId = HanoiThreadId, userId = RegularUserId)
        assertNotNull(actualLastRead)
        assertEquals("message_hanoi_03", actualLastRead?.messageId)
        assertEquals("2026-07-14T18:05:00", actualLastRead?.seenAt)
    }

    @Test
    fun markThreadAsRead_whenThreadHasNoMessages_doesNotInsertLastRead() = runBlocking {
        seedThreadWithoutMessages(database = database, userId = RegularUserId)
        val emptyThreadId: String = "thread_london"
        val messagesRepository = RoomMessagesRepository(
            messagesDao = database.messagesDao(),
            threadsDao = database.threadsDao(),
            lastReadMessagesDao = database.lastReadMessagesDao()
        )
        messagesRepository.markThreadAsRead(threadId = emptyThreadId, userId = RegularUserId)
        val actualLastRead: LastReadMessageEntity? = database.lastReadMessagesDao()
            .getLastReadForUser(threadId = emptyThreadId, userId = RegularUserId)
        assertNull(actualLastRead)
    }
}
