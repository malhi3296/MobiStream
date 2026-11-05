package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextMuted

enum class NavDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    EXPLORE("explore", "Explore", Icons.Filled.Explore, Icons.Outlined.Explore),
    FAVORITES("favorites", "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    SETTINGS("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun MobiStreamBottomNav(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MidnightCardBorder),
        color = MidnightBackground,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavDestination.values().forEach { destination ->
                val isSelected = currentDestination == destination

                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) Color.White else TextMuted,
                    label = "navIconTint"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) GlowLilac else TextMuted,
                    label = "navTextTint"
                )

                Box(
                    modifier = Modifier
                        .testTag("nav_item_${destination.route}")
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(destination) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) StreamViolet.copy(alpha = 0.35f) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.title,
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = destination.title,
                            fontFamily = InterFontFamily,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 11.sp,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}
