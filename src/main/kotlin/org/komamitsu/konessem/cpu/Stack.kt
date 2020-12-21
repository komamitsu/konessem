package org.komamitsu.konessem.cpu

class Stack(
    private val cpuBus: CpuBus,
    private val register: Register
) {
    private val baseAddr = 0x0100

    internal fun push(value: Byte) {
        val addr = register.sp + baseAddr
        cpuBus.write(addr, value)
        register.sp--
    }

    internal fun pushWord(value: Short) {
        val v = value.toInt()
        push((v shr 8 and 0xFF).toByte())
        push((v and 0xFF).toByte())
    }

    internal fun pop(): Int {
        ++register.sp
        val addr = register.sp + baseAddr
        val value = cpuBus.read(addr)
        return value
    }

    internal fun popWord(): Int {
        val lsb = pop()
        val msb = pop()
        return msb shl 8 or lsb
    }
}