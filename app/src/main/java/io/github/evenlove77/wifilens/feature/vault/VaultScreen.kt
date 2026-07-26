package io.github.evenlove77.wifilens.feature.vault

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.evenlove77.wifilens.core.component.GlassCard
import io.github.evenlove77.wifilens.core.theme.*
import io.github.evenlove77.wifilens.data.model.VaultItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun VaultScreen(
    viewModel: VaultViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = listOf(null, "常见弱密码", "品牌默认密码", "我的WiFi")

    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundDark)
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
            }

            // ===== 分类筛选 =====
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = SpacingMD),
                horizontalArrangement = Arrangement.spacedBy(SpacingSM)
            ) {
                categories.forEach { category ->
                    val selected = uiState.selectedCategory == category
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.selectCategory(category) },
                        label = {
                            Text(category ?: "全部", fontSize = 13.sp, color = if (selected) TextPrimary else TextSecondary)
                        },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AppleBlue.copy(alpha = 0.3f)),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selected) AppleBlue.copy(alpha = 0.5f) else GlassBorder,
                            selectedBorderColor = AppleBlue.copy(alpha = 0.5f), enabled = true, selected = selected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingMD))

            // ===== 列表 =====
            if (uiState.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.WifiLock, null, tint = TextTertiary.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(SpacingMD))
                        Text("暂无保存的 WiFi 配置", style = MaterialTheme.typography.bodyLarge, color = TextTertiary)
                        Spacer(modifier = Modifier.height(SpacingSM))
                        Text("点击右下角 + 按钮添加", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = SpacingMD, vertical = SpacingSM),
                    verticalArrangement = Arrangement.spacedBy(SpacingSM)
                ) {
                    items(items = uiState.items, key = { it.id }) { item ->
                        VaultItemCard(
                            item = item,
                            onClick = { viewModel.startEdit(item) },
                            onDelete = { viewModel.deleteItem(item.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // ===== FAB =====
        FloatingActionButton(
            onClick = { viewModel.startAdd() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(SpacingLG),
            containerColor = AppleBlue, shape = CircleShape
        ) {
            Icon(Icons.Rounded.Add, "添加", tint = TextPrimary)
        }
    }

    // ===== 添加/编辑弹窗 =====
    if (uiState.isEditing && uiState.editingItem != null) {
        EditVaultDialog(
            item = uiState.editingItem!!,
            isNew = uiState.editingItem!!.id == 0L,
            onSave = { viewModel.saveItem(it) },
            onDismiss = { viewModel.cancelEdit() }
        )
    }
}

@Composable
private fun VaultItemCard(
    item: VaultItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDelete by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = { showDelete = true }),
        backgroundColor = SurfaceDark.copy(alpha = 0.6f),
        borderColor = GlassBorder.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(SpacingMD),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(AppleBlue.copy(alpha = 0.3f), AppleCyan.copy(alpha = 0.3f)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Wifi, null, tint = AppleBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(SpacingMD))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.ssid, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Medium)
                if (item.remark.isNotBlank()) {
                    Text(item.remark, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Text(
                    item.category, style = MaterialTheme.typography.labelSmall,
                    color = AppleBlue.copy(alpha = 0.7f), fontSize = 11.sp
                )
            }
            Text(
                formatTime(item.updatedAt),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }

    // 删除确认
    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            containerColor = SurfaceDark,
            title = { Text("删除配置", color = TextPrimary) },
            text = { Text("确定删除「${item.ssid}」？", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDelete = false }) {
                    Text("删除", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditVaultDialog(
    item: VaultItem,
    isNew: Boolean,
    onSave: (VaultItem) -> Unit,
    onDismiss: () -> Unit
) {
    var ssid by remember { mutableStateOf(item.ssid) }
    var password by remember { mutableStateOf(item.password) }
    var remark by remember { mutableStateOf(item.remark) }
    var category by remember { mutableStateOf(item.category) }
    val categories = listOf("常见弱密码", "品牌默认密码", "我的WiFi")

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = SurfaceDark,
            borderColor = GlassBorder
        ) {
            Column(modifier = Modifier.padding(SpacingLG)) {
                Text(
                    if (isNew) "添加 WiFi 配置" else "编辑 WiFi 配置",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(SpacingLG))

                // SSID
                OutlinedTextField(
                    value = ssid, onValueChange = { ssid = it },
                    label = { Text("WiFi 名称 (SSID)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(SpacingMD))

                // 密码
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("密码") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    visualTransformation = PasswordVisualTransformation(),
                    trailingIcon = {
                        var visible by remember { mutableStateOf(false) }
                        IconButton(onClick = { visible = !visible }) {
                            Icon(
                                if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                "显示", tint = TextSecondary
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(SpacingMD))

                // 备注
                OutlinedTextField(
                    value = remark, onValueChange = { remark = it },
                    label = { Text("备注") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(SpacingMD))

                // 分类
                Row(horizontalArrangement = Arrangement.spacedBy(SpacingSM)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppleBlue.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(SpacingLG))

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(SpacingSM))
                    TextButton(
                        onClick = {
                            if (ssid.isNotBlank()) {
                                onSave(item.copy(ssid = ssid, password = password, remark = remark, category = category))
                            }
                        },
                        enabled = ssid.isNotBlank()
                    ) {
                        Text("保存", color = if (ssid.isNotBlank()) AppleBlue else TextTertiary)
                    }
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = AppleBlue,
    unfocusedBorderColor = GlassBorder,
    focusedLabelColor = AppleBlue,
    unfocusedLabelColor = TextSecondary,
    cursorColor = AppleBlue
)

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
