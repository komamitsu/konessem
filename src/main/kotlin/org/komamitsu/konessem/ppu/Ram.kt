package org.komamitsu.konessem.ppu

import org.komamitsu.konessem.toUint

internal class Ram(private val bytes: ByteArray) {
    private var addr: Int = 0
    private var settingHigherByteAddr = true

    private fun adjustedAddr(addr: Int): Int {
        return when (addr) {
            // Addresses $3F04/$3F08/$3F0C can contain unique data, though these values are not used by the PPU when normally rendering
            // (since the pattern values that would otherwise select those cells select the backdrop color instead).
            // They can still be shown using the background palette hack, explained below.
            //
            // Addresses $3F10/$3F14/$3F18/$3F1C are mirrors of $3F00/$3F04/$3F08/$3F0C. Note that this goes for writing as well as reading.
            // A symptom of not having implemented this correctly in an emulator is the sky being black in Super Mario Bros.,
            // which writes the backdrop color through $3F10.
            //
            // https://wiki.nesdev.com/w/index.php/PPU_palettes#Memory_Map
            0x3F10 -> 0x3F00
            0x3F14 -> 0x3F04
            0x3F18 -> 0x3F08
            0x3F1C -> 0x3F0C
            else -> addr
        }
    }

    fun addr(addr: Byte) {
        if (settingHigherByteAddr) {
            // Is it okay to clear lower 8 bits?
            this.addr = addr.toUint() shl 8
            settingHigherByteAddr = false
        }
        else {
            this.addr += addr.toUint()
            settingHigherByteAddr = true
        }
    }

    private var bufferedValue = 0
    fun read(addrIncr: Int): Int {
        val adjustedAddr = adjustedAddr(addr)
        if (adjustedAddr >= 0x3F00) {
            return bytes[adjustedAddr].toUint()
        }
        // https://wiki.nesdev.com/w/index.php/PPU_programmer_reference#The_PPUDATA_read_buffer_.28post-fetch.29
        val value = bufferedValue
        bufferedValue = bytes[adjustedAddr].toUint()
        addr += addrIncr
        return value
    }

    fun write(value: Byte, addrIncr: Int) {
        val adjustedAddr = adjustedAddr(addr)
        bytes[adjustedAddr] = value
        addr += addrIncr
    }

    fun spriteData(addrOfPatternTable: Int, spriteId: Int): ByteArray {
        val startAddr = addrOfPatternTable + Ppu.sizeOfSpriteData * spriteId
        val endAddr = addrOfPatternTable + Ppu.sizeOfSpriteData * (spriteId + 1)
        return bytes.sliceArray(startAddr.until(endAddr))
    }

    fun paletteData(addrOfPaletteTable: Int, paletteId: Int): ByteArray {
        val startAddr = addrOfPaletteTable + paletteId * 4
        val endAddr = addrOfPaletteTable + (paletteId + 1) * 4
        return bytes.sliceArray(startAddr.until(endAddr))
    }

    fun spriteId(addrOfNameTable: Int, tile: Tile): Int {
        return bytes[addrOfNameTable + (tile.y.value * Tile.maxX) + tile.x.value].toUint()
    }

    fun attribute(addrOfAttrTable: Int, tile: Tile): Attribute {
        val attrX = tile.x.toAttribute()
        val attrY = tile.y.toAttribute()
        return Attribute(
            x = attrX,
            y = attrY,
            value = bytes[addrOfAttrTable + (attrY.value * Attribute.maxX) + attrX.value].toUint()
        )
    }
}