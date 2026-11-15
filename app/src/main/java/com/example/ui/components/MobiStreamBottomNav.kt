package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MobiBottomNavShape
import com.example.ui.theme.MobiChipShape
import com.example.ui.theme.MobiStreamTheme
import com.example.ui.theme.mobiColors

enum class NavDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    EXPLORE("explore", "Explore", Icons.Filled.Explore, Icons.Outlined.Explore),
    FAVOURITE("favourite", "Favourite", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    PROFILE("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

/**
 * MobiBottomNav:
 * Floating pill (inset 14dp from the sides, 16dp from the bottom, 68dp high, fully rounded, elevation/shadow).
 * Tabs: Home, Explore, Favourite, Profile.
 */
@Composable
fun MobiBottomNav(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 14.dp, end = 14.dp, bottom = 16.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = MobiBottomNavShape,
                    spotColor = Color.Black.copy(alpha = if (mobi.isDark) 0.45f else 0.16f),
                    ambientColor = Color.Black.copy(alpha = if (mobi.isDark) 0.30f else 0.10f)
                ),
            shape = MobiBottomNavShape,
            color = mobi.navPill,
            border = BorderStroke(1.dp, colors.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination

                    Box(
                        modifier = Modifier
                            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                            .clip(MobiChipShape)
                            .background(if (isSelected) colors.primary else Color.Transparent)
                            .clickable { onNavigate(destination) }
                            .padding(horizontal = if (isSelected) 16.dp else 12.dp, vertical = 10.dp)
                            .testTag("nav_item_${destination.route}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.title,
                                tint = if (isSelected) colors.onPrimary else colors.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = destination.title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = colors.onPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Wrapper preserving existing call sites.
 */
@Composable
fun MobiStreamBottomNav(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    MobiBottomNav(
        currentDestination = currentDestination,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MobiBottomNavPreview() {
    MobiStreamTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MobiBottomNav(
                currentDestination = NavDestination.HOME,
                onNavigate = {}
            )
        }
    }
}
