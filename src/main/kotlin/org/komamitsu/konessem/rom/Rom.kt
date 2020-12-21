package org.komamitsu.konessem.rom

import org.komamitsu.konessem.Address
import org.komamitsu.konessem.toUint

class PrgRom(private val bytes: ByteArray) {
    val size: Int
        get() = bytes.size

    fun read(addr: Address): Int {
        return bytes[addr.value].toUint()
    }
}

class ChrRom(private val bytes: ByteArray) {
    fun copyInto(dest: ByteArray) {
        bytes.copyInto(dest)
    }
}