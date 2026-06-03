package by.voiteshonok.valacugi.core.notifications

interface NotificationSender {
    fun canPostNotifications(): Boolean
    fun sendBellAlert(): Boolean
}
