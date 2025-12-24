package org.strawberryfoundations.material.symbols.filled

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.strawberryfoundations.material.symbols.MaterialSymbols

val MaterialSymbols.Filled.Database: ImageVector
    get() {
        val current = _database
        if (current != null) return current

        return ImageVector.Builder(
            name = "Filled.Database",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            // M480 -520 q150 0 255 -47 t105 -113 -105 -113 -255 -47 -255 47 -105 113 105 113 255 47 m0 100 q41 0 102.5 -8.5 T701 -456 t98 -49.5 41 -74.5 v100 q0 44 -41 74.5 T701 -356 t-118.5 27.5 T480 -320 t-102.5 -8.5 T259 -356 t-98 -49.5 -41 -74.5 v-100 q0 44 41 74.5 t98 49.5 118.5 27.5 T480 -420 m0 200 q41 0 102.5 -8.5 T701 -256 t98 -49.5 41 -74.5 v100 q0 44 -41 74.5 T701 -156 t-118.5 27.5 T480 -120 t-102.5 -8.5 T259 -156 t-98 -49.5 -41 -74.5 v-100 q0 44 41 74.5 t98 49.5 118.5 27.5 T480 -220
            path(
                fill = SolidColor(Color(0xFFE3E3E3)),
            ) {
                // M 480 440
                moveTo(x = 480.0f, y = 440.0f)
                // q 150 0 255 -47
                quadToRelative(
                    dx1 = 150.0f,
                    dy1 = 0.0f,
                    dx2 = 255.0f,
                    dy2 = -47.0f,
                )
                // t 105 -113
                reflectiveQuadToRelative(
                    dx1 = 105.0f,
                    dy1 = -113.0f,
                )
                // t -105 -113
                reflectiveQuadToRelative(
                    dx1 = -105.0f,
                    dy1 = -113.0f,
                )
                // t -255 -47
                reflectiveQuadToRelative(
                    dx1 = -255.0f,
                    dy1 = -47.0f,
                )
                // t -255 47
                reflectiveQuadToRelative(
                    dx1 = -255.0f,
                    dy1 = 47.0f,
                )
                // t -105 113
                reflectiveQuadToRelative(
                    dx1 = -105.0f,
                    dy1 = 113.0f,
                )
                // t 105 113
                reflectiveQuadToRelative(
                    dx1 = 105.0f,
                    dy1 = 113.0f,
                )
                // t 255 47
                reflectiveQuadToRelative(
                    dx1 = 255.0f,
                    dy1 = 47.0f,
                )
                // m 0 100
                moveToRelative(dx = 0.0f, dy = 100.0f)
                // q 41 0 102.5 -8.5
                quadToRelative(
                    dx1 = 41.0f,
                    dy1 = 0.0f,
                    dx2 = 102.5f,
                    dy2 = -8.5f,
                )
                // T 701 504
                reflectiveQuadTo(
                    x1 = 701.0f,
                    y1 = 504.0f,
                )
                // t 98 -49.5
                reflectiveQuadToRelative(
                    dx1 = 98.0f,
                    dy1 = -49.5f,
                )
                // t 41 -74.5
                reflectiveQuadToRelative(
                    dx1 = 41.0f,
                    dy1 = -74.5f,
                )
                // l 0 100
                lineToRelative(dx = 0.0f, dy = 100.0f)
                // q 0 44 -41 74.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 44.0f,
                    dx2 = -41.0f,
                    dy2 = 74.5f,
                )
                // T 701 604
                reflectiveQuadTo(
                    x1 = 701.0f,
                    y1 = 604.0f,
                )
                // t -118.5 27.5
                reflectiveQuadToRelative(
                    dx1 = -118.5f,
                    dy1 = 27.5f,
                )
                // T 480 640
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 640.0f,
                )
                // t -102.5 -8.5
                reflectiveQuadToRelative(
                    dx1 = -102.5f,
                    dy1 = -8.5f,
                )
                // T 259 604
                reflectiveQuadTo(
                    x1 = 259.0f,
                    y1 = 604.0f,
                )
                // t -98 -49.5
                reflectiveQuadToRelative(
                    dx1 = -98.0f,
                    dy1 = -49.5f,
                )
                // t -41 -74.5
                reflectiveQuadToRelative(
                    dx1 = -41.0f,
                    dy1 = -74.5f,
                )
                // l 0 -100
                lineToRelative(dx = 0.0f, dy = -100.0f)
                // q 0 44 41 74.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 44.0f,
                    dx2 = 41.0f,
                    dy2 = 74.5f,
                )
                // t 98 49.5
                reflectiveQuadToRelative(
                    dx1 = 98.0f,
                    dy1 = 49.5f,
                )
                // t 118.5 27.5
                reflectiveQuadToRelative(
                    dx1 = 118.5f,
                    dy1 = 27.5f,
                )
                // T 480 540
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 540.0f,
                )
                // m 0 200
                moveToRelative(dx = 0.0f, dy = 200.0f)
                // q 41 0 102.5 -8.5
                quadToRelative(
                    dx1 = 41.0f,
                    dy1 = 0.0f,
                    dx2 = 102.5f,
                    dy2 = -8.5f,
                )
                // T 701 704
                reflectiveQuadTo(
                    x1 = 701.0f,
                    y1 = 704.0f,
                )
                // t 98 -49.5
                reflectiveQuadToRelative(
                    dx1 = 98.0f,
                    dy1 = -49.5f,
                )
                // t 41 -74.5
                reflectiveQuadToRelative(
                    dx1 = 41.0f,
                    dy1 = -74.5f,
                )
                // l 0 100
                lineToRelative(dx = 0.0f, dy = 100.0f)
                // q 0 44 -41 74.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 44.0f,
                    dx2 = -41.0f,
                    dy2 = 74.5f,
                )
                // T 701 804
                reflectiveQuadTo(
                    x1 = 701.0f,
                    y1 = 804.0f,
                )
                // t -118.5 27.5
                reflectiveQuadToRelative(
                    dx1 = -118.5f,
                    dy1 = 27.5f,
                )
                // T 480 840
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 840.0f,
                )
                // t -102.5 -8.5
                reflectiveQuadToRelative(
                    dx1 = -102.5f,
                    dy1 = -8.5f,
                )
                // T 259 804
                reflectiveQuadTo(
                    x1 = 259.0f,
                    y1 = 804.0f,
                )
                // t -98 -49.5
                reflectiveQuadToRelative(
                    dx1 = -98.0f,
                    dy1 = -49.5f,
                )
                // t -41 -74.5
                reflectiveQuadToRelative(
                    dx1 = -41.0f,
                    dy1 = -74.5f,
                )
                // l 0 -100
                lineToRelative(dx = 0.0f, dy = -100.0f)
                // q 0 44 41 74.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 44.0f,
                    dx2 = 41.0f,
                    dy2 = 74.5f,
                )
                // t 98 49.5
                reflectiveQuadToRelative(
                    dx1 = 98.0f,
                    dy1 = 49.5f,
                )
                // t 118.5 27.5
                reflectiveQuadToRelative(
                    dx1 = 118.5f,
                    dy1 = 27.5f,
                )
                // T 480 740
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 740.0f,
                )
            }
        }.build().also { _database = it }
    }

@Suppress("ObjectPropertyName")
private var _database: ImageVector? = null
