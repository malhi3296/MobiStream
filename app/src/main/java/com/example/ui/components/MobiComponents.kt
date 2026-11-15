package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.data.model.Channel
import com.example.domain.ChannelLogoResolver
import com.example.ui.theme.MobiButtonShape
import com.example.ui.theme.MobiCardShape
import com.example.ui.theme.MobiChipShape
import com.example.ui.theme.MobiSearchShape
import com.example.ui.theme.MobiStreamTheme
import com.example.ui.theme.mobiColors
import kotlin.math.abs

/**
 * Maps language names to their characteristic native script glyph/letter for filter chips.
 */
fun getLanguageGlyph(languageName: String): String? {
    val clean = languageName.trim()
    if (clean.isBlank() || clean.equals("All", ignoreCase = true)) return null
    val lower = clean.lowercase()
    return when {
        lower.contains("english") -> "Aa"
        lower.contains("hindi") -> "अ"
        lower.contains("punjabi") -> "ਅ"
        lower.contains("urdu") -> "ا"
        lower.contains("tamil") -> "அ"
        lower.contains("telugu") -> "అ"
        lower.contains("bengali") || lower.contains("bangla") -> "অ"
        lower.contains("marathi") -> "म"
        lower.contains("gujarati") -> "ગુ"
        lower.contains("kannada") -> "ಅ"
        lower.contains("malayalam") -> "അ"
        lower.contains("arabic") -> "ض"
        lower.contains("spanish") -> "Ñ"
        lower.contains("french") -> "Ç"
        lower.contains("german") -> "Ü"
        lower.contains("russian") -> "Я"
        lower.contains("japanese") -> "あ"
        lower.contains("korean") -> "한"
        lower.contains("chinese") || lower.contains("mandarin") -> "中"
        lower.contains("portuguese") -> "Ã"
        lower.contains("italian") -> "È"
        lower.contains("turkish") -> "Ş"
        lower.contains("persian") || lower.contains("farsi") -> "ف"
        lower.contains("polish") -> "Ł"
        lower.contains("ukrainian") -> "Ї"
        lower.contains("greek") -> "Ω"
        lower.contains("thai") -> "ก"
        lower.contains("vietnamese") -> "Đ"
        else -> clean.take(2)
    }
}

/**
 * Dual-mode legible Channel Logo frame that makes both white and dark transparent logos
 * clearly visible in Light and Dark modes, and falls back to Country Flag + Channel Name.
 */
@Composable
fun ChannelLogoBox(
    channel: Channel,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors
    val resolvedLogo = ChannelLogoResolver.resolve(channel.name, channel.logoUrl)
    // Balanced cool slate backdrop so both black and white transparent PNG logos remain readable
    val logoBackdrop = if (mobi.isDark) Color(0xFF5C6F8E) else Color(0xFFA9BDD8)

    Box(
        modifier = modifier
            .clip(shape)
            .border(BorderStroke(0.75.dp, colors.outline), shape),
        contentAlignment = Alignment.Center
    ) {
        if (resolvedLogo.isNotBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(resolvedLogo)
                    .crossfade(true)
                    .build(),
                contentDescription = "${channel.name} logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    LogoFallbackPlaceholder(channel = channel, compact = compact)
                },
                error = {
                    LogoFallbackPlaceholder(channel = channel, compact = compact)
                },
                success = {
                    val imageScope = this
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(logoBackdrop)
                            .padding(horizontal = if (compact) 6.dp else 10.dp, vertical = if (compact) 4.dp else 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        imageScope.SubcomposeAsyncImageContent(modifier = Modifier.fillMaxSize())
                    }
                }
            )
        } else {
            LogoFallbackPlaceholder(channel = channel, compact = compact)
        }
    }
}

@Composable
private fun LogoFallbackPlaceholder(
    channel: Channel,
    compact: Boolean
) {
    val mobi = MaterialTheme.mobiColors
    val tileColor = mobi.channelTileColors[abs(channel.name.hashCode()) % mobi.channelTileColors.size]
    val flag = getCountryFlag(channel.countryCode)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tileColor)
            .padding(horizontal = if (compact) 6.dp else 10.dp, vertical = if (compact) 4.dp else 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = flag,
                fontSize = if (compact) 13.sp else 18.sp,
                lineHeight = if (compact) 15.sp else 20.sp
            )
            Spacer(modifier = Modifier.height(if (compact) 1.dp else 3.dp))
            Text(
                text = channel.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 10.sp else 12.sp,
                lineHeight = if (compact) 12.sp else 15.sp,
                textAlign = TextAlign.Center,
                maxLines = if (compact) 1 else 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Shared SectionHeader with optional "See all" action in accentText color.
 */
@Composable
fun MobiSectionHeader(
    title: String,
    actionText: String? = "See all",
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = colors.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (actionText != null && onActionClick != null) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                    .clip(MobiChipShape)
                    .clickable { onActionClick() }
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = mobi.accentText
                )
            }
        }
    }
}

/**
 * Shared Search Pill + Round Primary Menu Button (used on Home & Explore).
 */
