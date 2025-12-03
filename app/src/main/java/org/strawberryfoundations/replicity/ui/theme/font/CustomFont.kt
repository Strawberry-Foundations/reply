package org.strawberryfoundations.replicity.ui.theme.font

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import org.strawberryfoundations.replicity.ui.theme.googleSansCode
import org.strawberryfoundations.replicity.ui.theme.googleSansFlex

class CustomFont {
    val labelMedium = TextStyle(
        fontFamily = googleSansFlex.headlineSmallFontFamily,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
    )

    val numeralMedium = TextStyle(
        fontFamily = googleSansCode.numeralMedium,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    )
}