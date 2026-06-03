package by.voiteshonok.valacugi.data.room

internal const val HanoiThreadId: String = "thread_hanoi"
internal const val AdminUserId: String = "user_admin"
internal const val RegularUserId: String = "user_user"

internal fun buildHanoiThreadMessages(): List<MessageEntity> {
    return listOf(
        MessageEntity(
            messageId = "message_hanoi_01",
            threadId = HanoiThreadId,
            senderId = AdminUserId,
            body = "Old Quarter meet at 18:00? Pho stall on Ta Hien confirmed.",
            sentAt = "2026-07-14T17:40:00"
        ),
        MessageEntity(
            messageId = "message_hanoi_02",
            threadId = HanoiThreadId,
            senderId = RegularUserId,
            body = "Confirmed. Adding to itinerary and syncing roster.",
            sentAt = "2026-07-14T17:52:00"
        ),
        MessageEntity(
            messageId = "message_hanoi_03",
            threadId = HanoiThreadId,
            senderId = AdminUserId,
            body = "Train to Halong leaves 06:15 tomorrow. Pax count still 2.",
            sentAt = "2026-07-14T18:05:00"
        ),
        MessageEntity(
            messageId = "message_hanoi_04",
            threadId = HanoiThreadId,
            senderId = RegularUserId,
            body = "Street food tour meets at 19:00. Budget line stays blank for now.",
            sentAt = "2026-07-14T19:00:00"
        ),
        MessageEntity(
            messageId = "message_hanoi_05",
            threadId = HanoiThreadId,
            senderId = AdminUserId,
            body = "Copy. Flagging orange if weather shifts overnight.",
            sentAt = "2026-07-14T19:04:00"
        )
    )
}
