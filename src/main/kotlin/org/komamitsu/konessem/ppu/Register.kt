package org.komamitsu.konessem.ppu

internal class Register {
    var writeByte0 = 0
    var writeByte1 = 0
    var readByte: Byte = 0

    val interruptOnVBlank: Boolean
        get() = writeByte0 and 0x80 != 0

    val spriteSize8x16 : Boolean
        get() = writeByte0 and 0x20 != 0

    val bgPatternAddrMode : Boolean
        get() = writeByte0 and 0x10 != 0

    val spritePatternAddrMode : Boolean
        get() = writeByte0 and 0x08 != 0

    val ppuAddrIncrBytes : Int
        get() = if (writeByte0 and 0x04 == 0) {
            1
        }
        else {
            32
        }

    val nameTable: Int
        get() = writeByte0 and 0x03

    val bgColor : Int
        get() = writeByte1 shr 5

    val spriteEnabled : Boolean
        get() = writeByte1 and 0x10 != 0

    val bgEnabled : Boolean
        get() = writeByte1 and 0x08 != 0

    val renderSpriteLeftEdge : Boolean
        get() = writeByte1 and 0x04 != 0

    val renderBgLeftEdge : Boolean
        get() = writeByte1 and 0x02 != 0

    val displayType: Boolean
        get() = writeByte1 and 0x01 != 0

    var vblank: Boolean
        get() = readByte.toInt() and 0x80 != 0
        set(value) {
            readByte = if (value) {
                (readByte.toInt() or 0x80).toByte()
            } else {
                (readByte.toInt() and 0x7F).toByte()
            }
        }

    var spriteHit: Boolean
        get() = readByte.toInt() and 0x40 != 0
        set(value) {
            readByte = if (value) {
                (readByte.toInt() or 0x40).toByte()
            } else {
                (readByte.toInt() and 0xBF).toByte()
            }
        }
}