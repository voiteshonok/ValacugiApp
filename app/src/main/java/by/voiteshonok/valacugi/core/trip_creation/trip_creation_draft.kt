package by.voiteshonok.valacugi.core.trip_creation

import java.util.UUID

data class TripCreationDraft(
    val expeditionId: String,
    val location: String,
    val dateRangeText: String,
    val pax: String,
    val budget: String,
    val roster: String,
    val editingTripId: String? = null
) {
    val isEditMode: Boolean
        get() = !editingTripId.isNullOrBlank()
    val locationHeaderLabel: String
        get() {
            val trimmedLocation: String = location.trim()
            if (trimmedLocation.isEmpty()) {
                return "PENDING"
            }
            return trimmedLocation.uppercase()
        }
}

data class TripStepDraft(
    val index: Int,
    val title: String,
    val coordinates: String,
    val timeOffset: String,
    val actionType: String
)

fun generateExpeditionId(): String {
    val token: String = UUID.randomUUID().toString().replace(oldValue = "-", newValue = "").take(n = 6).uppercase()
    return "0X$token.A1"
}
