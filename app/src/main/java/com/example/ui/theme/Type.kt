package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.example.R

val JetBrainsMonoFontFamily = FontFamily(
    Font(R.font.jetbrains_mono, FontWeight.Normal)
)

val UnboundedFontFamily = FontFamily(
    Font(R.font.unbounded, FontWeight.Bold)
)

private val DefaultLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both
)

private val DefaultPlatformTextStyle = PlatformTextStyle(
    includeFontPadding = false
)

val MaxoTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = UnboundedFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 36.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.02).sp,
        platformStyle = DefaultPlatformTextStyle,
        lineHeightStyle = DefaultLineHeightStyle
    ),
    displayMedium = TextStyle(
        fontFamily = UnboundedFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.015).sp,
        platformStyle = DefaultPlatformTextStyle,
        lineHeightStyle = DefaultLineHeightStyle
    ),
    displaySmall = TextStyle(
        fontFamily = UnboundedFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        platformStyle = DefaultPlatformTextStyle,
        lineHeightStyle = DefaultLineHeightStyle
    ),
    headlineMedium = TextStyle(
        fontFamily = UnboundedFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        platformStyle = DefaultPlatformTextStyle,
        lineHeightStyle = DefaultLineHeightStyle
    ),
    titleMedium = TextStyle(
        fontFamily = UnboundedFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.5.sp,
        lineHeight = 18.sp,
        platformStyle = DefaultPlatformTextStyle,
        lineHeightStyle = DefaultLineHeightStyle
    ),
    bodyLarge = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 20.sp,
        platformStyle = DefaultPlatformTextStyle,
        lineHeightStyle = DefaultLineHeightStyle
    ),
    bodyMedium = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        platformStyle = DefaultPlatformTextStyle,
        lineHeightStyle = DefaultLineHeightStyle
    ),
    bodySmall = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        platformStyle = DefaultPlatformTextStyle,
        lineHeightStyle = DefaultLineHeightStyle
    ),
    labelMedium = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.5.sp,
        lineHeight = 15.sp,
        platformStyle = DefaultPlatformTextStyle,
        lineHeightStyle = DefaultLineHeightStyle
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        platformStyle = DefaultPlatformTextStyle,
        lineHeightStyle = DefaultLineHeightStyle
    )
)
