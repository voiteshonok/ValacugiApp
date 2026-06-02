package by.voiteshonok.valacugi.core.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SessionDataStoreName: String = "valacugi_session"

private val Context.sessionDataStore by preferencesDataStore(name = SessionDataStoreName)

class DataStoreSessionRepository(
    private val context: Context
) : SessionRepository {
    override fun observeSession(): Flow<UserSession?> {
        return context.sessionDataStore.data.map { preferences: Preferences ->
            val identification: String? = preferences[PreferencesKeys.Identification]
            if (identification.isNullOrBlank()) null else UserSession(identification = identification)
        }
    }

    override suspend fun saveSession(session: UserSession) {
        context.sessionDataStore.edit { preferences: androidx.datastore.preferences.core.MutablePreferences ->
            preferences[PreferencesKeys.Identification] = session.identification
        }
    }

    override suspend fun clearSession() {
        context.sessionDataStore.edit { preferences: androidx.datastore.preferences.core.MutablePreferences ->
            preferences.remove(PreferencesKeys.Identification)
        }
    }
}

private object PreferencesKeys {
    val Identification: Preferences.Key<String> = stringPreferencesKey("identification")
}

