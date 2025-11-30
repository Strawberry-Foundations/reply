package org.strawberryfoundations.replicity.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import org.strawberryfoundations.replicity.R


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
    const val WEIGHT = 800
    const val WIDTH = 120f
    const val SLANT = 0f
    const val ASCENDER_HEIGHT = 800f
    const val COUNTER_WIDTH = 500
}


fun ascenderHeight(ascenderHeight: Float): FontVariation.Setting {
    require(ascenderHeight in 649f..854f) { "'Ascender Height' must be in 649f..854f" }
    return FontVariation.Setting("YTAS", ascenderHeight)
}

fun counterWidth(counterWidth: Int): FontVariation.Setting {
    require(counterWidth in 323..603) { "'Counter width' must be in 323..603" }
    return FontVariation.Setting("XTRA", counterWidth.toFloat())
}

@OptIn(ExperimentalTextApi::class)
val displayLargeFontFamily =
    FontFamily(
        Font(
            R.font.roboto_flex,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(DisplayLargeVFConfig.WEIGHT),
                FontVariation.width(DisplayLargeVFConfig.WIDTH),
                FontVariation.slant(DisplayLargeVFConfig.SLANT),
                ascenderHeight(DisplayLargeVFConfig.ASCENDER_HEIGHT),
                counterWidth(DisplayLargeVFConfig.COUNTER_WIDTH)
            )
        )
    )

@OptIn(ExperimentalTextApi::class)
val titleMediumFontFamily =
    FontFamily(
        Font(
            R.font.roboto_flex,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(TitleMediumVFConfig.WEIGHT),
                FontVariation.width(TitleMediumVFConfig.WIDTH),
                FontVariation.slant(TitleMediumVFConfig.SLANT),
                ascenderHeight(TitleMediumVFConfig.ASCENDER_HEIGHT),
                counterWidth(TitleMediumVFConfig.COUNTER_WIDTH)
            )
        )
    )


@OptIn(ExperimentalTextApi::class)
val headlineSmallFontFamily =
    FontFamily(
        Font(
            R.font.roboto_flex,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(HeadlineSmallVFConfig.WEIGHT),
                FontVariation.width(HeadlineSmallVFConfig.WIDTH),
                FontVariation.slant(HeadlineSmallVFConfig.SLANT),
                ascenderHeight(HeadlineSmallVFConfig.ASCENDER_HEIGHT),
                counterWidth(HeadlineSmallVFConfig.COUNTER_WIDTH)
            )
        )
    )

@OptIn(ExperimentalTextApi::class)
val labelLargeFontFamily = FontFamily(
    Font(
        R.font.roboto_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600),
            FontVariation.width(100f),
            FontVariation.slant(0f),
            ascenderHeight(800f),
            counterWidth(500)
        )
    )
)