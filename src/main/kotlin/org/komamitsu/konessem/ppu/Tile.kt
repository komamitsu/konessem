package org.komamitsu.konessem.ppu

internal data class Tile(
    val x: Unit,
    val y: Unit
) {
    companion object {
        const val pixelsInTile = 8
        const val maxX = 32
    }

    internal data class Unit(val value: Int) {
        companion object {
            fun fromPixel(pixel: Pixel.Unit) =
                Unit(pixel.value / pixelsInTile)
        }

        fun toPixel(): Pixel.Unit =
            Pixel.Unit(value * pixelsInTile)

        fun toAttribute() = Attribute.Unit(value / 4)

        fun incr(value: Int) = Unit(this.value + value)

        fun plus(other: Unit) = Unit(value + other.value)

        fun contains(pixel: Pixel.Unit) =
            pixel.value in (this.toPixel().value).until(incr(1).toPixel().value)
    }
}