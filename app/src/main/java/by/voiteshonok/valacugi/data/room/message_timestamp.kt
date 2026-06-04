package by.voiteshonok.valacugi.data.room

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private val IsoDateTimeFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

fun createMessageId(threadId: String): String {
    return "message_${threadId}_${UUID.randomUUID()}"
}

fun createSentAtIsoTimestamp(): String {
    return IsoDateTimeFormat.format(Date())
}

fun resolveSentAtForNewMessage(latestSentAtInThread: String?): String {
    val nowTimestamp: String = createSentAtIsoTimestamp()
    if (latestSentAtInThread.isNullOrBlank()) {
        return nowTimestamp
    }
    if (nowTimestamp > latestSentAtInThread) {
        return nowTimestamp
    }
    return incrementIsoDateTimeByOneSecond(isoDateTime = latestSentAtInThread)
}

fun incrementIsoDateTimeByOneSecond(isoDateTime: String): String {
    val parsedDate: Date = IsoDateTimeFormat.parse(isoDateTime)
        ?: error("Invalid ISO datetime: $isoDateTime")
    return IsoDateTimeFormat.format(Date(parsedDate.time + 1000L))
}

fun truncateMessagePreview(body: String, maxLength: Int = 40): String {
    val trimmedBody: String = body.trim()
    if (trimmedBody.length <= maxLength) {
        return trimmedBody
    }
    return "${trimmedBody.take(maxLength - 3)}..."
}
