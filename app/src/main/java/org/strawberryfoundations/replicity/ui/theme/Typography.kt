package org.strawberryfoundations.replicity.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp
import org.strawberryfoundations.replicity.ui.theme.font.CustomFont
import org.strawberryfoundations.replicity.ui.theme.font.GoogleSansCode
import org.strawberryfoundations.replicity.ui.theme.font.GoogleSansFlex
import org.strawberryfoundations.replicity.ui.theme.font.RobotoFlex

val robotoFlex = RobotoFlex()
val googleSansFlex = GoogleSansFlex()
val googleSansCode = GoogleSansCode()
val customFont = CustomFont()

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = googleSansFlex.headlineSmallFontFamily,
        fontSize = 17.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = googleSansFlex.headlineSmallFontFamily,
        fontSize = 17.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = googleSansFlex.headlineSmallFontFamily,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = robotoFlex.titleMediumFontFamily,
        fontSize = 15.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = robotoFlex.titleMediumFontFamily,
        fontSize = 13.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = robotoFlex.labelLargeFontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = googleSansFlex.labelLargeFontFamily,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = googleSansFlex.labelFontFamily,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = googleSansFlex.displayLargeFontFamily,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = robotoFlex.displayLargeFontFamily,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
    ),
)

fun ascenderHeight(ascenderHeight: Float): FontVariation.Setting {
    require(ascenderHeight in 649f..854f) { "'Ascender Height' must be in 649f..854f" }
    return FontVariation.Setting("YTAS", ascenderHeight)
}

fun counterWidth(counterWidth: Int): FontVariation.Setting {
    require(counterWidth in 323..603) { "'Counter width' must be in 323..603" }
    return FontVariation.Setting("XTRA", counterWidth.toFloat())
}