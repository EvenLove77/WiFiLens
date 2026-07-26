package io.github.evenlove77.wifilens.feature.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.evenlove77.wifilens.core.component.*
import io.github.evenlove77.wifilens.core.theme.*
import io.github.evenlove77.wifilens.data.model.WiFiNetwork

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: ScanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 权限请求 launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        viewModel.refreshPermissionState()
        if (granted) {
            viewModel.scan()
        }
    }

    // 首次进入自动检查权限并尝试加载缓存结果
    LaunchedEffect(Unit) {
        viewModel.refreshPermissionState()
        if (viewModel.uiState.value.hasPermission && viewModel.uiState.value.isWifiEnabled) {
            viewModel.scan()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ===== 标题栏 =====
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
                        text = "WiFiLens",
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "附近网络",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                // 扫描按钮
                IconButton(
                    onClick = {
                        if (!uiState.hasPermission) {
                            permissionLauncher.launch(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    arrayOf(
                                        Manifest.permission.NEARBY_WIFI_DEVICES,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                } else {
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                }
                            )
                        } else {
                            viewModel.scan()
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(AppleBlue, AppleCyan)
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "扫描",
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // ===== 扫描球区域 =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                LiquidGlassScanBall(
                    isScanning = uiState.isScanning,
                    ballSize = IconSizeScanBall
                )

                if (uiState.isScanning) {
                    Text(
                        text = "正在扫描...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    )
                } else if (uiState.errorMessage != null && uiState.networks.isEmpty()) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    )
                } else if (uiState.networks.isEmpty()) {
                    Text(
                        text = "点击右上角扫描附近网络",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary,
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingSM))

            // ===== WiFi 列表 =====
            if (uiState.networks.isNotEmpty()) {
                Text(
                    text = "${uiState.networks.size} 个网络",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = SpacingLG, vertical = SpacingSM)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = SpacingMD,
                        vertical = SpacingSM
                    ),
                    verticalArrangement = Arrangement.spacedBy(SpacingSM)
                ) {
                    items(
                        items = uiState.networks,
                        key = { it.bssid }
                    ) { network ->
                        WiFiNetworkCard(
                            network = network,
                            onClick = { onNavigateToDetail(network.ssid) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            } else {
                // 空状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.WifiFind,
                            contentDescription = null,
                            tint = TextTertiary.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(SpacingMD))
                        Text(
                            text = "暂无 WiFi 网络",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextTertiary
                        )
                    }
                }
            }
        }
    }
}

/**
 * WiFi 网络卡片
 */
@Composable
fun WiFiNetworkCard(
    network: WiFiNetwork,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundColor = SurfaceDark.copy(alpha = 0.6f),
        borderColor = GlassBorder.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingMD),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // WiFi 信号图标
            WifiSignalIcon(
                signalLevel = network.signalLevel,
                size = 28.dp
            )

            Spacer(modifier = Modifier.width(SpacingMD))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = network.ssid.ifBlank { "(隐藏网络)" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${network.band} · ${network.securityType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // 信号强度百分比
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${network.rssi} dBm",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${network.signalPercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        network.signalPercent >= 70 -> SignalExcellent
                        network.signalPercent >= 40 -> SignalFair
                        else -> SignalWeak
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(SpacingSM))
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
