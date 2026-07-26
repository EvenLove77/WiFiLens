package io.github.evenlove77.wifilens.feature.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.evenlove77.wifilens.core.component.GlassCard
import io.github.evenlove77.wifilens.core.theme.*
import io.github.evenlove77.wifilens.data.model.VaultItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    viewModel: VaultViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = listOf(null, "家庭", "办公室", "其他")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ===== 标题栏 =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingLG, vertical = SpacingMD)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "密码库",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = { /* 导入 */ }) {
                        Icon(
                            imageVector = Icons.Rounded.FileUpload,
                            contentDescription = "导入",
                            tint = AppleBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(onClick = { /* 导出 */ }) {
                        Icon(
                            imageVector = Icons.Rounded.FileDownload,
                            contentDescription = "导出",
                            tint = AppleBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // ===== 分类筛选 =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingMD),
                horizontalArrangement = Arrangement.spacedBy(SpacingSM)
            ) {
                categories.forEach { category ->
                    val isSelected = uiState.selectedCategory == category
                    val label = category ?: "全部"
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(category) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppleBlue.copy(alpha = 0.3f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) AppleBlue.copy(alpha = 0.5f) else GlassBorder,
                            selectedBorderColor = AppleBlue.copy(alpha = 0.5f),
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingMD))

            // ===== 列表 =====
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = SpacingMD,
                    vertical = SpacingSM
                ),
                verticalArrangement = Arrangement.spacedBy(SpacingSM)
            ) {
                items(
                    items = uiState.items,
                    key = { it.id }
                ) { item ->
                    VaultItemCard(item = item)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // ===== FAB 添加按钮 =====
        FloatingActionButton(
            onClick = { /* 添加 */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(SpacingLG),
            containerColor = AppleBlue,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "添加",
                tint = TextPrimary
            )
        }
    }
}

@Composable
private fun VaultItemCard(
    item: VaultItem,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = SurfaceDark.copy(alpha = 0.6f),
        borderColor = GlassBorder.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingMD),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                AppleBlue.copy(alpha = 0.3f),
                                AppleCyan.copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Wifi,
                    contentDescription = null,
                    tint = AppleBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(SpacingMD))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.ssid,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                if (item.remark.isNotBlank()) {
                    Text(
                        text = item.remark,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Text(
                text = formatTime(item.updatedAt),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
