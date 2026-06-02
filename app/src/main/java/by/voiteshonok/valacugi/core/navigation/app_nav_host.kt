package by.voiteshonok.valacugi.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.voiteshonok.valacugi.access.AccessScreen
import by.voiteshonok.valacugi.core.di.AppContainer
import by.voiteshonok.valacugi.core.session.UserSession
import by.voiteshonok.valacugi.ui.boot.BootScreen
import by.voiteshonok.valacugi.ui.shell.ShellScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Boot,
        modifier = modifier
    ) {
        composable(route = AppRoutes.Boot) {
            BootScreen(
                sessionRepository = appContainer.sessionRepository,
                onNavigateToAccess = {
                    navController.navigate(AppRoutes.Access) {
                        popUpTo(AppRoutes.Boot) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToShell = {
                    navController.navigate(AppRoutes.Shell) {
                        popUpTo(AppRoutes.Boot) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = AppRoutes.Access) {
            AccessScreen(
                onContinue = { credentials ->
                    appContainer.sessionRepository.saveSession(
                        UserSession(identification = credentials.identification)
                    )
                    navController.navigate(AppRoutes.Shell) {
                        popUpTo(AppRoutes.Access) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = AppRoutes.Shell) {
            ShellScreen(rootNavController = navController)
        }
    }
}

