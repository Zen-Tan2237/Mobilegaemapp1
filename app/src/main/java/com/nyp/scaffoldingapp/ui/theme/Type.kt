package com.nyp.scaffoldingapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.nyp.scaffoldingapp.R
import androidx.compose.ui.text.font.Font

val InterFontFamily = FontFamily(
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_regular, FontWeight.Normal)
)

// Set of Material typography styles to start with
private val DefaultTypography = Typography()

val Typography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(
        fontFamily = InterFontFamily
    ),
    displayMedium = DefaultTypography.displayMedium.copy(
        fontFamily = InterFontFamily
    ),
    displaySmall = DefaultTypography.displaySmall.copy(
        fontFamily = InterFontFamily
    ),

    headlineLarge = DefaultTypography.headlineLarge.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = DefaultTypography.headlineMedium.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold
    ),
    headlineSmall = DefaultTypography.headlineSmall.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold
    ),

    titleLarge = DefaultTypography.titleLarge.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = DefaultTypography.titleMedium.copy(
        fontFamily = InterFontFamily
    ),
    titleSmall = DefaultTypography.titleSmall.copy(
        fontFamily = InterFontFamily
    ),

    bodyLarge = DefaultTypography.bodyLarge.copy(
        fontFamily = InterFontFamily
    ),
    bodyMedium = DefaultTypography.bodyMedium.copy(
        fontFamily = InterFontFamily
    ),
    bodySmall = DefaultTypography.bodySmall.copy(
        fontFamily = InterFontFamily
    ),

    labelLarge = DefaultTypography.labelLarge.copy(
        fontFamily = InterFontFamily
    ),
    labelMedium = DefaultTypography.labelMedium.copy(
        fontFamily = InterFontFamily
    ),
    labelSmall = DefaultTypography.labelSmall.copy(
        fontFamily = InterFontFamily
    )
)

