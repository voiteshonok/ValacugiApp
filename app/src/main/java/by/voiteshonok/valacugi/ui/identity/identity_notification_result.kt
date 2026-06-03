package by.voiteshonok.valacugi.ui.identity

sealed interface BellNotificationResult {
    data object Sent : BellNotificationResult
    data object PushDisabled : BellNotificationResult
    data object PermissionRequired : BellNotificationResult
}
