package org.komamitsu.konessem.cpu

import org.komamitsu.konessem.Address
import org.komamitsu.konessem.toUint

class CpuRam {
    private val bytes = ByteArray(0x0800)

    fun read(addr: Address): Int {
        return bytes[addr.value].toUint()
    }

    fun write(addr: Address, value: Byte) {
        bytes[addr.value] = value
    }

    fun slice(addr: Address, size: Int): ByteArray {
        return bytes.sliceArray(addr.value.until(addr.value + size))
    }
}