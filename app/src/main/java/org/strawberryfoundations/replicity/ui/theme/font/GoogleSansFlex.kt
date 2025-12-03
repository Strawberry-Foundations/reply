package org.strawberryfoundations.replicity.ui.theme.font

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import org.strawberryfoundations.replicity.R
import org.strawberryfoundations.replicity.ui.theme.ascenderHeight
import org.strawberryfoundations.replicity.ui.theme.counterWidth

class GoogleSansFlex {
    object DisplayLargeVFConfig {
        const val WEIGHT = 950
        const val WIDTH = 120f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 800f
        const val COUNTER_WIDTH = 500
    }

    object TitleMediumVFConfig {
        const val WEIGHT = 600
        const val WIDTH = 110f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 800f
        const val COUNTER_WIDTH = 475
    }


    object HeadlineSmallVFConfig {
        const val WEIGHT = 1000
        const val WIDTH = 120f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 800f
        const val COUNTER_WIDTH = 515
    }

    @OptIn(ExperimentalTextApi::class)
    val displayLargeFontFamily =
        FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(DisplayLargeVFConfig.WEIGHT),
                    FontVariation.width(DisplayLargeVFConfig.WIDTH),
                    FontVariation.slant(DisplayLargeVFConfig.SLANT),
                    ascenderHeight(
                        DisplayLargeVFConfig.ASCENDER_HEIGHT
                    ),
                    counterWidth(
                        DisplayLargeVFConfig.COUNTER_WIDTH
                    )
                )
            )
        )

    @OptIn(ExperimentalTextApi::class)
    val titleMediumFontFamily =
        FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(TitleMediumVFConfig.WEIGHT),
                    FontVariation.width(TitleMediumVFConfig.WIDTH),
                    FontVariation.slant(TitleMediumVFConfig.SLANT),
                    ascenderHeight(
                        TitleMediumVFConfig.ASCENDER_HEIGHT
                    ),
                    counterWidth(
                        TitleMediumVFConfig.COUNTER_WIDTH
                    )
                )
            )
        )


    @OptIn(ExperimentalTextApi::class)
    val headlineSmallFontFamily =
        FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(HeadlineSmallVFConfig.WEIGHT),
                    FontVariation.width(HeadlineSmallVFConfig.WIDTH),
                    FontVariation.slant(HeadlineSmallVFConfig.SLANT),
                    ascenderHeight(
                        HeadlineSmallVFConfig.ASCENDER_HEIGHT
                    ),
                    counterWidth(
                        HeadlineSmallVFConfig.COUNTER_WIDTH
                    )
                )
            )
        )

    @OptIn(ExperimentalTextApi::class)
    val labelLargeFontFamily = FontFamily(
        Font(
            R.font.google_sans_flex,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(600),
                FontVariation.width(100f),
                FontVariation.slant(0f),
                ascenderHeight(800f),
                counterWidth(500)
            )
        )
    )

    @OptIn(ExperimentalTextApi::class)
    val labelFontFamily = FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(800),
                    FontVariation.width(108f),
                    FontVariation.slant(0f),
                    ascenderHeight(800f),
                    counterWidth(515)
                )
            )
        )
}