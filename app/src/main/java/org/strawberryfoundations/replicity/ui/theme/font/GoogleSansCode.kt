package org.strawberryfoundations.replicity.ui.theme.font

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import org.strawberryfoundations.replicity.R
import org.strawberryfoundations.replicity.ui.theme.font.RobotoFlex.DisplayLargeVFConfig


class GoogleSansCode {
    @OptIn(ExperimentalTextApi::class)
    val numeralMedium =
        FontFamily(
            Font(
                R.font.google_sans_code,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(DisplayLargeVFConfig.WEIGHT),
                    FontVariation.width(DisplayLargeVFConfig.WIDTH),
                )
            )
        )
}