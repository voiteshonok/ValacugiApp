package by.voiteshonok.valacugi.ui.atlas

import by.voiteshonok.valacugi.domain.TripItinerary

data class TripDetailsUiState(
    val isLoading: Boolean = true,
    val itinerary: TripItinerary? = null,
    val currentUserId: String? = null,
    val isCurrentUserAssigned: Boolean = false,
    val isMembershipActionInProgress: Boolean = false,
    val isCreatorActionInProgress: Boolean = false
) {
    val canManageMembership: Boolean
        get() = !currentUserId.isNullOrBlank()

    val membershipButtonLabel: String
        get() = if (isCurrentUserAssigned) "UNJOIN" else "JOIN"

    val canManageCreatedTrip: Boolean
        get() {
            val itineraryTrip = itinerary ?: return false
            val userId: String = currentUserId ?: return false
            return itineraryTrip.trip.createdById == userId
        }
}

