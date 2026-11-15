package com.example.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val MobiCardShape = RoundedCornerShape(18.dp)
val MobiButtonShape = RoundedCornerShape(16.dp)
val MobiChipShape = CircleShape
val MobiSearchShape = CircleShape
val MobiIconButtonShape = RoundedCornerShape(14.dp)
val MobiBottomNavShape = RoundedCornerShape(34.dp)

val MobiShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = MobiIconButtonShape,
    medium = MobiCardShape,
    large = RoundedCornerShape(24.dp),
    extraLarge = MobiBottomNavShape
)
