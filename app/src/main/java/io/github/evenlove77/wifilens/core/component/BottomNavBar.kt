package io.github.evenlove77.wifilens.core.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.evenlove77.wifilens.core.theme.*

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("扫描", Icons.Rounded.WifiFind, "scan"),
    BottomNavItem("设置", Icons.Rounded.Settings, "settings"),
)

@Composable
fun AppleBottomNavBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(GlassBackground),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                val color by animateColorAsState(
                    targetValue = if (selected) AppleBlue else TextTertiary,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                    label = "navColor"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onItemClick(item.route) }
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = item.title,
                        color = color,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        letterSpacing = 0.sp
                    )
                    if (selected) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(AppleBlue, AppleCyan)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}
