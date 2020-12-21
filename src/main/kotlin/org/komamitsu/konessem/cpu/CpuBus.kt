package org.komamitsu.konessem.cpu

import org.komamitsu.konessem.Apu
import org.komamitsu.konessem.KeyPad
import org.komamitsu.konessem.ppu.Ppu
import org.komamitsu.konessem.rom.PrgRom
import org.komamitsu.konessem.toUint
import java.lang.IllegalStateException

class CpuBus(
    private val cpuRam: CpuRam,
    private val ppu: Ppu,
    private val prgRom: PrgRom,
    private val keyPad: KeyPad
) {
    private val apu = Apu()

    fun read(addr: Int): Int {
        return when (addr) {
            in 0x0000.until(0x0800) -> cpuRam.read(addr)
            in 0x0800.until(0x2000) -> cpuRam.read(addr - 0x0800)
            in 0x2000.until(0x4000) -> ppu.read(addr - 0x2000)
            0x4016 -> keyPad.read()
            0x4017 -> 0
            in 0x4000.until(0x4020) -> apu.read(addr - 0x4000)
            in 0x4020.until(0x6000) -> throw IllegalStateException("Extended ROM isn't supported")
            in 0x6000.until(0x8000) -> { /* Need to take care of extended RAM access? */ 0 }
            in 0x8000.until(0xC000) -> prgRom.read(addr - 0x8000)
            in 0xC000.until(0x10000) -> prgRom.read(addr - if (prgRom.size <= 0x4000) 0xC000 else 0x8000)
            else -> throw IllegalArgumentException("Invalid address: 0x%X".format(addr))
        }
    }

    fun readWord(addr: Int): Int {
        val lsb = read(addr) and 0xFF
        val msb = read(addr + 1) and 0xFF
        return msb shl 8 or lsb
    }

    fun write(addr: Int, value: Byte) {
        when (addr) {
            in 0x0000.until(0x0800) -> cpuRam.write(addr, value)
            in 0x0800.until(0x2000) -> cpuRam.write(addr - 0x0800, value)
            in 0x2000.until(0x4000) -> ppu.write(addr - 0x2000, value)
            0x4014 -> {
                val srcBytes = cpuRam.slice(addr = value * 0x100, size = 0x100)
                ppu.transferToSprite(srcBytes)
            }
            0x4016 -> keyPad.write(value = value.toUint())
            0x4017 -> {}
            in 0x4000.until(0x4020) -> { /* TODO */ }
            else -> {}
        }
    }
}