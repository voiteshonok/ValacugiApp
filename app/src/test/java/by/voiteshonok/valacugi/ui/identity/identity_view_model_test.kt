package by.voiteshonok.valacugi.ui.identity

import by.voiteshonok.valacugi.core.notifications.NotificationSender
import by.voiteshonok.valacugi.core.session.SessionRepository
import by.voiteshonok.valacugi.core.session.UserSession
import by.voiteshonok.valacugi.domain.User
import by.voiteshonok.valacugi.domain.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IdentityViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_whenSessionAndUserExist_reflectsUserFields() = runTest(testDispatcher) {
        val inputUser: User = User(
            id = "user_admin",
            login = "admin",
            displayName = "Administrator",
            isPushNotificationsEnabled = false
        )
        val viewModel: IdentityViewModel = createViewModel(
            session = UserSession(identification = "user_admin"),
            user = inputUser
        )
        advanceUntilIdle()
        val actualState: IdentityUiState = viewModel.uiState.value
        assertEquals("user_admin", actualState.userId)
        assertEquals("ID: USER_ADMIN", actualState.displayId)
        assertEquals("Administrator", actualState.displayName)
        assertEquals("admin", actualState.login)
        assertEquals(false, actualState.isPushNotificationsEnabled)
    }

    @Test
    fun setPushNotificationsEnabled_persistsThroughRepository() = runTest(testDispatcher) {
        val fakeUsersRepository: FakeUsersRepository = FakeUsersRepository(
            user = User(
                id = "user_user",
                login = "user",
                displayName = "User",
                isPushNotificationsEnabled = true
            )
        )
        val viewModel: IdentityViewModel = createViewModel(
            session = UserSession(identification = "user_user"),
            usersRepository = fakeUsersRepository
        )
        advanceUntilIdle()
        viewModel.setPushNotificationsEnabled(isEnabled = false)
        advanceUntilIdle()
        assertEquals("user_user" to false, fakeUsersRepository.lastPushUpdate)
    }

    @Test
    fun sendBellNotification_whenPushDisabled_returnsPushDisabled() = runTest(testDispatcher) {
        val viewModel: IdentityViewModel = createViewModel(
            session = UserSession(identification = "user_admin"),
            user = User(
                id = "user_admin",
                login = "admin",
                displayName = "Administrator",
                isPushNotificationsEnabled = false
            )
        )
        advanceUntilIdle()
        val actualResult: BellNotificationResult = viewModel.sendBellNotification()
        assertEquals(BellNotificationResult.PushDisabled, actualResult)
    }

    @Test
    fun sendBellNotification_whenPermissionMissing_returnsPermissionRequired() = runTest(testDispatcher) {
        val viewModel: IdentityViewModel = createViewModel(
            session = UserSession(identification = "user_admin"),
            notificationSender = FakeNotificationSender(canPost = false, sendSucceeds = false)
        )
        advanceUntilIdle()
        val actualResult: BellNotificationResult = viewModel.sendBellNotification()
        assertEquals(BellNotificationResult.PermissionRequired, actualResult)
    }

    @Test
    fun sendBellNotification_whenAllowed_postsNotification() = runTest(testDispatcher) {
        val fakeNotificationSender: FakeNotificationSender =
            FakeNotificationSender(canPost = true, sendSucceeds = true)
        val viewModel: IdentityViewModel = createViewModel(
            session = UserSession(identification = "user_admin"),
            notificationSender = fakeNotificationSender
        )
        advanceUntilIdle()
        val actualResult: BellNotificationResult = viewModel.sendBellNotification()
        assertEquals(BellNotificationResult.Sent, actualResult)
        assertTrue(fakeNotificationSender.didSendBellAlert)
    }

    private fun createViewModel(
        session: UserSession? = null,
        user: User? = null,
        usersRepository: FakeUsersRepository = FakeUsersRepository(user = user),
        notificationSender: NotificationSender = FakeNotificationSender(
            canPost = true,
            sendSucceeds = true
        )
    ): IdentityViewModel {
        return IdentityViewModel(
            sessionRepository = FakeSessionRepository(initialSession = session),
            usersRepository = usersRepository,
            notificationSender = notificationSender
        )
    }
}

private class FakeSessionRepository(
    initialSession: UserSession?
) : SessionRepository {
    private val sessionFlow: MutableStateFlow<UserSession?> = MutableStateFlow(initialSession)

    override fun observeSession(): Flow<UserSession?> = sessionFlow

    override suspend fun saveSession(session: UserSession) {
        sessionFlow.value = session
    }

    override suspend fun clearSession() {
        sessionFlow.value = null
    }
}

private class FakeUsersRepository(
    private val user: User? = null
) : UsersRepository {
    var lastPushUpdate: Pair<String, Boolean>? = null

    override fun observeUsers(): Flow<List<User>> = flowOf(emptyList())

    override fun observeUser(userId: String): Flow<User?> {
        return flowOf(user?.takeIf { storedUser -> storedUser.id == userId })
    }

    override suspend fun authenticate(login: String, password: String): User? = null

    override suspend fun setPushNotificationsEnabled(userId: String, isEnabled: Boolean) {
        lastPushUpdate = userId to isEnabled
    }
}

private class FakeNotificationSender(
    private val canPost: Boolean,
    private val sendSucceeds: Boolean
) : NotificationSender {
    var didSendBellAlert: Boolean = false

    override fun canPostNotifications(): Boolean = canPost

    override fun sendBellAlert(): Boolean {
        didSendBellAlert = sendSucceeds
        return sendSucceeds
    }
}
