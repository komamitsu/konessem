package org.komamitsu.konessem.ppu

import org.komamitsu.konessem.toUint

internal class Scroll(private val register: Register) {
    private var scrollX = Pixel.Unit(0)
    private var scrollY = Pixel.Unit(0)

    // TODO: Receive tileX and tileY
    fun displayX(x: Pixel.Unit): Pixel.Unit {
        return Pixel.Unit(x.value - (scrollX.value % Tile.pixelsInTile))
    }

    fun displayY(y: Pixel.Unit): Pixel.Unit {
        return Pixel.Unit(y.value - (scrollY.value % Tile.pixelsInTile))
    }

    fun absoluteX(x: Pixel.Unit): Pixel.Unit {
        return Pixel.Unit(
            x.value + scrollX.value + if (register.nameTable % 2 == 0) {
                0
            } else {
                Pixel.maxX
            }
        )
    }

    fun absoluteY(y: Pixel.Unit): Pixel.Unit {
        return Pixel.Unit(
            y.value + scrollY.value + if (register.nameTable / 2 == 0) {
                0
            } else {
                Pixel.maxY
            }
        )
    }

    private var updateX = true

    fun write(value: Byte) {
        if (updateX) {
            scrollX = Pixel.Unit(value.toUint())
        }
        else {
            scrollY = Pixel.Unit(value.toUint())
        }
        updateX = !updateX
    }
}