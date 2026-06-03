package by.voiteshonok.valacugi.ui.identity

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import by.voiteshonok.valacugi.ui.theme.AtlasDotPattern
import by.voiteshonok.valacugi.ui.theme.AtlasPrimary
import by.voiteshonok.valacugi.ui.theme.AtlasSurfaceBright
import by.voiteshonok.valacugi.ui.theme.AtlasSurfaceContainerHigh

private const val TopBarHeightDp: Int = 64
private const val AvatarSizeDp: Int = 128
private const val ConfigCheckboxSizeDp: Int = 24

@Composable
fun IdentityScreen(
    modifier: Modifier = Modifier,
    viewModelFactory: ViewModelProvider.Factory,
    onLogout: () -> Unit,
    onMenuClick: () -> Unit = {}
) {
    val viewModel: IdentityViewModel = viewModel(factory = viewModelFactory)
    val uiState: IdentityUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        viewModel.sendBellNotification()
    }
    val onBellClick: () -> Unit = remember(viewModel, permissionLauncher) {
        {
            when (viewModel.sendBellNotification()) {
                BellNotificationResult.PermissionRequired -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                BellNotificationResult.Sent,
                BellNotificationResult.PushDisabled -> Unit
            }
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        IdentityTopBar(
            onMenuClick = onMenuClick,
            onNotificationsClick = onBellClick
        )
        IdentityUserSection(displayId = uiState.displayId)
        IdentitySystemConfigurationSection(
            isPushNotificationsEnabled = uiState.isPushNotificationsEnabled,
            onPushNotificationsToggle = { isEnabled: Boolean ->
                viewModel.setPushNotificationsEnabled(isEnabled = isEnabled)
            },
            onLogout = onLogout
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .dotPatternBackground()
        )
    }
}

@Composable
private fun IdentityTopBar(
    onMenuClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TopBarHeightDp.dp)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "[  VALACUGI  ]",
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        IconButton(onClick = onNotificationsClick) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun IdentityUserSection(
    displayId: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(AvatarSizeDp.dp)
                    .background(AtlasPrimary)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "IDENTITY",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 48.sp,
                        lineHeight = 48.sp,
                        letterSpacing = (-1.5).sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(AtlasPrimary)
                    )
                    Text(
                        text = displayId,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun IdentitySystemConfigurationSection(
    isPushNotificationsEnabled: Boolean,
    onPushNotificationsToggle: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                .background(AtlasSurfaceContainerHigh)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "SYSTEM CONFIGURATION",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        IdentityConfigToggleRow(
            label = "PUSH_NOTIFS",
            isChecked = isPushNotificationsEnabled,
            isEnabled = true,
            onToggle = onPushNotificationsToggle
        )
        IdentityLogoutRow(onLogout = onLogout)
    }
}

@Composable
private fun IdentityConfigToggleRow(
    label: String,
    isChecked: Boolean,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .then(
                if (isEnabled) {
                    Modifier.clickable { onToggle(!isChecked) }
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = if (isEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                },
                textDecoration = if (isEnabled) TextDecoration.None else TextDecoration.LineThrough
            )
        )
        Box(
            modifier = Modifier
                .width(56.dp)
                .fillMaxSize()
                .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                .background(AtlasSurfaceBright),
            contentAlignment = Alignment.Center
        ) {
            IdentityCheckbox(
                isChecked = isChecked,
                isEnabled = isEnabled
            )
        }
    }
}

@Composable
private fun IdentityLogoutRow(
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .clickable(onClick = onLogout),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "LOGOUT",
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Box(
            modifier = Modifier
                .width(56.dp)
                .fillMaxSize()
                .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                .background(AtlasSurfaceBright),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun IdentityCheckbox(
    isChecked: Boolean,
    isEnabled: Boolean
) {
    Box(
        modifier = Modifier
            .size(ConfigCheckboxSizeDp.dp)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .background(
                if (isChecked && isEnabled) {
                    AtlasPrimary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isChecked && isEnabled) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private fun Modifier.dotPatternBackground(): Modifier {
    return drawBehind { drawDotPattern() }
}

private fun DrawScope.drawDotPattern() {
    val dotColor: Color = AtlasDotPattern.copy(alpha = 0.2f)
    val spacing: Float = 16.dp.toPx()
    val radius: Float = 1.dp.toPx()
    var y: Float = spacing
    while (y < size.height) {
        var x: Float = spacing
        while (x < size.width) {
            drawCircle(color = dotColor, radius = radius, center = Offset(x = x, y = y))
            x += spacing
        }
        y += spacing
    }
}
