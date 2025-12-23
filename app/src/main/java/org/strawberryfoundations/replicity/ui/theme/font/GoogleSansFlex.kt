package org.strawberryfoundations.replicity.ui.theme.font

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import org.strawberryfoundations.replicity.R

class GoogleSansFlex {
    object DisplayLargeVFConfig {
        const val WEIGHT = 950
        const val WIDTH = 120f
        const val SLANT = 0f
    }


    object HeadlineSmallVFConfig {
        const val WEIGHT = 1000
        const val WIDTH = 120f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 800f
    }

    @OptIn(ExperimentalTextApi::class)
    val displayFontFamily =
        FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    settings = arrayOf(
                        FontVariation.weight(1000),
                        FontVariation.width(110f),
                        FontVariation.grade(0),
                        FontVariation.Setting("ROND", 100f)
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
                    settings = arrayOf(
                        FontVariation.weight(1000),
                        FontVariation.width(100f),
                        FontVariation.grade(0),
                        FontVariation.Setting("ROND", 100f)
                    )
                )
            )
        )

    @OptIn(ExperimentalTextApi::class)
    val titleSmallFontFamily =
        FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(700),
                    FontVariation.width(100f),
                    FontVariation.Setting("ROND", 100f)
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
                    FontVariation.Setting("ROND", 100f)
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
                FontVariation.Setting("ROND", 100f)
            )
        )
    )

    @OptIn(ExperimentalTextApi::class)
    val labelFontFamily = FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(800),
                    FontVariation.width(100f),
                    FontVariation.Setting("ROND", 100f)
                )
            )
        )
}