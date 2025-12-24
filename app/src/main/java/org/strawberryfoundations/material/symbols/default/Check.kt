package org.strawberryfoundations.material.symbols.default

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.strawberryfoundations.material.symbols.MaterialSymbols

val MaterialSymbols.Default.Check: ImageVector
    get() {
        val current = _check
        if (current != null) return current

        return ImageVector.Builder(
            name = "Default.Check",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            // m382 -373.98 333.93 -333.93 q17.05 -16.96 40.18 -16.96 t40.09 16.75 16.95 40.13 -16.95 40.34 L421.63 -253.33 q-16.77 16.96 -39.54 16.96 t-39.72 -16.96 l-176.8 -176.56 q-16.96 -16.93 -16.58 -40.12 t17.13 -40.14 q16.98 -17.2 40.25 -17.2 t40.22 17.2z
            path(
                fill = SolidColor(Color(0xFFE3E3E3)),
            ) {
                // M 382 597.87
                moveTo(x = 382.0f, y = 597.87f)
                // l 334.7 -334.7
                lineToRelative(dx = 334.7f, dy = -334.7f)
                // q 13.67 -13.67 32.06 -13.67
                quadToRelative(
                    dx1 = 13.67f,
                    dy1 = -13.67f,
                    dx2 = 32.06f,
                    dy2 = -13.67f,
                )
                // t 32.07 13.67
                reflectiveQuadToRelative(
                    dx1 = 32.07f,
                    dy1 = 13.67f,
                )
                // q 13.67 13.68 13.67 32.45
                quadToRelative(
                    dx1 = 13.67f,
                    dy1 = 13.68f,
                    dx2 = 13.67f,
                    dy2 = 32.45f,
                )
                // t -13.67 32.45
                reflectiveQuadToRelative(
                    dx1 = -13.67f,
                    dy1 = 32.45f,
                )
                // L 414.07 695.58997
                lineTo(x = 414.07f, y = 695.58997f)
                // q -13.68 13.67 -32.07 13.67
                quadToRelative(
                    dx1 = -13.68f,
                    dy1 = 13.67f,
                    dx2 = -32.07f,
                    dy2 = 13.67f,
                )
                // t -32.07 -13.67
                reflectiveQuadToRelative(
                    dx1 = -32.07f,
                    dy1 = -13.67f,
                )
                // L 178.41 524.07
                lineTo(x = 178.41f, y = 524.07f)
                // q -13.67 -13.68 -13.29 -32.45
                quadToRelative(
                    dx1 = -13.67f,
                    dy1 = -13.68f,
                    dx2 = -13.29f,
                    dy2 = -32.45f,
                )
                // t 14.05 -32.45
                reflectiveQuadToRelative(
                    dx1 = 14.05f,
                    dy1 = -32.45f,
                )
                // t 32.45 -13.67
                reflectiveQuadToRelative(
                    dx1 = 32.45f,
                    dy1 = -13.67f,
                )
                // t 32.45 13.67z
                reflectiveQuadToRelative(
                    dx1 = 32.45f,
                    dy1 = 13.67f,
                )
                close()
            }
        }.build().also { _check = it }
    }

@Suppress("ObjectPropertyName")
private var _check: ImageVector? = null
