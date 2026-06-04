package by.voiteshonok.valacugi.data.room

internal fun buildHanoiLastReadMessages(): List<LastReadMessageEntity> {
    return listOf(
        LastReadMessageEntity(
            threadId = HanoiThreadId,
            userId = RegularUserId,
            messageId = "message_hanoi_03",
            seenAt = "2026-07-14T18:05:00"
        ),
        LastReadMessageEntity(
            threadId = HanoiThreadId,
            userId = AdminUserId,
            messageId = "message_hanoi_05",
            seenAt = "2026-07-14T19:04:00"
        )
    )
}
