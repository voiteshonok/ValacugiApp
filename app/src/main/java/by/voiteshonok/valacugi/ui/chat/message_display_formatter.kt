package by.voiteshonok.valacugi.ui.chat

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object MessageDisplayFormatter {
    private val IsoDateTimeFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    private val ClockTimeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)

    fun formatSentAt(isoDateTime: String): String {
        val sentAt: Calendar = parseIsoDateTime(isoDateTime = isoDateTime) ?: return isoDateTime
        return ClockTimeFormat.format(sentAt.time)
    }

    fun formatSenderLabel(senderId: String, currentUserId: String?): String {
        val senderCode: String = senderId.removePrefix(prefix = "user_").uppercase(Locale.US)
        if (!currentUserId.isNullOrBlank() && senderId == currentUserId) {
            return "$senderCode (YOU)"
        }
        return senderCode
    }

    private fun parseIsoDateTime(isoDateTime: String): Calendar? {
        return try {
            val parsedAt = IsoDateTimeFormat.parse(isoDateTime) ?: return null
            Calendar.getInstance().apply { time = parsedAt }
        } catch (exception: Exception) {
            null
        }
    }
}
