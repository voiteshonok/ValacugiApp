package by.voiteshonok.valacugi.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import by.voiteshonok.valacugi.ui.chat.ChatScreen
import by.voiteshonok.valacugi.ui.chat.ChatViewModelFactory
import by.voiteshonok.valacugi.ui.shell.ShellScreen
import by.voiteshonok.valacugi.core.trip_creation.TripCreationDraftStore
import by.voiteshonok.valacugi.ui.trips.TripConstructorScreen
import by.voiteshonok.valacugi.ui.trips.TripConstructorViewModelFactory
import by.voiteshonok.valacugi.ui.trips.TripInitializationScreen

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
                usersRepository = appContainer.usersRepository,
                sessionRepository = appContainer.sessionRepository,
                threadsRepository = appContainer.threadsRepository,
                notificationSender = appContainer.notificationSender,
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
                    getTripDetails = GetTripDetails(tripsRepository = appContainer.tripsRepository),
                    tripsRepository = appContainer.tripsRepository,
                    sessionRepository = appContainer.sessionRepository
                )
            )
        }
        composable(route = AppRoutes.TripInitialization) {
            TripInitializationScreen(
                onNavigateBack = { navController.popBackStack() },
                onInitializeTrip = { draft ->
                    TripCreationDraftStore.saveDraft(draft = draft)
                    navController.navigate(AppRoutes.TripConstructor) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = AppRoutes.TripConstructor) {
            val draft = TripCreationDraftStore.currentDraft
            if (draft == null) {
                LaunchedEffect(Unit) {
                    navController.popBackStack(
                        route = AppRoutes.Shell,
                        inclusive = false
                    )
                }
            } else {
                TripConstructorScreen(
                    draft = draft,
                    onNavigateBack = { navController.popBackStack() },
                    onFinished = {
                        navController.popBackStack(
                            route = AppRoutes.TripInitialization,
                            inclusive = true
                        )
                        TripCreationDraftStore.clearDraft()
                    },
                    viewModelFactory = TripConstructorViewModelFactory(
                        draft = draft,
                        tripsRepository = appContainer.tripsRepository,
                        sessionRepository = appContainer.sessionRepository
                    )
                )
            }
        }
        composable(
            route = AppRoutes.Chat,
            arguments = listOf(
                navArgument(AppRouteArguments.ThreadId) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val threadId: String = backStackEntry.arguments?.getString(AppRouteArguments.ThreadId).orEmpty()
            ChatScreen(
                threadId = threadId,
                onNavigateBack = { navController.popBackStack() },
                viewModelFactory = ChatViewModelFactory(
                    threadId = threadId,
                    threadsRepository = appContainer.threadsRepository,
                    messagesRepository = appContainer.messagesRepository,
                    sessionRepository = appContainer.sessionRepository
                )
            )
        }
    }
}

