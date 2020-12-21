package org.komamitsu.konessem.cpu

data class NotSupportedOperation(
    val msg: String,
    val opcode: Opcode? = null,
    val operand: Operand? = null
) : Exception() {

    override val message: String
        get() {
            return this.toString()
        }
}