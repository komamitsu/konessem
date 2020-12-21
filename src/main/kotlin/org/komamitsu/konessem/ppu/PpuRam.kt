package org.komamitsu.konessem.ppu

import org.komamitsu.konessem.Address
import org.komamitsu.konessem.toUint

internal class PpuRam(private val bytes: ByteArray) {
    private var addr: Address = Address(0)
    private var settingHigherByteAddr = true

    private fun adjustedAddr(addr: Address): Address {
        return when (addr.value) {
            // Addresses $3F04/$3F08/$3F0C can contain unique data, though these values are not used by the PPU when normally rendering
            // (since the pattern values that would otherwise select those cells select the backdrop color instead).
            // They can still be shown using the background palette hack, explained below.
            //
            // Addresses $3F10/$3F14/$3F18/$3F1C are mirrors of $3F00/$3F04/$3F08/$3F0C. Note that this goes for writing as well as reading.
            // A symptom of not having implemented this correctly in an emulator is the sky being black in Super Mario Bros.,
            // which writes the backdrop color through $3F10.
            //
            // https://wiki.nesdev.com/w/index.php/PPU_palettes#Memory_Map
            0x3F10 -> Address(0x3F00)
            0x3F14 -> Address(0x3F04)
            0x3F18 -> Address(0x3F08)
            0x3F1C -> Address(0x3F0C)
            else -> addr
        }
    }

    fun addr(addr: Address) {
        if (settingHigherByteAddr) {
            // Is it okay to clear lower 8 bits?
            this.addr = Address(addr.lsb.value shl 8)
            settingHigherByteAddr = false
        }
        else {
            this.addr = this.addr.plus(addr)
            settingHigherByteAddr = true
        }
    }

    private var bufferedValue = 0
    fun read(addrIncr: Int): Int {
        val adjustedAddr = adjustedAddr(addr)
        if (adjustedAddr.value >= 0x3F00) {
            return bytes[adjustedAddr.value].toUint()
        }
        // https://wiki.nesdev.com/w/index.php/PPU_programmer_reference#The_PPUDATA_read_buffer_.28post-fetch.29
        val value = bufferedValue
        bufferedValue = bytes[adjustedAddr.value].toUint()
        addr = addr.plus(addrIncr)
        return value
    }

    fun write(value: Byte, addrIncr: Int) {
        val adjustedAddr = adjustedAddr(addr)
        bytes[adjustedAddr.value] = value
        addr = addr.plus(addrIncr)
    }

    fun spriteData(addrOfPatternTable: Address, spriteId: Int): ByteArray {
        val startAddr = addrOfPatternTable.plus(Ppu.sizeOfSpriteData * spriteId)
        val endAddr = addrOfPatternTable.plus(Ppu.sizeOfSpriteData * (spriteId + 1))
        return bytes.sliceArray(startAddr.value.until(endAddr.value))
    }

    fun paletteData(addrOfPaletteTable: Address, paletteId: Int): ByteArray {
        val startAddr = addrOfPaletteTable.plus(paletteId * 4)
        val endAddr = addrOfPaletteTable.plus((paletteId + 1) * 4)
        return bytes.sliceArray(startAddr.value.until(endAddr.value))
    }

    fun spriteId(addrOfNameTable: Address, tile: Tile): Int {
        return bytes[addrOfNameTable.plus(tile.y.value * Tile.maxX).plus(tile.x.value).value].toUint()
    }

    fun attribute(addrOfAttrTable: Address, tile: Tile): Attribute {
        val attrX = tile.x.toAttribute()
        val attrY = tile.y.toAttribute()
        return Attribute(
            x = attrX,
            y = attrY,
            value = bytes[addrOfAttrTable.plus(attrY.value * Attribute.maxX).plus(attrX.value).value].toUint()
        )
    }
}