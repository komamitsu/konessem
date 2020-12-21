package org.komamitsu.konessem.ppu

import javafx.scene.image.Image

internal data class Pixel(
    val x: Unit,
    val y: Unit
) {
    companion object {
        const val maxX = 32 * Tile.pixelsInTile
        const val maxY = 30 * Tile.pixelsInTile
    }

    fun zoomed(): Pixel {
        return Pixel(x.zoomed(), y.zoomed())
    }

    internal data class Unit(val value: Int) {
        // TODO Should create ZoomedPixel class?
        fun zoomed(): Unit = Unit(value * Ppu.zoomRate)

        fun incr(value: Int) = Unit(this.value + value)

        fun plus(other: Unit) = Unit(value + other.value)

        fun validWidth(image: Image) = value >= 0 && value < image.width

        fun validHeight(image: Image) = value >= 0 && value < image.height
    }
}