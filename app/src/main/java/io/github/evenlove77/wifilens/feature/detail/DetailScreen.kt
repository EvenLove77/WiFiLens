package io.github.evenlove77.wifilens.feature.detail

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.evenlove77.wifilens.core.component.*
import io.github.evenlove77.wifilens.core.theme.*
import io.github.evenlove77.wifilens.data.model.WiFiNetwork

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    ssid: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Currently showing mock data for demo
    // When navigating from ScanScreen with real data, we'll pass the WiFiNetwork object
    LaunchedEffect(ssid) {
        if (uiState.network == null) {
            // Load mock detail for demo
            viewModel.loadNetwork(
                WiFiNetwork(
                    ssid = ssid,
                    bssid = "AA:BB:CC:DD:EE:FF",
                    rssi = -48,
                    frequency = 5180,
                    capabilities = "[WPA2-PSK-CCMP][RSN-PSK+SAE-CCMP][ESS]"
                )
            )
        }
    }

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
            // ===== 顶部导航栏 =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingSM, vertical = SpacingSM)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "返回",
                        tint = AppleBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(SpacingSM))
                Text(
                    text = "WiFi 详情",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
            }

            uiState.network?.let { network ->
                // ===== 信号可视化区域 =====
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SpacingXL),
                    contentAlignment = Alignment.Center
                ) {
                    // 大 WiFi 图标
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        WifiSignalIconLarge(
                            signalLevel = network.signalLevel,
                            size = 64.dp
                        )
                        Spacer(modifier = Modifier.height(SpacingMD))
                        Text(
                            text = network.ssid,
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${network.signalPercent}% · ${network.rssi} dBm",
                            style = MaterialTheme.typography.bodyLarge,
                            color = when {
                                network.signalPercent >= 70 -> SignalExcellent
                                network.signalPercent >= 40 -> SignalFair
                                else -> SignalWeak
                            }
                        )
                    }
                }

                // ===== 网络信息卡片 =====
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpacingMD),
                    backgroundColor = SurfaceDark.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(SpacingMD)) {
                        DetailRow("SSID", network.ssid)
                        DetailDivider()
                        DetailRow("BSSID", network.bssid)
                        DetailDivider()
                        DetailRow("信号强度", "${network.rssi} dBm (${network.signalPercent}%)")
                        DetailDivider()
                        DetailRow("频段", network.band)
                        DetailDivider()
                        DetailRow("频道", "CH ${network.channel}")
                        DetailDivider()
                        DetailRow("频率", "${network.frequency} MHz")
                        DetailDivider()
                        DetailRow("安全类型", network.securityType)
                    }
                }

                Spacer(modifier = Modifier.height(SpacingXL))

                // ===== 验证按钮 =====
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    GlassButton(
                        text = "开始检测",
                        onClick = { viewModel.startVerification() },
                        enabled = !uiState.isVerifying
                    )
                }

                // 验证状态
                AnimatedVisibility(visible = uiState.isVerifying) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpacingLG),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppleBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(SpacingXXL))
            } ?: run {
                // 加载中
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppleBlue)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpacingSM),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun DetailDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(GlassBorder.copy(alpha = 0.2f))
    )
}
