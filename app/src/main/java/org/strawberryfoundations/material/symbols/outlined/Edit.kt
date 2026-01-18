package org.strawberryfoundations.material.symbols.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.strawberryfoundations.material.symbols.MaterialSymbols

val MaterialSymbols.Outlined.Edit: ImageVector
    get() {
        val current = _edit
        if (current != null) return current

        return ImageVector.Builder(
            name = "Outlined.Edit",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            // M200 -200 h57 l391 -391 -57 -57 -391 391z m-40 80 q-17 0 -28.5 -11.5 T120 -160 v-97 q0 -16 6 -30.5 t17 -25.5 l505 -504 q12 -11 26.5 -17 t30.5 -6 31 6 26 18 l55 56 q12 11 17.5 26 t5.5 30 q0 16 -5.5 30.5 T817 -647 L313 -143 q-11 11 -25.5 17 t-30.5 6z m600 -584 -56 -56z m-141 85 -28 -29 57 57z
            path(
                fill = SolidColor(Color(0xFFE3E3E3)),
            ) {
                // M 200 760
                moveTo(x = 200.0f, y = 760.0f)
                // l 57 0
                lineToRelative(dx = 57.0f, dy = 0.0f)
                // l 391 -391
                lineToRelative(dx = 391.0f, dy = -391.0f)
                // l -57 -57
                lineToRelative(dx = -57.0f, dy = -57.0f)
                // l -391 391z
                lineToRelative(dx = -391.0f, dy = 391.0f)
                close()
                // m -40 80
                moveToRelative(dx = -40.0f, dy = 80.0f)
                // q -17 0 -28.5 -11.5
                quadToRelative(
                    dx1 = -17.0f,
                    dy1 = 0.0f,
                    dx2 = -28.5f,
                    dy2 = -11.5f,
                )
                // T 120 800
                reflectiveQuadTo(
                    x1 = 120.0f,
                    y1 = 800.0f,
                )
                // l 0 -97
                lineToRelative(dx = 0.0f, dy = -97.0f)
                // q 0 -16 6 -30.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = -16.0f,
                    dx2 = 6.0f,
                    dy2 = -30.5f,
                )
                // t 17 -25.5
                reflectiveQuadToRelative(
                    dx1 = 17.0f,
                    dy1 = -25.5f,
                )
                // l 505 -504
                lineToRelative(dx = 505.0f, dy = -504.0f)
                // q 12 -11 26.5 -17
                quadToRelative(
                    dx1 = 12.0f,
                    dy1 = -11.0f,
                    dx2 = 26.5f,
                    dy2 = -17.0f,
                )
                // t 30.5 -6
                reflectiveQuadToRelative(
                    dx1 = 30.5f,
                    dy1 = -6.0f,
                )
                // t 31 6
                reflectiveQuadToRelative(
                    dx1 = 31.0f,
                    dy1 = 6.0f,
                )
                // t 26 18
                reflectiveQuadToRelative(
                    dx1 = 26.0f,
                    dy1 = 18.0f,
                )
                // l 55 56
                lineToRelative(dx = 55.0f, dy = 56.0f)
                // q 12 11 17.5 26
                quadToRelative(
                    dx1 = 12.0f,
                    dy1 = 11.0f,
                    dx2 = 17.5f,
                    dy2 = 26.0f,
                )
                // t 5.5 30
                reflectiveQuadToRelative(
                    dx1 = 5.5f,
                    dy1 = 30.0f,
                )
                // q 0 16 -5.5 30.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 16.0f,
                    dx2 = -5.5f,
                    dy2 = 30.5f,
                )
                // T 817 313
                reflectiveQuadTo(
                    x1 = 817.0f,
                    y1 = 313.0f,
                )
                // L 313 817
                lineTo(x = 313.0f, y = 817.0f)
                // q -11 11 -25.5 17
                quadToRelative(
                    dx1 = -11.0f,
                    dy1 = 11.0f,
                    dx2 = -25.5f,
                    dy2 = 17.0f,
                )
                // t -30.5 6z
                reflectiveQuadToRelative(
                    dx1 = -30.5f,
                    dy1 = 6.0f,
                )
                close()
                // m 600 -584
                moveToRelative(dx = 600.0f, dy = -584.0f)
                // l -56 -56z
                lineToRelative(dx = -56.0f, dy = -56.0f)
                close()
                // m -141 85
                moveToRelative(dx = -141.0f, dy = 85.0f)
                // l -28 -29
                lineToRelative(dx = -28.0f, dy = -29.0f)
                // l 57 57z
                lineToRelative(dx = 57.0f, dy = 57.0f)
                close()
            }
        }.build().also { _edit = it }
    }


@Suppress("ObjectPropertyName")
private var _edit: ImageVector? = null
