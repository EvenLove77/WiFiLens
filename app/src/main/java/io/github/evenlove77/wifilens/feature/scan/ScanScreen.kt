package io.github.evenlove77.wifilens.feature.scan

import android.Manifest
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.evenlove77.wifilens.core.component.*
import kotlinx.coroutines.delay
import io.github.evenlove77.wifilens.core.theme.*
import io.github.evenlove77.wifilens.data.model.WiFiNetwork

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateToDetail: (ssid: String, bssid: String, rssi: Int, frequency: Int, capabilities: String) -> Unit,
    viewModel: ScanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.refreshState()
        if (permissions.values.all { it }) viewModel.scan()
    }

    LaunchedEffect(Unit) {
        viewModel.onScreenEnter()
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
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
                Column {
                    Text("WiFiLens", style = MaterialTheme.typography.displayMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(
                        when {
                            uiState.isTesting -> uiState.testProgress
                            uiState.networks.isNotEmpty() -> "附近 ${uiState.networks.size} 个网络"
                            uiState.isScanning -> "正在扫描..."
                            else -> "附近网络"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                // 全部测试按钮
                if (uiState.networks.isNotEmpty() && !uiState.isTesting) {
                    TextButton(
                        onClick = { viewModel.startFullTest(context) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = AppleBlue)
                    ) {
                        Icon(Icons.Rounded.PlayArrow, null, modifier = Modifier.size(18.dp), tint = AppleBlue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("全部测试", color = AppleBlue, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }
                }
            }

            // ===== 测试进度 =====
            AnimatedVisibility(visible = uiState.isTesting) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = SpacingMD, vertical = SpacingSM),
                    backgroundColor = SurfaceDark.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(SpacingMD)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(color = AppleBlue, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(SpacingSM))
                            Text(
                                "正在测试: ${uiState.testCurrentWifi}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { viewModel.stopTest() }) {
                                Text("停止", color = ErrorRed, fontWeight = FontWeight.Medium)
                            }
                        }
                        Spacer(modifier = Modifier.height(SpacingSM))
                        LinearProgressIndicator(
                            progress = { if (uiState.testTotalWifi > 0) uiState.testCurrentIndex.toFloat() / uiState.testTotalWifi else 0f },
                            modifier = Modifier.fillMaxWidth(),
                            color = AppleBlue,
                            trackColor = GlassBorder
                        )
                    }
                }
            }

            // ===== 测试结果 =====
            AnimatedVisibility(visible = uiState.testComplete && uiState.testResults.isNotEmpty()) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = SpacingMD, vertical = SpacingSM),
                    backgroundColor = AppleBlue.copy(alpha = 0.1f)
                ) {
                    Column(modifier = Modifier.padding(SpacingMD)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(SpacingSM))
                            Text("找到 ${uiState.testResults.size} 个弱密码", style = MaterialTheme.typography.bodyLarge, color = SuccessGreen, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.dismissTestResults() }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Rounded.Close, "关闭", tint = TextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(SpacingSM))
                        uiState.testResults.forEach { result ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(result.ssid, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                                Text(result.password, style = MaterialTheme.typography.bodyMedium, color = AppleBlue, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(SpacingSM))
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("password", result.password))
                                    },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Rounded.ContentCopy, "复制", tint = AppleBlue, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 测试完成但没找到
            AnimatedVisibility(visible = uiState.testComplete && uiState.testResults.isEmpty()) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = SpacingMD, vertical = SpacingSM),
                    backgroundColor = SurfaceDark.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(SpacingMD),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Shield, null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(SpacingSM))
                        Text("未发现弱密码 — 网络安全", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.dismissTestResults() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Rounded.Close, "关闭", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // ===== iOS 风格卡片逐个弹入动画 =====
            var visibleCount by remember { mutableIntStateOf(0) }
            LaunchedEffect(uiState.networks) {
                visibleCount = 0
                if (uiState.networks.isNotEmpty()) {
                    uiState.networks.forEachIndexed { index, _ ->
                        delay(80L)
                        visibleCount = index + 1
                    }
                }
            }

            // ===== 主内容区 =====
            if (uiState.networks.isNotEmpty()) {
                PullToRefreshBox(
                    isRefreshing = uiState.isScanning,
                    onRefresh = {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
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
                    modifier = Modifier.weight(1f),
                    indicator = { IosPullIndicator(isRefreshing = uiState.isScanning) }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = SpacingMD, vertical = 2.dp),
                        verticalArrangement = Arrangement.spacedBy(SpacingSM)
                    ) {
                        items(items = uiState.networks, key = { it.bssid }) { network ->
                            val index = uiState.networks.indexOf(network)
                            androidx.compose.animation.AnimatedVisibility(
                                visible = index < visibleCount,
                                enter = slideInVertically(
                                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                                    initialOffsetY = { it / 2 }
                                ) + fadeIn(animationSpec = tween(300))
                            ) {
                                WiFiNetworkCard(network = network, onClick = {
                                    onNavigateToDetail(network.ssid, network.bssid, network.rssi, network.frequency, network.capabilities)
                                })
                            }
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            } else {
                // 无数据：扫描球居中（带淡出动画）
                AnimatedVisibility(
                    visible = uiState.networks.isEmpty(),
                    exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.9f, animationSpec = tween(200))
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            LiquidGlassScanBall(isScanning = uiState.isScanning, ballSize = IconSizeScanBall)
                            Spacer(modifier = Modifier.height(SpacingLG))
                            if (uiState.isScanning) {
                                Text("正在扫描周围 WiFi...", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                            } else if (uiState.errorMessage != null) {
                                Text(uiState.errorMessage ?: "", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            } else {
                                Text("下拉刷新扫描附近 WiFi", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 100.dp), contentAlignment = Alignment.Center) {
                    GlassButton(text = "扫描附近 WiFi", onClick = {
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
                    }, enabled = !uiState.isScanning)
                }
            }
        }
    }
}

/**
 * iOS 风格三点下拉刷新指示器
 */
@Composable
private fun IosPullIndicator(isRefreshing: Boolean) {
    if (!isRefreshing) return

    val dots = remember { listOf(0, 1, 2) }
    val transition = rememberInfiniteTransition(label = "dots")

    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dots.forEach { index ->
            val scale by transition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, delayMillis = index * 150),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot$index"
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(AppleBlue)
            )
        }
    }
}

@Composable
fun WiFiNetworkCard(
    network: WiFiNetwork,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f),
        label = "cardScale"
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                        onClick()
                    }
                )
            },
        backgroundColor = SurfaceDark.copy(alpha = 0.6f),
        borderColor = GlassBorder.copy(alpha = 0.3f)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(SpacingMD), verticalAlignment = Alignment.CenterVertically) {
            WifiSignalIcon(signalLevel = network.signalLevel, size = 28.dp)
            Spacer(modifier = Modifier.width(SpacingMD))
            Column(modifier = Modifier.weight(1f)) {
                Text(network.ssid.ifBlank { "(隐藏网络)" }, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Medium)
                Text("${network.band} · ${network.securityType}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${network.rssi} dBm", style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontSize = 13.sp)
                Text("${network.signalPercent}%", style = MaterialTheme.typography.labelSmall,
                    color = when { network.signalPercent >= 70 -> SignalExcellent; network.signalPercent >= 40 -> SignalFair; else -> SignalWeak },
                    fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.width(SpacingSM))
            Icon(Icons.Rounded.ChevronRight, null, tint = TextTertiary, modifier = Modifier.size(20.dp))
        }
    }
}
