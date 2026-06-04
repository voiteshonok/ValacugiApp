package by.voiteshonok.valacugi.ui.chat

import by.voiteshonok.valacugi.core.session.SessionRepository
import by.voiteshonok.valacugi.core.session.UserSession
import by.voiteshonok.valacugi.domain.Message
import by.voiteshonok.valacugi.domain.MessagesRepository
import by.voiteshonok.valacugi.domain.MessageThread
import by.voiteshonok.valacugi.domain.ThreadsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelReadTest {
    private val testDispatcher = StandardTestDispatcher()
    private val inputThreadId: String = "thread_hanoi"
    private val inputUserId: String = "user_user"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun observeChat_whenMessagesLoaded_marksThreadAsReadForCurrentUser() = runTest(testDispatcher) {
        val fakeMessagesRepository: FakeMessagesRepository = FakeMessagesRepository(
            messages = listOf(
                Message(
                    id = "message_hanoi_01",
                    threadId = inputThreadId,
                    senderId = "user_admin",
                    body = "Meet at 18:00?",
                    sentAt = "2026-07-14T17:40:00"
                )
            )
        )
        ChatViewModel(
            threadId = inputThreadId,
            threadsRepository = FakeThreadsRepository(
                thread = MessageThread(
                    id = inputThreadId,
                    tripId = "trip_hanoi",
                    title = "HANOI TRIP",
                    lastMessagePreview = "Preview",
                    lastMessageAt = "2026-07-14T19:00:00",
                    hasUnread = true
                )
            ),
            messagesRepository = fakeMessagesRepository,
            sessionRepository = FakeSessionRepository(
                initialSession = UserSession(identification = inputUserId)
            )
        )
        advanceUntilIdle()
        assertEquals(inputThreadId to inputUserId, fakeMessagesRepository.lastMarkAsReadCall)
    }

    @Test
    fun observeChat_whenNoSession_doesNotMarkThreadAsRead() = runTest(testDispatcher) {
        val fakeMessagesRepository: FakeMessagesRepository = FakeMessagesRepository(
            messages = listOf(
                Message(
                    id = "message_hanoi_01",
                    threadId = inputThreadId,
                    senderId = "user_admin",
                    body = "Meet at 18:00?",
                    sentAt = "2026-07-14T17:40:00"
                )
            )
        )
        ChatViewModel(
            threadId = inputThreadId,
            threadsRepository = FakeThreadsRepository(thread = null),
            messagesRepository = fakeMessagesRepository,
            sessionRepository = FakeSessionRepository(initialSession = null)
        )
        advanceUntilIdle()
        assertEquals(null, fakeMessagesRepository.lastMarkAsReadCall)
    }
}

private class FakeSessionRepository(
    initialSession: UserSession?
) : SessionRepository {
    private val sessionFlow: MutableStateFlow<UserSession?> = MutableStateFlow(initialSession)

    override fun observeSession(): Flow<UserSession?> = sessionFlow

    override suspend fun saveSession(session: UserSession) {
        sessionFlow.value = session
    }

    override suspend fun clearSession() {
        sessionFlow.value = null
    }
}

private class FakeThreadsRepository(
    private val thread: MessageThread?
) : ThreadsRepository {
    override fun observeThreads(): Flow<List<MessageThread>> = flowOf(emptyList())

    override fun observeThreadsForUser(userId: String): Flow<List<MessageThread>> = flowOf(emptyList())

    override fun observeThread(threadId: String): Flow<MessageThread?> {
        return flowOf(thread?.takeIf { storedThread -> storedThread.id == threadId })
    }
}

private class FakeMessagesRepository(
    private val messages: List<Message>
) : MessagesRepository {
    var lastMarkAsReadCall: Pair<String, String>? = null

    override fun observeMessages(threadId: String): Flow<List<Message>> {
        return flowOf(messages.filter { message -> message.threadId == threadId })
    }

    override suspend fun sendMessage(threadId: String, senderId: String, body: String) {
    }

    override suspend fun markThreadAsRead(threadId: String, userId: String) {
        lastMarkAsReadCall = threadId to userId
    }
}
