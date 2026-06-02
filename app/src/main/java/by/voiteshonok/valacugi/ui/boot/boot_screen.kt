package by.voiteshonok.valacugi.ui.boot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import by.voiteshonok.valacugi.core.session.SessionRepository

@Composable
fun BootScreen(
    modifier: Modifier = Modifier,
    sessionRepository: SessionRepository,
    onNavigateToAccess: () -> Unit,
    onNavigateToShell: () -> Unit
) {
    val session by sessionRepository.observeSession().collectAsState(initial = null)
    LaunchedEffect(session) {
        if (session == null) {
            onNavigateToAccess()
            return@LaunchedEffect
        }
        onNavigateToShell()
    }
}

