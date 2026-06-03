package by.voiteshonok.valacugi.ui.identity

data class IdentityUiState(
    val userId: String? = null,
    val displayId: String = "—",
    val displayName: String = "",
    val login: String = "",
    val isPushNotificationsEnabled: Boolean = true
)
