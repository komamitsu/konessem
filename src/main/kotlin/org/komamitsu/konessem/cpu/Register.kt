package org.komamitsu.konessem.cpu

class Register {
    var sp: Int = 0
        set(value) {
            field = value and 0xFF
        }

    var status: Int = 0x20
        set(value) {
            field = (value or 0x20) and 0xFF
        }

    var pc: Int = 0

    var a: Int = 0
        set(value) {
            val v = value and 0xFF
            field = v
            updateStatusZeroAndNegative(v)
        }

    var x: Int = 0
        set(value) {
            val v = value and 0xFF
            field = v
            updateStatusZeroAndNegative(v)
        }

    var y: Int = 0
        set(value) {
            val v = value and 0xFF
            field = v
            updateStatusZeroAndNegative(v)
        }

    var statusNegative: Boolean
        get() = getterOfStatus(0x80)()
        set(value) = setterOfStatus(0x80)(value)

    var statusOverflow: Boolean
        get() = getterOfStatus(0x40)()
        set(value) = setterOfStatus(0x40)(value)

    var statusBreakMode: Boolean
        get() = getterOfStatus(0x10)()
        set(value) = setterOfStatus(0x10)(value)

    var statusDecimalMode: Boolean
        get() = getterOfStatus(0x08)()
        set(value) = setterOfStatus(0x08)(value)

    var statusInterruptDisabled: Boolean
        get() = getterOfStatus(0x04)()
        set(value) = setterOfStatus(0x04)(value)

    var statusZero: Boolean
        get() = getterOfStatus(0x02)()
        set(value) = setterOfStatus(0x02)(value)

    var statusCarry: Boolean
        get() = getterOfStatus(0x01)()
        set(value) = setterOfStatus(0x01)(value)

    val statusCarryAsInt: Int
        get() = if (statusCarry) { 1 } else { 0 }

    fun updateStatusZeroAndNegative(value: Int) {
        statusNegative = value and 0x80 != 0
        statusZero = value == 0
    }

    private fun getterOfStatus(bitmask: Int): () -> Boolean {
        return { status and bitmask != 0 }
    }

    private fun setterOfStatus(bitmask: Int): (Boolean) -> Unit {
        return { value ->
            status = if (value) {
                status or bitmask
            }
            else {
                status and bitmask.inv()
            }
        }
    }

    override fun toString(): String {
        return "Register(sp=0x%02X, status=0x%02X, pc=0x%02X, a=0x%02X, x=0x%02X, y=0x%02X)".format(sp, status, pc, a, x, y)
    }
}