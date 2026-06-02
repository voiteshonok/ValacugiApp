package by.voiteshonok.valacugi.ui.boot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import by.voiteshonok.valacugi.core.session.SessionRepository
import by.voiteshonok.valacugi.core.session.UserSession
import kotlinx.coroutines.flow.first

@Composable
fun BootScreen(
    modifier: Modifier = Modifier,
    sessionRepository: SessionRepository,
    onNavigateToAccess: () -> Unit,
    onNavigateToShell: () -> Unit
) {
    LaunchedEffect(sessionRepository) {
        val session: UserSession? = sessionRepository.observeSession().first()
        if (session == null) {
            onNavigateToAccess()
            return@LaunchedEffect
        }
        onNavigateToShell()
    }
}
