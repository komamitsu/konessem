package org.komamitsu.konessem.cpu

internal data class NotSupportedOperation(
    val msg: String,
    val opcode: Opcode? = null,
    val operand: Operand? = null
) : Exception() {

    override val message: String
        get() {
            return this.toString()
        }
}