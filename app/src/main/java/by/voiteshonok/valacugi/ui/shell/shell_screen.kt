package by.voiteshonok.valacugi.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import by.voiteshonok.valacugi.core.navigation.AppRoutes
import by.voiteshonok.valacugi.core.notifications.ValacugiNotificationSender
import by.voiteshonok.valacugi.core.session.SessionRepository
import by.voiteshonok.valacugi.domain.ThreadsRepository
import by.voiteshonok.valacugi.domain.TripsRepository
import by.voiteshonok.valacugi.domain.UsersRepository
import by.voiteshonok.valacugi.ui.directory.DirectoryScreen
import by.voiteshonok.valacugi.ui.directory.DirectoryViewModelFactory
import by.voiteshonok.valacugi.ui.identity.IdentityScreen
import by.voiteshonok.valacugi.ui.identity.IdentityViewModelFactory
import by.voiteshonok.valacugi.ui.theme.AtlasOnPrimary
import by.voiteshonok.valacugi.ui.theme.AtlasPrimary
import by.voiteshonok.valacugi.ui.trips.TripsScreen
import by.voiteshonok.valacugi.ui.trips.TripsViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun ShellScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    tripsRepository: TripsRepository,
    threadsRepository: ThreadsRepository,
    usersRepository: UsersRepository,
    sessionRepository: SessionRepository,
    notificationSender: ValacugiNotificationSender,
    onLogout: suspend () -> Unit
) {
    val shellNavController: NavHostController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val navBackStackEntry = shellNavController.currentBackStackEntryAsState().value
    val currentRoute: String? = navBackStackEntry?.destination?.route
    Scaffold(
        modifier = modifier,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(64.dp)
                    .border(width = 1.dp, color = AtlasPrimary)
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShellNavigationItem(
                    modifier = Modifier.weight(1f),
                    label = "TRIPS",
                    icon = Icons.Filled.DateRange,
                    isSelected = currentRoute == AppRoutes.Trips,
                    showStartBorder = false,
                    onClick = { shellNavController.navigate(AppRoutes.Trips) { launchSingleTop = true } }
                )
                ShellNavigationItem(
                    modifier = Modifier.weight(1f),
                    label = "MESSAGES",
                    icon = Icons.Filled.Email,
                    isSelected = currentRoute == AppRoutes.Directory,
                    showStartBorder = true,
                    onClick = { shellNavController.navigate(AppRoutes.Directory) { launchSingleTop = true } }
                )
                ShellNavigationItem(
                    modifier = Modifier.weight(1f),
                    label = "IDENTITY",
                    icon = Icons.Filled.Person,
                    isSelected = currentRoute == AppRoutes.Identity,
                    showStartBorder = true,
                    onClick = { shellNavController.navigate(AppRoutes.Identity) { launchSingleTop = true } }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = shellNavController,
            startDestination = AppRoutes.Trips,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = AppRoutes.Trips) {
                TripsScreen(
                    onOpenAtlas = { tripId: String ->
                        rootNavController.navigate("atlas/$tripId")
                    },
                    onOpenTripConstructor = {
                        rootNavController.navigate(AppRoutes.TripConstructor)
                    },
                    viewModelFactory = TripsViewModelFactory(tripsRepository = tripsRepository)
                )
            }
            composable(route = AppRoutes.Directory) {
                DirectoryScreen(
                    viewModelFactory = DirectoryViewModelFactory(
                        threadsRepository = threadsRepository,
                        sessionRepository = sessionRepository
                    ),
                    onOpenChat = { threadId: String ->
                        rootNavController.navigate("chat/$threadId")
                    }
                )
            }
            composable(route = AppRoutes.Identity) {
                IdentityScreen(
                    viewModelFactory = IdentityViewModelFactory(
                        sessionRepository = sessionRepository,
                        usersRepository = usersRepository,
                        notificationSender = notificationSender
                    ),
                    onLogout = {
                        coroutineScope.launch {
                            onLogout()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ShellNavigationItem(
    modifier: Modifier = Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    showStartBorder: Boolean,
    onClick: () -> Unit
) {
    val contentColor: Color = if (isSelected) AtlasOnPrimary else AtlasPrimary
    val backgroundColor: Color = if (isSelected) AtlasPrimary else Color.White
    Column(
        modifier = modifier
            .fillMaxHeight()
            .then(
                if (showStartBorder) {
                    Modifier.border(width = 1.dp, color = AtlasPrimary)
                } else {
                    Modifier
                }
            )
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 1.5.sp,
                color = contentColor
            )
        )
    }
}

