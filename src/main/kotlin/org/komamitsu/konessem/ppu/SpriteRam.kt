package org.komamitsu.konessem.ppu

import org.komamitsu.konessem.toUint

internal class SpriteRam {
    private var addr: Byte = 0
    private val bytes = ByteArray(256)

    fun addr(addr: Byte) {
        this.addr = addr
    }

    fun read(): Int {
        val value = bytes[addr.toUint()]
        addr++
        return value.toUint()
    }

    fun directRead(addr: Int): Int {
        val value = bytes[addr]
        return value.toUint()
    }

    fun write(value: Byte) {
        bytes[addr.toUint()] = value
        addr++
    }

    fun transfer(bytes: ByteArray) {
        if (this.bytes.size != bytes.size) {
            throw IllegalArgumentException("The sizes of byte arrays are different. ${this.bytes.size} and ${bytes.size}")
        }
        bytes.copyInto(this.bytes)
    }
}