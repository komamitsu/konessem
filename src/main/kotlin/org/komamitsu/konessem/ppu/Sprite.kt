package org.komamitsu.konessem.ppu

import javafx.scene.image.Image
import org.komamitsu.konessem.Address

internal data class Sprite(
    val id: Int,
    val position: Pixel,
    val reverseVertical: Boolean,
    val reverseHorizontal: Boolean,
    val prioritizedBg: Boolean,
    val paletteId: Int,
    val heightOfSprite: Int,
    val addrOfPatternTable: Address
) {
    companion object {
        fun get(register: Register, image: Image, spriteRam: SpriteRam, index: Int): Sprite? {
            val y = Pixel.Unit(spriteRam.directRead(index))
            if (!y.validHeight(image)) {
                return null
            }

            val spriteIndex = spriteRam.directRead(index + 1)
            val bits = spriteRam.directRead(index + 2)
            val reverseVertical = bits and 0x80 != 0
            val reverseHorizontal = bits and 0x40 != 0
            val prioritizedBg = bits and 0x20 != 0
            val paletteId = bits and 0x03
            val x = Pixel.Unit(spriteRam.directRead(index + 3))
            val (heightOfSprite, actualSpriteIndex, addrOfPatternTable) = if (register.spriteSize8x16) {
                val addrOfPatternTable = if (spriteIndex and 0x01 == 0) {
                    Ppu.addrOfPatternTable0
                } else {
                    Ppu.addrOfPatternTable1
                }
                Triple(2, spriteIndex and 0xFE, addrOfPatternTable)
            } else {
                val addrOfPatternTable = if (register.spritePatternAddrMode) {
                    Ppu.addrOfPatternTable1
                } else {
                    Ppu.addrOfPatternTable0
                }
                Triple(1, spriteIndex, addrOfPatternTable)
            }

            return Sprite(
                id = actualSpriteIndex,
                position = Pixel(x, y),
                reverseVertical = reverseVertical,
                reverseHorizontal = reverseHorizontal,
                prioritizedBg = prioritizedBg,
                paletteId = paletteId,
                heightOfSprite = heightOfSprite,
                addrOfPatternTable = addrOfPatternTable
            )
        }
    }
}
