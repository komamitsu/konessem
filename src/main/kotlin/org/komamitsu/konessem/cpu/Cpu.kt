package org.komamitsu.konessem.cpu

import org.komamitsu.konessem.Address
import org.komamitsu.konessem.Interrupt
import org.komamitsu.konessem.toUint
import java.lang.RuntimeException

class Cpu(
    private val cpuBus: CpuBus,
    private val interrupt: Interrupt
) {
    internal val register = Register()
    internal val stack = Stack(cpuBus = cpuBus, register = register)

    fun reset() {
        register.pc = cpuBus.readWord(Address(0xFFFC))
        register.sp = 0xFF
        register.statusInterruptDisabled = true
        register.statusDecimalMode = false
    }

    private fun processNmi() {
        register.statusBreakMode = false
        val pc = register.pc
        stack.push((pc shr 8).toByte())
        stack.push((pc and 0xFF).toByte())
        stack.push(register.sp.toByte())
        register.statusInterruptDisabled = true
        val addr = cpuBus.readWord(Address(0xFFFA))
        register.pc = addr
    }

    fun fetch(): Int {
        val byte = cpuBus.read(register.pcAsAddress())
        register.pc++
        return byte
    }

    fun fetchWord(): Int {
        val word = cpuBus.readWord(register.pcAsAddress())
        register.pc++
        register.pc++
        return word
    }

    fun fetchAsAddress() = Address(fetch())

    fun fetchWordAsAddress() = Address(fetchWord())

    private fun fetchOperand(addressingMode: AddressingMode): Operand {
        return when (addressingMode) {
            AddressingMode.IMPLICIT -> Operand()
            AddressingMode.ACCUMULATOR -> Operand(
                valueFunction = { register.a }
            )
            AddressingMode.IMMEDIATE -> fetch().let { value ->
                Operand(
                    raw = value,
                    valueFunction = { value }
                )
            }
            AddressingMode.ZERO_PAGE -> fetchAsAddress().let { addr ->
                val calculatedAddr = addr.lsb
                Operand(
                    raw = addr.value,
                    addrFunction = { calculatedAddr },
                    valueFunction = { read(calculatedAddr) }
                )
            }
            AddressingMode.ZERO_PAGE_X -> fetchAsAddress().let { addr ->
                val calculatedAddr = addr.plus(register.x).lsb
                Operand(
                    raw = addr.value,
                    addrFunction = { calculatedAddr },
                    valueFunction = { read(calculatedAddr) }
                )
            }
            AddressingMode.ZERO_PAGE_Y -> fetchAsAddress().let { addr ->
                val calculatedAddr = addr.plus(register.y).lsb
                Operand(
                    raw = addr.value,
                    addrFunction = { calculatedAddr },
                    valueFunction = { read(calculatedAddr) }
                )
            }
            AddressingMode.RELATIVE -> fetch().let { value ->
                val x = register.pc + value.toByte()
                Operand(
                    raw = value,
                    valueFunction = { x }
                )
            }
            AddressingMode.ABSOLUTE -> fetchWordAsAddress().let { addr ->
                Operand(
                    raw = addr.value,
                    addrFunction = { addr },
                    valueFunction = { read(addr) }
                )
            }
            AddressingMode.ABSOLUTE_X -> fetchWordAsAddress().let { addr ->
                val calculatedAddr = addr.plus(register.x)
                Operand(
                    raw = addr.value,
                    addrFunction = { calculatedAddr },
                    valueFunction = { read(calculatedAddr) }
                )
            }
            AddressingMode.ABSOLUTE_Y -> fetchWordAsAddress().let { addr ->
                val calculatedAddr = addr.plus(register.y)
                Operand(
                    raw = addr.value,
                    addrFunction = { calculatedAddr },
                    valueFunction = { read(calculatedAddr) }
                )
            }
            AddressingMode.INDIRECT -> fetchWordAsAddress().let { addr ->
                // https://everything2.com/title/6502+indirect+JMP+bug
                val calculatedAddr = Address(
                    read(Address(addr.value))
                            or (read(Address((addr.value and 0xFF00) or ((addr.value + 1) and 0x00FF))) shl 8)
                )
                Operand(
                    raw = addr.value,
                    addrFunction = { calculatedAddr }
                )
            }
            AddressingMode.INDEXED_INDIRECT -> fetchAsAddress().let { addr ->
                val baseAddr = addr.plus(register.x)
                val calculatedAddr = Address(read(baseAddr.lsb) + (read(baseAddr.plus(1).lsb) shl 8))
                Operand(
                    raw = addr.value,
                    addrFunction = { calculatedAddr },
                    valueFunction = { read(calculatedAddr) }
                )
            }
            AddressingMode.INDIRECT_INDEXED -> fetchAsAddress().let { addr ->
                val calculatedAddr = Address((read(addr.lsb) + (read(addr.plus(1).lsb) shl 8) + register.y) and 0xFFFF)
                Operand(
                    raw = addr.value,
                    addrFunction = { calculatedAddr },
                    valueFunction = { read(calculatedAddr) }
                )
            }
        }
    }

    private fun areSameSigns(a: Int, b: Int): Boolean {
        return (a xor b) and 0x80 == 0
    }

    private fun areDifferentSigns(a: Int, b: Int): Boolean {
        return !areSameSigns(a, b)
    }

    private fun read(addr: Address): Int {
        return cpuBus.read(addr)
    }

    private fun readWord(addr: Address): Int {
        return cpuBus.readWord(addr)
    }

    private fun write(addr: Address, value: Byte) {
        return cpuBus.write(addr, value)
    }

    private fun execOpcode(opcode: Opcode, operand: Operand) {
        fun jumpRelativelyIf(cond: () -> Boolean) {
            if (cond()) {
                // AddressingMode.RELATIVE has a calculated PC as a value
                register.pc = operand.value and 0xFFFF
            }
        }

        fun compareWithRegister(x: Int, value: Int = operand.value) {
            val diff = x - value
            register.statusCarry = diff >= 0
            register.updateStatusZeroAndNegative(diff)
        }

        fun rotate(f: (Int, Int) -> Pair<Boolean, Byte>): Byte {
            val (newCarry, newValue) = f(register.statusCarryAsInt, operand.value)

            register.statusCarry = newCarry
            if (opcode.addressingMode == AddressingMode.ACCUMULATOR) {
                register.a = newValue.toUint()
            }
            else {
                write(operand.addr, newValue)
                register.updateStatusZeroAndNegative(newValue.toUint())
            }
            return newValue
        }

        fun ror() = rotate { carryAsInt, value ->
            val newCarry = value and 0x01 != 0
            val newValue = (value shr 1 or (carryAsInt shl 7)).toByte()
            Pair(newCarry, newValue)
        }

        fun rol() = rotate { carryAsInt, value ->
            val newCarry = value and 0x80 != 0
            val newValue = (value shl 1 or carryAsInt).toByte()
            Pair(newCarry, newValue)
        }

        fun add(value: Int) {
            val origA = register.a
            val result = register.a + register.statusCarryAsInt + value
            register.a = result and 0xFF
            register.statusCarry = result > 0xFF
            register.statusOverflow = areSameSigns(origA, value) && areDifferentSigns(origA, register.a)
        }

        fun and(value: Int) {
            register.a = register.a and value
        }

        fun updateMemory(value: Byte) {
            register.updateStatusZeroAndNegative(value.toUint())
            write(operand.addr, value)
        }

        fun asl(value: Int, addressingMode: AddressingMode = opcode.addressingMode): Int {
            register.statusCarry = value and 0x80 != 0
            val result = value shl 1
            if (addressingMode == AddressingMode.ACCUMULATOR) {
                register.a = result
            }
            else {
                updateMemory(result.toByte())
            }
            return result
        }

        fun lsr(value: Int, addressingMode: AddressingMode = opcode.addressingMode): Int {
            register.statusCarry = value and 0x01 != 0
            val result = value shr 1
            if (addressingMode == AddressingMode.ACCUMULATOR) {
                register.a = result
            }
            else {
                updateMemory(result.toByte())
            }
            return result
        }

        when (opcode.instruction) {
            Instruction.ADC -> add(operand.value and 0xFF)
            Instruction.AND -> and(operand.value)
            Instruction.ASL -> asl(operand.value)
            Instruction.BCC -> jumpRelativelyIf { !register.statusCarry }
            Instruction.BCS -> jumpRelativelyIf { register.statusCarry }
            Instruction.BEQ -> jumpRelativelyIf { register.statusZero }
            Instruction.BIT -> {
                register.statusNegative = operand.value and 0x80 != 0
                register.statusOverflow = operand.value and 0x40 != 0
                register.statusZero = operand.value and register.a and 0xFF == 0
            }
            Instruction.BMI -> jumpRelativelyIf { register.statusNegative }
            Instruction.BNE -> jumpRelativelyIf { !register.statusZero }
            Instruction.BPL -> jumpRelativelyIf { !register.statusNegative }
            Instruction.BRK -> {
                if (register.statusInterruptDisabled) {
                    return
                }
                stack.pushWord(register.pc.toShort())
                stack.push(register.status.toByte())
                register.statusBreakMode = true
                register.statusInterruptDisabled = true
                register.pc = readWord(Address(0xFFFE))
            }
            Instruction.BVC -> jumpRelativelyIf { !register.statusOverflow }
            Instruction.BVS -> jumpRelativelyIf { register.statusOverflow }
            Instruction.CLC -> register.statusCarry = false
            Instruction.CLD -> register.statusDecimalMode = false
            Instruction.CLI -> register.statusInterruptDisabled = false
            Instruction.CLV -> register.statusOverflow = false
            Instruction.CMP -> compareWithRegister(register.a)
            Instruction.CPX -> compareWithRegister(register.x)
            Instruction.CPY -> compareWithRegister(register.y)
            Instruction.DEC -> updateMemory((operand.value - 1).toByte())
            Instruction.DEX -> register.x--
            Instruction.DEY -> register.y--
            Instruction.EOR -> register.a = register.a xor operand.value
            Instruction.INC -> updateMemory((operand.value + 1).toByte())
            Instruction.INX -> register.x++
            Instruction.INY -> register.y++
            Instruction.JMP -> register.pc = operand.addr.value
            Instruction.JSR -> {
                stack.pushWord((register.pc - 1).toShort())
                register.pc = operand.addr.value
            }
            Instruction.LDA -> register.a = operand.value
            Instruction.LDX -> register.x = operand.value
            Instruction.LDY -> register.y = operand.value
            Instruction.LSR -> lsr(operand.value)
            Instruction.NOP -> {
            }
            Instruction.ORA -> register.a = register.a or operand.value
            Instruction.PHA -> stack.push(register.a.toByte())
            Instruction.PHP -> stack.push((register.status or 0x30).toByte())
            Instruction.PLA -> register.a = stack.pop()
            Instruction.PLP -> register.status = stack.pop() and 0xCF
            Instruction.ROL -> rol()
            Instruction.ROR -> ror()
            Instruction.RTI -> {
                register.status = stack.pop()
                register.pc = stack.popWord()
            }
            Instruction.RTS -> {
                register.pc = stack.popWord()
                register.pc++
            }
            Instruction.SBC -> add(operand.value.inv() and 0xFF)
            Instruction.SEC -> register.statusCarry = true
            Instruction.SED -> register.statusDecimalMode = true
            Instruction.SEI -> register.statusInterruptDisabled = true
            Instruction.STA -> write(operand.addr, register.a.toByte())
            Instruction.STX -> write(operand.addr, register.x.toByte())
            Instruction.STY -> write(operand.addr, register.y.toByte())
            Instruction.TAX -> register.x = register.a
            Instruction.TAY -> register.y = register.a
            Instruction.TSX -> register.x = register.sp
            Instruction.TXA -> register.a = register.x
            Instruction.TXS -> register.sp = register.x
            Instruction.TYA -> register.a = register.y

            Instruction.ALR -> {
                // AND
                and(operand.value)
                // LSR
                lsr(register.a, AddressingMode.ACCUMULATOR)
            }
            Instruction.ANC -> {
                and(operand.value)
                register.statusCarry = register.statusNegative
            }
            Instruction.ARR -> {
                and(operand.value)

                val newValue = operand.value shr 1 or (register.statusCarryAsInt shl 7)
                register.a = newValue
                register.statusCarry = newValue and 0x40 != 0
                register.statusOverflow = (newValue and 0x40 != 0).xor(newValue and 0x20 != 0)
            }
            Instruction.AXS -> register.x = register.a and register.x
            Instruction.LAX -> {
                // LDA
                register.a = operand.value
                // TXA
                register.a = register.x
            }
            Instruction.SAX -> write(operand.addr, (register.a and register.x).toByte())
            Instruction.DCP -> {
                // DEC
                val newValue = operand.value - 1
                updateMemory(newValue.toByte())
                // CMP
                compareWithRegister(
                    x = register.a,
                    value = newValue
                )
            }
            Instruction.ISC -> {
                // INC
                val newValue = operand.value + 1
                updateMemory(newValue.toByte())
                // SBC
                add((newValue.inv() and 0xFF) + 1)
            }
            Instruction.RLA -> and(rol().toUint())
            Instruction.RRA -> add(ror().toUint())
            Instruction.SLO -> register.a = register.a or asl(operand.value)
            Instruction.SRE -> register.a = register.a xor lsr(operand.value)
            Instruction.SKB -> operand.value
            Instruction.IGN -> operand.value
        }
    }

    fun run(): Int {
        if (interrupt.nmi) {
            processNmi()
            interrupt.nmi = false
        }

        val code = fetch()
        val opcode = Opcode.find(code)
        val operand = fetchOperand(opcode.addressingMode)

        try {
            execOpcode(opcode, operand)
        }
        catch (e: Throwable) {
            throw RuntimeException("Failed to exec opcode:$opcode, operand:$operand", e)
        }

        return opcode.cycle
    }

    override fun toString(): String {
        return "Cpu(interrupt=$interrupt, register=$register)"
    }
}