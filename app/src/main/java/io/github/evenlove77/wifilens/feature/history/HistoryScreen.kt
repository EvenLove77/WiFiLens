package io.github.evenlove77.wifilens.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.evenlove77.wifilens.core.component.GlassCard
import io.github.evenlove77.wifilens.core.theme.*
import io.github.evenlove77.wifilens.data.model.HistoryItem
import io.github.evenlove77.wifilens.data.model.HistoryStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ===== 标题栏 =====
            Text(
                text = "历史记录",
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingLG, vertical = SpacingMD)
                    .statusBarsPadding()
            )

            // ===== 时间线列表 =====
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = SpacingXL,
                    end = SpacingMD,
                    top = SpacingSM,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(
                    items = uiState.items,
                    key = { it.id }
                ) { item ->
                    TimelineItem(item = item)
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(item: HistoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // ===== 左侧时间线 =====
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            // 圆点
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        when (item.status) {
                            HistoryStatus.SUCCESS -> SuccessGreen
                            HistoryStatus.FAILED -> ErrorRed
                            HistoryStatus.TIMEOUT -> WarningOrange
                            HistoryStatus.WEAK_SIGNAL -> WarningOrange
                        }
                    )
            )
            // 竖线
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(72.dp)
                    .background(GlassBorder.copy(alpha = 0.3f))
            )
        }

        Spacer(modifier = Modifier.width(SpacingMD))

        // ===== 右侧卡片 =====
        GlassCard(
            modifier = Modifier.weight(1f),
            backgroundColor = SurfaceDark.copy(alpha = 0.6f),
            borderColor = GlassBorder.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingMD),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 状态图标
                Icon(
                    imageVector = statusIcon(item.status),
                    contentDescription = null,
                    tint = when (item.status) {
                        HistoryStatus.SUCCESS -> SuccessGreen
                        HistoryStatus.FAILED -> ErrorRed
                        HistoryStatus.TIMEOUT -> WarningOrange
                        HistoryStatus.WEAK_SIGNAL -> WarningOrange
                    },
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(SpacingSM))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.ssid,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.status.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = when (item.status) {
                            HistoryStatus.SUCCESS -> SuccessGreen
                            else -> TextSecondary
                        }
                    )
                }

                Text(
                    text = formatDate(item.time),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private fun statusIcon(status: HistoryStatus): ImageVector = when (status) {
    HistoryStatus.SUCCESS -> Icons.Rounded.CheckCircle
    HistoryStatus.FAILED -> Icons.Rounded.Cancel
    HistoryStatus.TIMEOUT -> Icons.Rounded.Timer
    HistoryStatus.WEAK_SIGNAL -> Icons.Rounded.SignalWifiStatusbarConnectedNoInternet4
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
