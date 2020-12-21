package org.komamitsu.konessem.cpu

import org.komamitsu.konessem.toUint

class CpuRam {
    private val bytes = ByteArray(0x0800)

    fun read(addr: Int): Int {
        return bytes[addr].toUint()
    }

    fun write(addr: Int, value: Byte) {
        bytes[addr] = value
    }

    fun slice(addr: Int, size: Int): ByteArray {
        return bytes.sliceArray(addr.until(addr + size))
    }
}