package by.voiteshonok.valacugi.core.di

import android.content.Context
import by.voiteshonok.valacugi.core.session.DataStoreSessionRepository
import by.voiteshonok.valacugi.core.session.SessionRepository

class AppContainer(context: Context) {
    val sessionRepository: SessionRepository = DataStoreSessionRepository(context = context)
}

