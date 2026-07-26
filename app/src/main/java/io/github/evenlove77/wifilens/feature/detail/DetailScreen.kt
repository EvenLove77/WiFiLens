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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundDark)
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

                // ===== 密码测试区域 =====
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = SpacingMD),
                    backgroundColor = SurfaceDark.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(SpacingMD)) {
                        Text("弱密码测试", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${uiState.candidates.size} 个候选密码", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                        Spacer(modifier = Modifier.height(SpacingMD))

                        // 测试按钮
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(SpacingSM)
                        ) {
                            // 开始测试
                            Button(
                                onClick = { viewModel.startTest(context) },
                                enabled = !uiState.isTesting,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (uiState.isTesting) {
                                    CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(SpacingSM))
                                }
                                Text(if (uiState.isTesting) "测试中..." else "逐个测试", color = TextPrimary)
                            }

                            // 手动选择
                            OutlinedButton(
                                onClick = { viewModel.toggleTestCard() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(if (uiState.showTestCard) "隐藏列表" else "手动选择", color = AppleBlue)
                            }
                        }

                        // 测试进度
                        if (uiState.isTesting) {
                            Spacer(modifier = Modifier.height(SpacingMD))
                            LinearProgressIndicator(
                                progress = { uiState.testIndex.toFloat() / uiState.testTotal },
                                modifier = Modifier.fillMaxWidth(),
                                color = AppleBlue,
                                trackColor = GlassBorder,
                            )
                            Spacer(modifier = Modifier.height(SpacingSM))
                            Text(
                                "${uiState.testIndex} / ${uiState.testTotal}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        // 测试结果
                        uiState.testResult?.let { result ->
                            Spacer(modifier = Modifier.height(SpacingMD))
                            Text(
                                result,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (result.startsWith("密码已找到")) SuccessGreen else TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // 密码候选列表
                        AnimatedVisibility(visible = uiState.showTestCard || uiState.isTesting) {
                            Column {
                                Spacer(modifier = Modifier.height(SpacingMD))
                                Text("候选密码", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                                Spacer(modifier = Modifier.height(SpacingSM))

                                uiState.candidates.take(20).forEachIndexed { index, candidate ->
                                    val isCurrent = uiState.isTesting && uiState.testIndex == index + 1
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (isCurrent) AppleBlue.copy(alpha = 0.15f) else SurfaceVariant.copy(alpha = 0.3f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("password", candidate.password))
                                                Toast.makeText(context, "已复制: ${candidate.password}", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = SpacingMD, vertical = SpacingSM),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // 编号
                                        Text(
                                            "${index + 1}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextTertiary,
                                            modifier = Modifier.width(28.dp)
                                        )
                                        // 密码
                                        Text(
                                            candidate.password,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isCurrent) AppleBlue else TextPrimary,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.weight(1f)
                                        )
                                        // 标签
                                        Text(
                                            candidate.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextTertiary,
                                            fontSize = 10.sp
                                        )
                                        Spacer(modifier = Modifier.width(SpacingSM))
                                        Icon(
                                            Icons.Rounded.ContentCopy,
                                            "复制",
                                            tint = TextTertiary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    if (index < uiState.candidates.take(20).lastIndex) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }

                                if (uiState.candidates.size > 20) {
                                    Spacer(modifier = Modifier.height(SpacingSM))
                                    Text(
                                        "... 还有 ${uiState.candidates.size - 20} 个",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextTertiary
                                    )
                                }
                            }
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
