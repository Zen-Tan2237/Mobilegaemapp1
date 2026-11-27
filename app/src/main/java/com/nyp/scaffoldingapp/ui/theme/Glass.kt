package com.nyp.scaffoldingapp.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

/** Shared glass look: transparent layered fill and soft edges. */
@Composable
fun Modifier.glassSurface(cornerRadius: Int = 24): Modifier {
    val colours = MaterialTheme.colorScheme
    val dark = colours.background.luminance() < 0.5f

    val shape = RoundedCornerShape(cornerRadius.dp)

    val top = Color.White.copy(
        alpha = if (dark) 0.12f else 0.65f
    )

    val bottom = colours.primary.copy(
        alpha = if (dark) 0.04f else 0.08f
    )

    val borderColour = Color.White.copy(
        alpha = if (dark) 0.16f else 0.85f
    )

    return this
        .clip(shape)
        .background(
            Brush.linearGradient(listOf(top, bottom))
        )
        .border(
            BorderStroke(1.dp, borderColour),
            shape
        )
}

@Composable
fun GlassBackground(
    content: @Composable BoxScope.() -> Unit
) {
    val colours = MaterialTheme.colorScheme

    val palette = listOf(
        colours.background,
        colours.surface,
        colours.surfaceVariant
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(palette)),
        content = content
    )
}
