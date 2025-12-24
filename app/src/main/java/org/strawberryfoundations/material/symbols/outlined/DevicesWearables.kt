package org.strawberryfoundations.material.symbols.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

import org.strawberryfoundations.material.symbols.MaterialSymbols

val MaterialSymbols.Outlined.DevicesWearables: ImageVector
    get() {
        if (_devicesWearables != null) return _devicesWearables!!

        _devicesWearables = ImageVector.Builder(
            name = "devicesWearables",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFe3e3e3))
            ) {
                moveTo(280f, 840f)
                verticalLineToRelative(-720f)
                verticalLineToRelative(720f)
                close()
                moveToRelative(200f, -600f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(520f, 200f)
                quadToRelative(0f, -17f, -11.5f, -28.5f)
                reflectiveQuadTo(480f, 160f)
                quadToRelative(-17f, 0f, -28.5f, 11.5f)
                reflectiveQuadTo(440f, 200f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(480f, 240f)
                close()
                moveToRelative(58f, 680f)
                horizontalLineTo(280f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(200f, 840f)
                verticalLineToRelative(-720f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(280f, 40f)
                horizontalLineToRelative(400f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(760f, 120f)
                verticalLineToRelative(240f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-240f)
                horizontalLineTo(280f)
                verticalLineToRelative(720f)
                horizontalLineToRelative(215f)
                quadToRelative(7f, 9f, 15f, 17f)
                lineToRelative(16f, 16f)
                lineToRelative(12f, 47f)
                close()
                moveToRelative(82f, 0f)
                lineToRelative(-23f, -92f)
                quadToRelative(-36f, -26f, -56.5f, -64.5f)
                reflectiveQuadTo(520f, 680f)
                quadToRelative(0f, -45f, 20.5f, -83.5f)
                reflectiveQuadTo(597f, 532f)
                lineToRelative(23f, -92f)
                horizontalLineToRelative(160f)
                lineToRelative(23f, 92f)
                quadToRelative(36f, 26f, 56.5f, 64.5f)
                reflectiveQuadTo(880f, 680f)
                quadToRelative(0f, 45f, -20.5f, 83.5f)
                reflectiveQuadTo(803f, 828f)
                lineToRelative(-23f, 92f)
                horizontalLineTo(620f)
                close()
                moveToRelative(80f, -140f)
                quadToRelative(42f, 0f, 71f, -29f)
                reflectiveQuadToRelative(29f, -71f)
                quadToRelative(0f, -42f, -29f, -71f)
                reflectiveQuadToRelative(-71f, -29f)
                quadToRelative(-42f, 0f, -71f, 29f)
                reflectiveQuadToRelative(-29f, 71f)
                quadToRelative(0f, 42f, 29f, 71f)
                reflectiveQuadToRelative(71f, 29f)
                close()
            }
        }.build()

        return _devicesWearables!!
    }

private var _devicesWearables: ImageVector? = null
