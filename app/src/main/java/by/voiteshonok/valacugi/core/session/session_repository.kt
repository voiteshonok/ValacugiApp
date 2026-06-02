package by.voiteshonok.valacugi.core.session

import kotlinx.coroutines.flow.Flow

data class UserSession(
    val identification: String
)

interface SessionRepository {
    fun observeSession(): Flow<UserSession?>
    suspend fun saveSession(session: UserSession)
    suspend fun clearSession()
}

