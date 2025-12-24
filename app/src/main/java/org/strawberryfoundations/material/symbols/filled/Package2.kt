package org.strawberryfoundations.material.symbols.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.strawberryfoundations.material.symbols.MaterialSymbols

val MaterialSymbols.Filled.Package2: ImageVector
    get() {
        val current = _package2
        if (current != null) return current

        return ImageVector.Builder(
            name = "Filled.Package2",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            // M440 -91 v-366 L120 -642 v321 q0 22 10.5 40 t29.5 29z m80 0 280 -161 q19 -11 29.5 -29 t10.5 -40 v-321 L520 -457z m159 -550 118 -69 -277 -159 q-19 -11 -40 -11 t-40 11 l-79 45z M480 -526 l119 -68 -317 -184 -120 69z
            path(
                fill = SolidColor(Color(0xFFE3E3E3)),
            ) {
                // M 440 869
                moveTo(x = 440.0f, y = 869.0f)
                // l 0 -366
                lineToRelative(dx = 0.0f, dy = -366.0f)
                // L 120 318
                lineTo(x = 120.0f, y = 318.0f)
                // l 0 321
                lineToRelative(dx = 0.0f, dy = 321.0f)
                // q 0 22 10.5 40
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 22.0f,
                    dx2 = 10.5f,
                    dy2 = 40.0f,
                )
                // t 29.5 29z
                reflectiveQuadToRelative(
                    dx1 = 29.5f,
                    dy1 = 29.0f,
                )
                close()
                // m 80 0
                moveToRelative(dx = 80.0f, dy = 0.0f)
                // l 280 -161
                lineToRelative(dx = 280.0f, dy = -161.0f)
                // q 19 -11 29.5 -29
                quadToRelative(
                    dx1 = 19.0f,
                    dy1 = -11.0f,
                    dx2 = 29.5f,
                    dy2 = -29.0f,
                )
                // t 10.5 -40
                reflectiveQuadToRelative(
                    dx1 = 10.5f,
                    dy1 = -40.0f,
                )
                // l 0 -321
                lineToRelative(dx = 0.0f, dy = -321.0f)
                // L 520 503z
                lineTo(x = 520.0f, y = 503.0f)
                close()
                // m 159 -550
                moveToRelative(dx = 159.0f, dy = -550.0f)
                // l 118 -69
                lineToRelative(dx = 118.0f, dy = -69.0f)
                // l -277 -159
                lineToRelative(dx = -277.0f, dy = -159.0f)
                // q -19 -11 -40 -11
                quadToRelative(
                    dx1 = -19.0f,
                    dy1 = -11.0f,
                    dx2 = -40.0f,
                    dy2 = -11.0f,
                )
                // t -40 11
                reflectiveQuadToRelative(
                    dx1 = -40.0f,
                    dy1 = 11.0f,
                )
                // l -79 45z
                lineToRelative(dx = -79.0f, dy = 45.0f)
                close()
                // M 480 434
                moveTo(x = 480.0f, y = 434.0f)
                // l 119 -68
                lineToRelative(dx = 119.0f, dy = -68.0f)
                // l -317 -184
                lineToRelative(dx = -317.0f, dy = -184.0f)
                // l -120 69z
                lineToRelative(dx = -120.0f, dy = 69.0f)
                close()
            }
        }.build().also { _package2 = it }
    }

@Suppress("ObjectPropertyName")
private var _package2: ImageVector? = null
