package by.voiteshonok.valacugi.ui.identity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import by.voiteshonok.valacugi.core.notifications.ValacugiNotificationSender
import by.voiteshonok.valacugi.core.session.SessionRepository
import by.voiteshonok.valacugi.domain.User
import by.voiteshonok.valacugi.domain.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class IdentityViewModel(
    private val sessionRepository: SessionRepository,
    private val usersRepository: UsersRepository,
    private val notificationSender: ValacugiNotificationSender
) : ViewModel() {
    private val _uiState: MutableStateFlow<IdentityUiState> = MutableStateFlow(IdentityUiState())
    val uiState: StateFlow<IdentityUiState> = _uiState.asStateFlow()

    init {
        observeCurrentUser()
    }

    fun sendBellNotification(): BellNotificationResult {
        if (!_uiState.value.isPushNotificationsEnabled) {
            return BellNotificationResult.PushDisabled
        }
        if (!notificationSender.canPostNotifications()) {
            return BellNotificationResult.PermissionRequired
        }
        val wasSent: Boolean = notificationSender.sendBellAlert()
        return if (wasSent) {
            BellNotificationResult.Sent
        } else {
            BellNotificationResult.PermissionRequired
        }
    }

    fun setPushNotificationsEnabled(isEnabled: Boolean) {
        val userId: String? = _uiState.value.userId
        if (userId.isNullOrBlank()) {
            return
        }
        viewModelScope.launch {
            usersRepository.setPushNotificationsEnabled(userId = userId, isEnabled = isEnabled)
        }
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            sessionRepository.observeSession()
                .map { session -> session?.identification }
                .flatMapLatest { userId: String? ->
                    if (userId.isNullOrBlank()) {
                        flowOf(null)
                    } else {
                        usersRepository.observeUser(userId = userId)
                    }
                }
                .collect { user: User? ->
                    _uiState.update { previousState ->
                        previousState.copy(
                            userId = user?.id,
                            displayId = formatDisplayId(userId = user?.id),
                            displayName = user?.displayName.orEmpty(),
                            login = user?.login.orEmpty(),
                            isPushNotificationsEnabled = user?.isPushNotificationsEnabled ?: true
                        )
                    }
                }
        }
    }
}

class IdentityViewModelFactory(
    private val sessionRepository: SessionRepository,
    private val usersRepository: UsersRepository,
    private val notificationSender: ValacugiNotificationSender
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IdentityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IdentityViewModel(
                sessionRepository = sessionRepository,
                usersRepository = usersRepository,
                notificationSender = notificationSender
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

fun formatDisplayId(userId: String?): String {
    if (userId.isNullOrBlank()) {
        return "—"
    }
    return "ID: ${userId.uppercase()}"
}
