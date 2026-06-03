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

fun truncateMessagePreview(body: String, maxLength: Int = 40): String {
    val trimmedBody: String = body.trim()
    if (trimmedBody.length <= maxLength) {
        return trimmedBody
    }
    return "${trimmedBody.take(maxLength - 3)}..."
}
