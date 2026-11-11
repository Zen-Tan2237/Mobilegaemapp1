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
import androidx.compose.ui.unit.dp

/** Shared glass look: transparent layered fill and soft edges (not a backdrop blur). */
@Composable
fun Modifier.glassSurface(cornerRadius: Int = 24): Modifier {
    val dark = MaterialTheme.colorScheme.background.red < 0.2f
    val shape = RoundedCornerShape(cornerRadius.dp)
    val top = if (dark) Color.White.copy(alpha = 0.19f) else Color.White.copy(alpha = 0.84f)
    val bottom = if (dark) Color(0xFF9CCBFF).copy(alpha = 0.07f)
                 else Color(0xFFBBD5F3).copy(alpha = 0.36f)
    return this
        .clip(shape)
        .background(Brush.linearGradient(listOf(top, bottom)))
        .border(BorderStroke(1.dp, if (dark) Color.White.copy(alpha = 0.22f)
                            else Color.White.copy(alpha = 0.95f)), shape)
}

@Composable
fun GlassBackground(content: @Composable BoxScope.() -> Unit) {
    val dark = MaterialTheme.colorScheme.background.red < 0.2f
    val palette = if (dark) listOf(Color(0xFF06101C), Color(0xFF163556), Color(0xFF302B58))
                  else listOf(Color(0xFFDDEFFF), Color(0xFFF2F7FF), Color(0xFFDADCF9))
    Box(Modifier.fillMaxSize().background(Brush.linearGradient(palette)), content = content)
}
