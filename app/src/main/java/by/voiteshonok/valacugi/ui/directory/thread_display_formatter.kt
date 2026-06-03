package by.voiteshonok.valacugi.ui.directory

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object ThreadDisplayFormatter {
    private val IsoDateTimeFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    private val ClockTimeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
    private val WeekdayFormat: SimpleDateFormat = SimpleDateFormat("EEE", Locale.US)
    private val MonthDayFormat: SimpleDateFormat = SimpleDateFormat("MMM dd", Locale.US)

    fun formatLastMessageAt(isoDateTime: String): String {
        val messageAt: Calendar = parseIsoDateTime(isoDateTime = isoDateTime) ?: return isoDateTime
        val now: Calendar = Calendar.getInstance()
        val startOfToday: Calendar = startOfDay(calendar = now)
        val startOfMessageDay: Calendar = startOfDay(calendar = messageAt)
        val daysBetween: Long = TimeUnit.MILLISECONDS.toDays(
            startOfToday.timeInMillis - startOfMessageDay.timeInMillis
        )
        if (daysBetween == 0L) {
            return ClockTimeFormat.format(messageAt.time)
        }
        if (daysBetween == 1L) {
            return "YEST"
        }
        if (daysBetween in 2L..6L) {
            return WeekdayFormat.format(messageAt.time).uppercase(Locale.US)
        }
        return MonthDayFormat.format(messageAt.time).uppercase(Locale.US)
    }

    fun formatChatHeaderTitle(threadTitle: String): String {
        return threadTitle.uppercase(Locale.US).replace(oldValue = " ", newValue = "_")
    }

    private fun parseIsoDateTime(isoDateTime: String): Calendar? {
        return try {
            val parsedAt = IsoDateTimeFormat.parse(isoDateTime) ?: return null
            Calendar.getInstance().apply { time = parsedAt }
        } catch (exception: Exception) {
            null
        }
    }

    private fun startOfDay(calendar: Calendar): Calendar {
        return (calendar.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}
