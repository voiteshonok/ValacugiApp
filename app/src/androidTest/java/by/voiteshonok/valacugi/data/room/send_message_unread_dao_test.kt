package by.voiteshonok.valacugi.data.room

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import by.voiteshonok.valacugi.data.repositories.RoomMessagesRepository
import by.voiteshonok.valacugi.data.repositories.RoomThreadsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SendMessageUnreadDaoTest {
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
    fun sendMessage_fromRegularUser_marksHanoiUnreadForAdmin() = runBlocking {
        val messagesRepository = RoomMessagesRepository(
            messagesDao = database.messagesDao(),
            threadsDao = database.threadsDao(),
            lastReadMessagesDao = database.lastReadMessagesDao()
        )
        messagesRepository.sendMessage(
            threadId = HanoiThreadId,
            senderId = RegularUserId,
            body = "New update from the field."
        )
        val threadsRepository = RoomThreadsRepository(threadsDao = database.threadsDao())
        val hanoiThread = threadsRepository.observeThreadsForUser(userId = AdminUserId).first()
            .first { thread -> thread.id == HanoiThreadId }
        assertTrue(hanoiThread.hasUnread)
    }
}
