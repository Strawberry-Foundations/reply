package org.strawberryfoundations.material.symbols.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.strawberryfoundations.material.symbols.MaterialSymbols

val MaterialSymbols.Outlined.Delete: ImageVector
    get() {
        val current = _delete
        if (current != null) return current

        return ImageVector.Builder(
            name = "Outlined.Delete",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            // M280 -120 q-33 0 -56.5 -23.5 T200 -200 v-520 q-17 0 -28.5 -11.5 T160 -760 t11.5 -28.5 T200 -800 h160 q0 -17 11.5 -28.5 T400 -840 h160 q17 0 28.5 11.5 T600 -800 h160 q17 0 28.5 11.5 T800 -760 t-11.5 28.5 T760 -720 v520 q0 33 -23.5 56.5 T680 -120z m400 -600 H280 v520 h400z M400 -280 q17 0 28.5 -11.5 T440 -320 v-280 q0 -17 -11.5 -28.5 T400 -640 t-28.5 11.5 T360 -600 v280 q0 17 11.5 28.5 T400 -280 m160 0 q17 0 28.5 -11.5 T600 -320 v-280 q0 -17 -11.5 -28.5 T560 -640 t-28.5 11.5 T520 -600 v280 q0 17 11.5 28.5 T560 -280 M280 -720 v520z
            path(
                fill = SolidColor(Color(0xFFE3E3E3)),
            ) {
                // M 280 840
                moveTo(x = 280.0f, y = 840.0f)
                // q -33 0 -56.5 -23.5
                quadToRelative(
                    dx1 = -33.0f,
                    dy1 = 0.0f,
                    dx2 = -56.5f,
                    dy2 = -23.5f,
                )
                // T 200 760
                reflectiveQuadTo(
                    x1 = 200.0f,
                    y1 = 760.0f,
                )
                // l 0 -520
                lineToRelative(dx = 0.0f, dy = -520.0f)
                // q -17 0 -28.5 -11.5
                quadToRelative(
                    dx1 = -17.0f,
                    dy1 = 0.0f,
                    dx2 = -28.5f,
                    dy2 = -11.5f,
                )
                // T 160 200
                reflectiveQuadTo(
                    x1 = 160.0f,
                    y1 = 200.0f,
                )
                // t 11.5 -28.5
                reflectiveQuadToRelative(
                    dx1 = 11.5f,
                    dy1 = -28.5f,
                )
                // T 200 160
                reflectiveQuadTo(
                    x1 = 200.0f,
                    y1 = 160.0f,
                )
                // l 160 0
                lineToRelative(dx = 160.0f, dy = 0.0f)
                // q 0 -17 11.5 -28.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = -17.0f,
                    dx2 = 11.5f,
                    dy2 = -28.5f,
                )
                // T 400 120
                reflectiveQuadTo(
                    x1 = 400.0f,
                    y1 = 120.0f,
                )
                // l 160 0
                lineToRelative(dx = 160.0f, dy = 0.0f)
                // q 17 0 28.5 11.5
                quadToRelative(
                    dx1 = 17.0f,
                    dy1 = 0.0f,
                    dx2 = 28.5f,
                    dy2 = 11.5f,
                )
                // T 600 160
                reflectiveQuadTo(
                    x1 = 600.0f,
                    y1 = 160.0f,
                )
                // l 160 0
                lineToRelative(dx = 160.0f, dy = 0.0f)
                // q 17 0 28.5 11.5
                quadToRelative(
                    dx1 = 17.0f,
                    dy1 = 0.0f,
                    dx2 = 28.5f,
                    dy2 = 11.5f,
                )
                // T 800 200
                reflectiveQuadTo(
                    x1 = 800.0f,
                    y1 = 200.0f,
                )
                // t -11.5 28.5
                reflectiveQuadToRelative(
                    dx1 = -11.5f,
                    dy1 = 28.5f,
                )
                // T 760 240
                reflectiveQuadTo(
                    x1 = 760.0f,
                    y1 = 240.0f,
                )
                // l 0 520
                lineToRelative(dx = 0.0f, dy = 520.0f)
                // q 0 33 -23.5 56.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 33.0f,
                    dx2 = -23.5f,
                    dy2 = 56.5f,
                )
                // T 680 840z
                reflectiveQuadTo(
                    x1 = 680.0f,
                    y1 = 840.0f,
                )
                close()
                // m 400 -600
                moveToRelative(dx = 400.0f, dy = -600.0f)
                // L 280 240
                lineTo(x = 280.0f, y = 240.0f)
                // l 0 520
                lineToRelative(dx = 0.0f, dy = 520.0f)
                // l 400 0z
                lineToRelative(dx = 400.0f, dy = 0.0f)
                close()
                // M 400 680
                moveTo(x = 400.0f, y = 680.0f)
                // q 17 0 28.5 -11.5
                quadToRelative(
                    dx1 = 17.0f,
                    dy1 = 0.0f,
                    dx2 = 28.5f,
                    dy2 = -11.5f,
                )
                // T 440 640
                reflectiveQuadTo(
                    x1 = 440.0f,
                    y1 = 640.0f,
                )
                // l 0 -280
                lineToRelative(dx = 0.0f, dy = -280.0f)
                // q 0 -17 -11.5 -28.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = -17.0f,
                    dx2 = -11.5f,
                    dy2 = -28.5f,
                )
                // T 400 320
                reflectiveQuadTo(
                    x1 = 400.0f,
                    y1 = 320.0f,
                )
                // t -28.5 11.5
                reflectiveQuadToRelative(
                    dx1 = -28.5f,
                    dy1 = 11.5f,
                )
                // T 360 360
                reflectiveQuadTo(
                    x1 = 360.0f,
                    y1 = 360.0f,
                )
                // l 0 280
                lineToRelative(dx = 0.0f, dy = 280.0f)
                // q 0 17 11.5 28.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 17.0f,
                    dx2 = 11.5f,
                    dy2 = 28.5f,
                )
                // T 400 680
                reflectiveQuadTo(
                    x1 = 400.0f,
                    y1 = 680.0f,
                )
                // m 160 0
                moveToRelative(dx = 160.0f, dy = 0.0f)
                // q 17 0 28.5 -11.5
                quadToRelative(
                    dx1 = 17.0f,
                    dy1 = 0.0f,
                    dx2 = 28.5f,
                    dy2 = -11.5f,
                )
                // T 600 640
                reflectiveQuadTo(
                    x1 = 600.0f,
                    y1 = 640.0f,
                )
                // l 0 -280
                lineToRelative(dx = 0.0f, dy = -280.0f)
                // q 0 -17 -11.5 -28.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = -17.0f,
                    dx2 = -11.5f,
                    dy2 = -28.5f,
                )
                // T 560 320
                reflectiveQuadTo(
                    x1 = 560.0f,
                    y1 = 320.0f,
                )
                // t -28.5 11.5
                reflectiveQuadToRelative(
                    dx1 = -28.5f,
                    dy1 = 11.5f,
                )
                // T 520 360
                reflectiveQuadTo(
                    x1 = 520.0f,
                    y1 = 360.0f,
                )
                // l 0 280
                lineToRelative(dx = 0.0f, dy = 280.0f)
                // q 0 17 11.5 28.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 17.0f,
                    dx2 = 11.5f,
                    dy2 = 28.5f,
                )
                // T 560 680
                reflectiveQuadTo(
                    x1 = 560.0f,
                    y1 = 680.0f,
                )
                // M 280 240
                moveTo(x = 280.0f, y = 240.0f)
                // l 0 520z
                lineToRelative(dx = 0.0f, dy = 520.0f)
                close()
            }
        }.build().also { _delete = it }
    }

@Suppress("ObjectPropertyName")
private var _delete: ImageVector? = null
