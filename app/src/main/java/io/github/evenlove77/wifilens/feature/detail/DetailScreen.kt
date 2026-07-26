package io.github.evenlove77.wifilens.feature.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.evenlove77.wifilens.core.component.*
import io.github.evenlove77.wifilens.core.theme.*
import io.github.evenlove77.wifilens.data.model.WiFiNetwork

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    ssid: String,
    bssid: String,
    rssi: Int,
    frequency: Int,
    capabilities: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(ssid) {
        viewModel.load(ssid, bssid, rssi, frequency, capabilities)
    }

    // 右滑返回
    var dragOffset by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragOffset > 120f) onNavigateBack()
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            // 顶部导航
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingSM, vertical = SpacingSM)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Rounded.ArrowBackIosNew, "返回", tint = AppleBlue)
                }
                Text("WiFi 详情", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            }

            uiState.network?.let { network ->
                // 信号展示
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = SpacingXL),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        WifiSignalIconLarge(signalLevel = network.signalLevel, size = 64.dp)
                        Spacer(modifier = Modifier.height(SpacingMD))
                        Text(network.ssid, style = MaterialTheme.typography.headlineLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("${network.signalPercent}% · ${network.rssi} dBm", style = MaterialTheme.typography.bodyLarge,
                            color = when { network.signalPercent >= 70 -> SignalExcellent; network.signalPercent >= 40 -> SignalFair; else -> SignalWeak })
                    }
                }

                // 网络信息
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = SpacingMD),
                    backgroundColor = SurfaceDark.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(SpacingMD)) {
                        DetailRow("SSID", network.ssid)
                        DetailDivider()
                        DetailRow("BSSID", network.bssid)
                        DetailDivider()
                        DetailRow("信号", "${network.rssi} dBm (${network.signalPercent}%)")
                        DetailDivider()
                        DetailRow("频段", network.band)
                        DetailDivider()
                        DetailRow("频道", "CH ${network.channel}")
                        DetailDivider()
                        DetailRow("频率", "${network.frequency} MHz")
                        DetailDivider()
                        DetailRow("安全", network.securityType)
                    }
                }

                Spacer(modifier = Modifier.height(SpacingLG))

                // ===== 密码推荐区域 =====
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = SpacingMD),
                    backgroundColor = SurfaceDark.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(SpacingMD)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Key, null, tint = AppleBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(SpacingSM))
                            Text("最可能的密码", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("点击复制，去系统 WiFi 设置粘贴试试", style = MaterialTheme.typography.bodySmall, color = TextTertiary)

                        Spacer(modifier = Modifier.height(SpacingMD))

                        uiState.candidates.take(10).forEachIndexed { index, candidate ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceVariant.copy(alpha = 0.4f))
                                    .clickable {
                                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cm.setPrimaryClip(ClipData.newPlainText("pwd", candidate.password))
                                        Toast.makeText(context, "已复制: ${candidate.password}", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = SpacingMD, vertical = SpacingSM),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${index + 1}", style = MaterialTheme.typography.labelMedium, color = TextTertiary, modifier = Modifier.width(24.dp))
                                Text(candidate.password, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                Text(candidate.label, style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(SpacingSM))
                                Icon(Icons.Rounded.ContentCopy, "复制", tint = AppleBlue, modifier = Modifier.size(16.dp))
                            }
                            if (index < uiState.candidates.take(10).lastIndex) {
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }

                        if (uiState.candidates.size > 10) {
                            Spacer(modifier = Modifier.height(SpacingSM))
                            Text("... 还有 ${uiState.candidates.size - 10} 个候选密码", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpacingXXL))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = SpacingSM),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.weight(0.35f))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium, modifier = Modifier.weight(0.65f))
    }
}

@Composable
private fun DetailDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(GlassBorder.copy(alpha = 0.2f)))
}