@Composable
fun MobiSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    hint: String,
    onMenuClick: (() -> Unit)? = null,
    onBarClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 50.dp)
                .clip(MobiSearchShape)
                .then(if (onBarClick != null) Modifier.clickable { onBarClick() } else Modifier),
            shape = MobiSearchShape,
            color = colors.surface,
            border = BorderStroke(1.dp, colors.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))

                if (onBarClick != null) {
                    Text(
                        text = if (query.isNotBlank()) query else hint,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (query.isNotBlank()) colors.onSurface else colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = hint,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.onSurface),
                            cursorBrush = SolidColor(colors.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_bar_input")
                        )
                    }
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = colors.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        if (onMenuClick != null) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colors.primary)
                    .testTag("search_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * Shared Chip: Selected = primary fill + onPrimary text; Unselected = surface + outline border.
 */
@Composable
fun MobiChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingGlyph: String? = null,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(MobiChipShape)
            .clickable { onClick() },
        shape = MobiChipShape,
        color = if (selected) colors.primary else colors.surface,
        border = BorderStroke(1.dp, if (selected) colors.primary else colors.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingGlyph != null) {
                Text(
                    text = leadingGlyph,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) colors.onPrimary else colors.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
            } else if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (selected) colors.onPrimary else colors.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) colors.onPrimary else colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Shared Card with 18dp radius, surface color, and 1dp outline border.
 */
@Composable
fun MobiCard(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier
            .clip(MobiCardShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = MobiCardShape,
        color = if (selected) colors.secondaryContainer else colors.surface,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) colors.primary else colors.outline
        )
    ) {
        Column(content = content)
    }
}

enum class MobiButtonStyle {
    PRIMARY,
    TONAL
}

/**
 * Shared Button (16dp radius, minimum 48dp touch height).
 */
@Composable
fun MobiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: MobiButtonStyle = MobiButtonStyle.PRIMARY,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val containerColor = when (style) {
        MobiButtonStyle.PRIMARY -> colors.primary
        MobiButtonStyle.TONAL -> colors.secondaryContainer
    }
    val contentColor = when (style) {
        MobiButtonStyle.PRIMARY -> colors.onPrimary
        MobiButtonStyle.TONAL -> colors.onSecondaryContainer
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MobiButtonShape,
        border = if (style == MobiButtonStyle.TONAL) BorderStroke(1.dp, colors.outline) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        modifier = modifier.defaultMinSize(minHeight = 48.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Shared Toggle Switch styled with MobiStream tokens.
 */
@Composable
fun MobiSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = colors.onPrimary,
            checkedTrackColor = colors.primary,
            uncheckedThumbColor = colors.onSurfaceVariant,
            uncheckedTrackColor = colors.surfaceVariant,
            uncheckedBorderColor = colors.outline
        )
    )
}

/**
 * Shared LIVE Badge (#E0263F with crisp white text).
 */
@Composable
fun LiveBadge(
    text: String = "LIVE",
    modifier: Modifier = Modifier
) {
    val mobi = MaterialTheme.mobiColors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(mobi.liveBadge)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = Color.White,
            maxLines = 1
        )
    }
}

/**
 * Shared channel list row with wide full-contrast ChannelLogoBox and Favorite toggle.
 */
@Composable
fun ChannelTile(
    channel: Channel,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    showLiveBadge: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    MobiCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("channel_tile_${channel.id}"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChannelLogoBox(
                channel = channel,
                compact = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(92.dp)
                    .height(54.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (showLiveBadge || channel.isLive) {
                        LiveBadge(text = "LIVE")
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                val subtitleParts = listOfNotNull(
                    channel.category.takeIf { it.isNotBlank() },
                    channel.country.takeIf { it.isNotBlank() }?.let { "${getCountryFlag(channel.countryCode)} $it" },
                    channel.quality.takeIf { it.isNotBlank() }
                )
                Text(
                    text = subtitleParts.joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("fav_btn_${channel.id}")
            ) {
                Icon(
                    imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (channel.isFavorite) "Remove from Favorites" else "Add to Favorites",
                    tint = if (channel.isFavorite) mobi.liveBadge else colors.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SharedComponentsPreview() {
    MobiStreamTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MobiSearchBar(
                query = "",
                onQueryChange = {},
                hint = "Search 1000+ live channels, sports, news...",
                onMenuClick = {}
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MobiChip(label = "Hindi", leadingGlyph = getLanguageGlyph("Hindi"), selected = true, onClick = {})
                MobiChip(label = "India", leadingGlyph = getCountryFlag("IN"), selected = false, onClick = {})
            }
            ChannelTile(
                channel = Channel(
                    id = "bbc",
                    name = "BBC World News",
                    logoUrl = "",
                    streamUrl = "",
                    category = "News",
                    country = "United Kingdom",
                    countryCode = "GB",
                    language = "English",
                    quality = "FHD",
                    isLive = true,
                    isFavorite = true
                ),
                onClick = {},
                onToggleFavorite = {},
                showLiveBadge = true
            )
        }
    }
}
