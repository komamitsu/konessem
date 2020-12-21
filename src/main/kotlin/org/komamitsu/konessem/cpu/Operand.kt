package org.komamitsu.konessem.cpu

class Operand(
    private val raw: Int? = null,
    private val addrFunction: (() -> Int)? = null,
    private val valueFunction: (() -> Int)? = null
) {
    val addr : Int by lazy {
        val f = addrFunction

        if (f == null) {
            throw NotSupportedOperation(
                msg = "This operand doesn't have an address"
            )
        }
        else {
            f()
        }
    }

    val value : Int by lazy {
        val f = valueFunction

        if (f == null) {
            throw NotSupportedOperation(
                msg = "This operand doesn't have a value"
            )
        }
        else {
            f()
        }
    }

    override fun toString(): String {
        val addr = addrFunction.let {
            if (it == null) "null" else "0x%04X".format(addr)
        }
        val raw = raw.let {
            if (it == null) "null" else "0x%04X".format(it)
        }
        return "Operand(addr=$addr, raw=$raw)"
    }
}
