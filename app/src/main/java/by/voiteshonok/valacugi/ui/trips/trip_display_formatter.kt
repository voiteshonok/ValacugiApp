package by.voiteshonok.valacugi.ui.trips

import by.voiteshonok.valacugi.domain.Trip

private const val UnknownPlaceholder: String = "[ ? ]"

object TripDisplayFormatter {
    fun formatDates(trip: Trip): String {
        val dateStart: String? = trip.dateStart
        val dateEnd: String? = trip.dateEnd
        if (dateStart.isNullOrBlank() || dateEnd.isNullOrBlank()) {
            return UnknownPlaceholder
        }
        val startLabel: String = formatIsoDateShort(dateStart)
        val endLabel: String = formatIsoDateShort(dateEnd)
        if (startLabel == endLabel) {
            return startLabel
        }
        return "$startLabel–$endLabel"
    }

    fun formatPax(trip: Trip): String {
        val pax: Int? = trip.pax
        if (pax == null) {
            return UnknownPlaceholder
        }
        return String.format("%02d", pax)
    }

    fun formatBudget(trip: Trip): String {
        val budgetText: String? = trip.budgetText
        if (budgetText.isNullOrBlank()) {
            return UnknownPlaceholder
        }
        return budgetText
    }

    fun formatAssigned(trip: Trip): String {
        val assignedCount: Int? = trip.assignedCount
        val assignedTotal: Int? = trip.assignedTotal
        if (assignedCount == null && assignedTotal == null) {
            return UnknownPlaceholder
        }
        if (assignedTotal == null) {
            return "${assignedCount ?: "?"}/?"
        }
        return "${assignedCount ?: "?"}/$assignedTotal"
    }

    private fun formatIsoDateShort(isoDate: String): String {
        val parts: List<String> = isoDate.split("-")
        if (parts.size != 3) {
            return isoDate
        }
        val day: String = parts[2]
        val month: String = parts[1]
        return "$day/$month"
    }
}
