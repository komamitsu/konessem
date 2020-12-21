package org.komamitsu.konessem

fun Byte.toUint(): Int {
    return this.toInt() and 0xFF
}
