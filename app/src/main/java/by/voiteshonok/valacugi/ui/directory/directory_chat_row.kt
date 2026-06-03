package by.voiteshonok.valacugi.ui.directory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.voiteshonok.valacugi.domain.MessageThread
import by.voiteshonok.valacugi.ui.theme.AtlasOnSurfaceVariant
import by.voiteshonok.valacugi.ui.theme.AtlasSafetyOrange
import by.voiteshonok.valacugi.ui.theme.AtlasSurfaceContainerHigh

private const val ChatRowHeightDp: Int = 72

@Composable
fun DirectoryChatRow(
    thread: MessageThread,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderWidth: Dp = 1.dp
    val borderColor = MaterialTheme.colorScheme.primary
    val density = LocalDensity.current
    val timestampLabel: String = ThreadDisplayFormatter.formatLastMessageAt(isoDateTime = thread.lastMessageAt)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ChatRowHeightDp.dp)
            .drawBehind {
                val strokeWidthPx: Float = with(density) { borderWidth.toPx() }
                val y: Float = size.height - strokeWidthPx / 2f
                drawLine(
                    color = borderColor,
                    start = Offset(x = 0f, y = y),
                    end = Offset(x = size.width, y = y),
                    strokeWidth = strokeWidthPx
                )
            }
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = thread.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = thread.lastMessagePreview,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            val timestampColor = if (thread.hasUnread) {
                MaterialTheme.colorScheme.primary
            } else {
                AtlasOnSurfaceVariant
            }
            Text(
                text = timestampLabel,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = timestampColor
                )
            )
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(8.dp)
                    .background(
                        color = if (thread.hasUnread) AtlasSafetyOrange else AtlasSurfaceContainerHigh
                    )
            )
        }
    }
}
