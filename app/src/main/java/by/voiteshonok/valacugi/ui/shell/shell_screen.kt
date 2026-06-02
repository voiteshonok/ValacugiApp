package by.voiteshonok.valacugi.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import by.voiteshonok.valacugi.domain.TripsRepository
import by.voiteshonok.valacugi.ui.directory.DirectoryScreen
import by.voiteshonok.valacugi.ui.identity.IdentityScreen
import by.voiteshonok.valacugi.ui.trips.TripsScreen
import by.voiteshonok.valacugi.ui.trips.TripsViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun ShellScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    tripsRepository: TripsRepository,
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
                    .background(Color.White),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShellNavigationItem(
                    label = "TRIPS",
                    isSelected = currentRoute == AppRoutes.Trips,
                    onClick = { shellNavController.navigate(AppRoutes.Trips) { launchSingleTop = true } }
                )
                ShellNavigationItem(
                    label = "MESSAGES",
                    isSelected = currentRoute == AppRoutes.Directory,
                    onClick = { shellNavController.navigate(AppRoutes.Directory) { launchSingleTop = true } }
                )
                ShellNavigationItem(
                    label = "IDENTITY",
                    isSelected = currentRoute == AppRoutes.Identity,
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
                    viewModelFactory = TripsViewModelFactory(tripsRepository = tripsRepository)
                )
            }
            composable(route = AppRoutes.Directory) {
                DirectoryScreen(
                    onOpenThread = { threadId: String ->
                        rootNavController.navigate("transmission/$threadId")
                    }
                )
            }
            composable(route = AppRoutes.Identity) {
                IdentityScreen(onLogout = {
                    coroutineScope.launch {
                        onLogout()
                    }
                })
            }
        }
    }
}

@Composable
private fun ShellNavigationItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 1.5.sp,
                color = Color.Black
            )
        )
    }
}

