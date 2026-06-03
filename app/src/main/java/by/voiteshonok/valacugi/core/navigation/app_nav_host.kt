package by.voiteshonok.valacugi.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import by.voiteshonok.valacugi.access.AccessCredentials
import by.voiteshonok.valacugi.access.AccessScreen
import by.voiteshonok.valacugi.core.di.AppContainer
import by.voiteshonok.valacugi.core.session.UserSession
import by.voiteshonok.valacugi.domain.GetTripDetails
import by.voiteshonok.valacugi.ui.atlas.TripDetailsScreen
import by.voiteshonok.valacugi.ui.atlas.TripDetailsViewModelFactory
import by.voiteshonok.valacugi.ui.boot.BootScreen
import by.voiteshonok.valacugi.ui.shell.ShellScreen
import by.voiteshonok.valacugi.ui.trips.TripConstructorScreen

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
                authenticate = { credentials: AccessCredentials ->
                    appContainer.accessCredentialsValidator.authenticate(
                        identification = credentials.identification,
                        credential = credentials.credential
                    )
                },
                onContinue = { user ->
                    appContainer.sessionRepository.saveSession(
                        UserSession(identification = user.id)
                    )
                    navController.navigate(AppRoutes.Shell) {
                        popUpTo(AppRoutes.Access) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = AppRoutes.Shell) {
            ShellScreen(
                rootNavController = navController,
                tripsRepository = appContainer.tripsRepository,
                onLogout = {
                    appContainer.sessionRepository.clearSession()
                    navController.navigate(AppRoutes.Access) {
                        popUpTo(AppRoutes.Shell) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = AppRoutes.Atlas,
            arguments = listOf(
                navArgument(AppRouteArguments.TripId) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tripId: String = backStackEntry.arguments?.getString(AppRouteArguments.TripId).orEmpty()
            TripDetailsScreen(
                viewModelFactory = TripDetailsViewModelFactory(
                    tripId = tripId,
                    getTripDetails = GetTripDetails(tripsRepository = appContainer.tripsRepository)
                )
            )
        }
        composable(route = AppRoutes.TripConstructor) {
            TripConstructorScreen()
        }
    }
}

