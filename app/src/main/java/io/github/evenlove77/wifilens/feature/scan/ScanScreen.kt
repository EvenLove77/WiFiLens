package io.github.evenlove77.wifilens.feature.scan

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.evenlove77.wifilens.core.component.*
import io.github.evenlove77.wifilens.core.theme.*
import io.github.evenlove77.wifilens.data.model.WiFiNetwork

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateToDetail: (ssid: String, bssid: String, rssi: Int, frequency: Int, capabilities: String) -> Unit,
    viewModel: ScanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.refreshState()
        if (permissions.values.all { it }) viewModel.scan()
    }

    LaunchedEffect(Unit) {
        viewModel.onScreenEnter()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ===== 标题栏（简洁，无按钮） =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingLG, vertical = SpacingMD)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "WiFiLens",
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (uiState.networks.isNotEmpty()) "附近 ${uiState.networks.size} 个网络"
                        else if (uiState.isScanning) "正在扫描..."
                        else "附近网络",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            // 有数据：下拉刷新列表
            if (uiState.networks.isNotEmpty()) {
                PullToRefreshBox(
                    isRefreshing = uiState.isScanning,
                    onRefresh = {
                        if (!uiState.hasPermission) {
                            val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES, Manifest.permission.ACCESS_FINE_LOCATION)
                            } else {
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                            }
                            permissionLauncher.launch(perms)
                        } else {
                            viewModel.scan()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = SpacingMD, vertical = SpacingSM),
                        verticalArrangement = Arrangement.spacedBy(SpacingSM)
                    ) {
                        items(items = uiState.networks, key = { it.bssid }) { network ->
                            WiFiNetworkCard(
                                network = network,
                                onClick = {
                                    onNavigateToDetail(network.ssid, network.bssid, network.rssi, network.frequency, network.capabilities)
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            } else {
                // 无数据：扫描球居中
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LiquidGlassScanBall(isScanning = uiState.isScanning, ballSize = IconSizeScanBall)
                        Spacer(modifier = Modifier.height(SpacingLG))
                        if (uiState.isScanning) {
                            Text("正在扫描周围 WiFi...", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                        } else if (uiState.errorMessage != null) {
                            Text(uiState.errorMessage ?: "", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        } else {
                            Text("下拉或点击下方刷新", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
                        }
                    }
                }

                // 无数据时显示一个手动扫描按钮
                Box(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassButton(
                        text = "扫描附近 WiFi",
                        onClick = {
                            if (!uiState.hasPermission) {
                                val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES, Manifest.permission.ACCESS_FINE_LOCATION)
                                } else {
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                }
                                permissionLauncher.launch(perms)
                            } else {
                                viewModel.scan()
                            }
                        },
                        enabled = !uiState.isScanning
                    )
                }
            }
        }
    }
}

@Composable
fun WiFiNetworkCard(
    network: WiFiNetwork,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        backgroundColor = SurfaceDark.copy(alpha = 0.6f),
        borderColor = GlassBorder.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(SpacingMD),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WifiSignalIcon(signalLevel = network.signalLevel, size = 28.dp)
            Spacer(modifier = Modifier.width(SpacingMD))
            Column(modifier = Modifier.weight(1f)) {
                Text(network.ssid.ifBlank { "(隐藏网络)" }, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Medium)
                Text("${network.band} · ${network.securityType}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${network.rssi} dBm", style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontSize = 13.sp)
                Text("${network.signalPercent}%", style = MaterialTheme.typography.labelSmall, color = when {
                    network.signalPercent >= 70 -> SignalExcellent; network.signalPercent >= 40 -> SignalFair; else -> SignalWeak
                }, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.width(SpacingSM))
            Icon(Icons.Rounded.ChevronRight, null, tint = TextTertiary, modifier = Modifier.size(20.dp))
        }
    }
}
