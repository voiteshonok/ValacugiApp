package by.voiteshonok.valacugi.data.repositories

import by.voiteshonok.valacugi.data.room.LastReadMessageEntity
import by.voiteshonok.valacugi.data.room.LastReadMessagesDao
import by.voiteshonok.valacugi.data.room.MessageEntity
import by.voiteshonok.valacugi.data.room.MessagesDao
import by.voiteshonok.valacugi.data.room.ThreadEntity
import by.voiteshonok.valacugi.data.room.ThreadsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RoomMessagesRepositoryTest {
    @Test
    fun markThreadAsRead_upsertsLatestMessageIdAndSentAt() = runTest {
        val inputThreadId: String = "thread_hanoi"
        val inputUserId: String = "user_user"
        val fakeMessagesDao: FakeMessagesDao = FakeMessagesDao(
            latestMessage = MessageEntity(
                messageId = "message_hanoi_05",
                threadId = inputThreadId,
                senderId = "user_admin",
                body = "Copy.",
                sentAt = "2026-07-14T19:04:00"
            )
        )
        val fakeLastReadMessagesDao: FakeLastReadMessagesDao = FakeLastReadMessagesDao()
        val repository: RoomMessagesRepository = RoomMessagesRepository(
            messagesDao = fakeMessagesDao,
            threadsDao = FakeThreadsDao(),
            lastReadMessagesDao = fakeLastReadMessagesDao
        )
        repository.markThreadAsRead(threadId = inputThreadId, userId = inputUserId)
        val actualLastRead: LastReadMessageEntity = fakeLastReadMessagesDao.upsertedLastRead
            ?: error("Expected upsert to be called")
        assertEquals(inputThreadId, actualLastRead.threadId)
        assertEquals(inputUserId, actualLastRead.userId)
        assertEquals("message_hanoi_05", actualLastRead.messageId)
        assertEquals("2026-07-14T19:04:00", actualLastRead.seenAt)
    }

    @Test
    fun markThreadAsRead_whenNoMessages_doesNotUpsert() = runTest {
        val fakeLastReadMessagesDao: FakeLastReadMessagesDao = FakeLastReadMessagesDao()
        val repository: RoomMessagesRepository = RoomMessagesRepository(
            messagesDao = FakeMessagesDao(latestMessage = null),
            threadsDao = FakeThreadsDao(),
            lastReadMessagesDao = fakeLastReadMessagesDao
        )
        repository.markThreadAsRead(threadId = "thread_empty", userId = "user_user")
        assertNull(fakeLastReadMessagesDao.upsertedLastRead)
    }
}

private class FakeMessagesDao(
    private val latestMessage: MessageEntity?
) : MessagesDao {
    override fun observeMessages(threadId: String): Flow<List<MessageEntity>> {
        return flowOf(emptyList())
    }

    override suspend fun getMessageCountForThread(threadId: String): Int {
        return 0
    }

    override suspend fun insertAll(messages: List<MessageEntity>) {
    }

    override suspend fun insertMessage(message: MessageEntity) {
    }

    override suspend fun getLatestMessageForThread(threadId: String): MessageEntity? {
        return latestMessage
    }
}

private class FakeLastReadMessagesDao : LastReadMessagesDao {
    var upsertedLastRead: LastReadMessageEntity? = null

    override suspend fun upsert(lastRead: LastReadMessageEntity) {
        upsertedLastRead = lastRead
    }

    override suspend fun getLastReadCountForThread(threadId: String): Int {
        return 0
    }

    override suspend fun insertAll(lastReadMessages: List<LastReadMessageEntity>) {
    }

    override suspend fun getLastReadForUser(threadId: String, userId: String): LastReadMessageEntity? {
        return upsertedLastRead?.takeIf { lastRead ->
            lastRead.threadId == threadId && lastRead.userId == userId
        }
    }
}

private class FakeThreadsDao : ThreadsDao {
    override fun observeThreads(): Flow<List<ThreadEntity>> {
        return flowOf(emptyList())
    }

    override fun observeThreadsForUser(userId: String): Flow<List<by.voiteshonok.valacugi.data.room.ThreadWithUnreadEntity>> {
        return flowOf(emptyList())
    }

    override fun observeThread(threadId: String): Flow<ThreadEntity?> {
        return flowOf(null)
    }

    override suspend fun getThreadsCount(): Int {
        return 0
    }

    override suspend fun insertAll(threads: List<ThreadEntity>) {
    }

    override suspend fun updateLastMessage(threadId: String, preview: String, sentAt: String) {
    }
}
