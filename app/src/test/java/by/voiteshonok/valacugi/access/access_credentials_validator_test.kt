package by.voiteshonok.valacugi.access

import by.voiteshonok.valacugi.domain.User
import by.voiteshonok.valacugi.domain.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AccessCredentialsValidatorTest {
    @Test
    fun authenticate_whenIdentificationIsEmpty_returnsNull() = runTest {
        val validator: AccessCredentialsValidator = createValidator()
        val actualUser: User? = validator.authenticate(identification = "", credential = "admin")
        assertNull(actualUser)
    }

    @Test
    fun authenticate_whenCredentialIsEmpty_returnsNull() = runTest {
        val validator: AccessCredentialsValidator = createValidator()
        val actualUser: User? = validator.authenticate(identification = "admin", credential = "")
        assertNull(actualUser)
    }

    @Test
    fun authenticate_whenIdentificationHasWhitespace_trimsBeforeRepositoryCall() = runTest {
        val fakeUsersRepository: RecordingUsersRepository = RecordingUsersRepository(
            userByCredentials = adminUser()
        )
        val validator: AccessCredentialsValidator = AccessCredentialsValidator(
            usersRepository = fakeUsersRepository
        )
        val actualUser: User? = validator.authenticate(identification = "  admin  ", credential = "admin")
        assertEquals("admin", fakeUsersRepository.lastLogin)
        assertEquals(adminUser(), actualUser)
    }

    @Test
    fun authenticate_whenCredentialsMatch_returnsUser() = runTest {
        val expectedUser: User = adminUser()
        val validator: AccessCredentialsValidator = createValidator(
            userByCredentials = expectedUser
        )
        val actualUser: User? = validator.authenticate(identification = "admin", credential = "admin")
        assertEquals(expectedUser, actualUser)
    }

    @Test
    fun authenticate_whenCredentialsDoNotMatch_returnsNull() = runTest {
        val validator: AccessCredentialsValidator = createValidator(userByCredentials = null)
        val actualUser: User? = validator.authenticate(identification = "admin", credential = "wrong")
        assertNull(actualUser)
    }

    private fun createValidator(userByCredentials: User? = null): AccessCredentialsValidator {
        return AccessCredentialsValidator(
            usersRepository = RecordingUsersRepository(userByCredentials = userByCredentials)
        )
    }

    private fun adminUser(): User {
        return User(
            id = "user_admin",
            login = "admin",
            displayName = "Administrator",
            isPushNotificationsEnabled = true
        )
    }
}

private class RecordingUsersRepository(
    private val userByCredentials: User?
) : UsersRepository {
    var lastLogin: String? = null
    var lastPassword: String? = null

    override fun observeUsers(): Flow<List<User>> = flowOf(emptyList())

    override fun observeUser(userId: String): Flow<User?> = flowOf(null)

    override suspend fun authenticate(login: String, password: String): User? {
        lastLogin = login
        lastPassword = password
        return userByCredentials
    }

    override suspend fun setPushNotificationsEnabled(userId: String, isEnabled: Boolean) = Unit
}
