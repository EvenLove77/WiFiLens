package io.github.evenlove77.wifilens.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.evenlove77.wifilens.core.component.GlassCard
import io.github.evenlove77.wifilens.core.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ===== 标题 =====
            Text(
                text = "设置",
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingLG, vertical = SpacingMD)
                    .statusBarsPadding()
            )

            Spacer(modifier = Modifier.height(SpacingSM))

            // ===== 主题设置 =====
            SectionHeader("外观")
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingMD),
                backgroundColor = SurfaceDark.copy(alpha = 0.6f)
            ) {
                Column(modifier = Modifier.padding(SpacingMD)) {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setThemeMode(mode) }
                                .padding(vertical = SpacingSM),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary
                            )
                            RadioButton(
                                selected = uiState.themeMode == mode,
                                onClick = { viewModel.setThemeMode(mode) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = AppleBlue,
                                    unselectedColor = TextTertiary
                                )
                            )
                        }
                        if (mode != ThemeMode.entries.last()) {
                            SettingDivider()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpacingXL))

            // ===== 数据管理 =====
            SectionHeader("数据")
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingMD),
                backgroundColor = SurfaceDark.copy(alpha = 0.6f)
            ) {
                Column(modifier = Modifier.padding(SpacingMD)) {
                    SettingRow(
                        icon = Icons.Rounded.FileUpload,
                        title = "导入数据",
                        subtitle = "导入 WiFi 配置备份"
                    )
                    SettingDivider()
                    SettingRow(
                        icon = Icons.Rounded.FileDownload,
                        title = "导出数据",
                        subtitle = "导出所有 WiFi 配置"
                    )
                    SettingDivider()
                    SettingRow(
                        icon = Icons.Rounded.DeleteOutline,
                        title = "清除数据",
                        subtitle = "删除所有历史记录和配置",
                        destructive = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingXL))

            // ===== 关于 =====
            SectionHeader("关于")
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingMD),
                backgroundColor = SurfaceDark.copy(alpha = 0.6f)
            ) {
                Column(modifier = Modifier.padding(SpacingMD)) {
                    SettingRow(
                        icon = Icons.Rounded.Info,
                        title = "WiFiLens",
                        subtitle = "版本 ${uiState.dataVersion}"
                    )
                    SettingDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EvenLove77/WiFiLens"))
                                context.startActivity(intent)
                            }
                            .padding(vertical = SpacingSM),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AppleBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Code,
                                contentDescription = null,
                                tint = AppleBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(SpacingMD))
                        Column {
                            Text(
                                text = "GitHub",
                                style = MaterialTheme.typography.bodyLarge,
                                color = AppleBlue
                            )
                            Text(
                                text = "开源代码仓库",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = TextSecondary,
        modifier = Modifier.padding(horizontal = SpacingLG, vertical = SpacingSM)
    )
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    destructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 功能后续实现 */ }
            .padding(vertical = SpacingSM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (destructive) ErrorRed.copy(alpha = 0.2f)
                    else AppleBlue.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (destructive) ErrorRed else AppleBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(SpacingMD))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (destructive) ErrorRed else TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SettingDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(GlassBorder.copy(alpha = 0.2f))
    )
}
