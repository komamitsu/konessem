package org.komamitsu.konessem.ppu

internal data class Attribute(
    val x: Unit,
    val y: Unit,
    private val value: Int
) {
    companion object {
        const val maxX = Tile.maxX / 4
    }

    internal data class Unit(val value: Int)

    fun paletteId(tile: Tile): Int {
        val attrIndexX = if (tile.x.value % 4 > 1) 1 else 0
        val attrIndexY = if (tile.y.value % 4 > 1) 2 else 0
        val attrIndex = attrIndexY + attrIndexX
        return (value shr (attrIndex * 2)) and 0x03
    }
}
