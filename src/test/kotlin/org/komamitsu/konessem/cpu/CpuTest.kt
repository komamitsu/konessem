package org.komamitsu.konessem.cpu

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.komamitsu.konessem.Interrupt

internal class CpuTest {
    lateinit var cpuBus: CpuBus

    @MockK
    lateinit var interrupt: Interrupt

    lateinit var cpu: Cpu

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        cpuBus = CpuBus(
            cpuRam = CpuRam(),
            ppu = mockk(),
            prgRom = mockk(),
            keyPad = mockk()
        )
        cpu = Cpu(cpuBus = cpuBus, interrupt = interrupt)
    }

    @Test
    fun pushAndPop() {
        cpu.register.sp = 0xFF
        assertEquals(0xFF, cpu.register.sp)

        cpu.stack.push(0x01)
        assertEquals(0xFE, cpu.register.sp)

        cpu.stack.push(0x23)
        assertEquals(0xFD, cpu.register.sp)

        cpu.stack.push(0xCD.toByte())
        assertEquals(0xFC, cpu.register.sp)

        cpu.stack.push(0xFE.toByte())
        assertEquals(0xFB, cpu.register.sp)

        assertEquals(0xFE, cpu.stack.pop())
        assertEquals(0xFC, cpu.register.sp)

        assertEquals(0xCD, cpu.stack.pop())
        assertEquals(0xFD, cpu.register.sp)

        assertEquals(0x23, cpu.stack.pop())
        assertEquals(0xFE, cpu.register.sp)

        assertEquals(0x01, cpu.stack.pop())
        assertEquals(0xFF, cpu.register.sp)
    }

    @Test
    fun pushWordAndPopWord() {
        cpu.register.sp = 0xFF
        assertEquals(0xFF, cpu.register.sp)

        cpu.stack.pushWord(0x0123)
        assertEquals(0xFD, cpu.register.sp)

        cpu.stack.pushWord(0xFEDC.toShort())
        assertEquals(0xFB, cpu.register.sp)

        assertEquals(0xFEDC, cpu.stack.popWord())
        assertEquals(0xFD, cpu.register.sp)

        assertEquals(0x0123, cpu.stack.popWord())
        assertEquals(0xFF, cpu.register.sp)
    }

    @Nested
    inner class Adc {
        @Test
        fun `NoCarry Positive NoOverflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0x50

            val opcode = Opcode(
                instruction = Instruction.ADC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0x10
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0x60, cpu.register.a)
            assertFalse(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertFalse(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }

        @Test
        fun `NoCarry Negative Overflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0x50

            val opcode = Opcode(
                instruction = Instruction.ADC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0x50
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0xA0, cpu.register.a)
            assertFalse(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertTrue(cpu.register.statusNegative)
            assertTrue(cpu.register.statusOverflow)
        }

        @Test
        fun `NoCarry Negative NoOverflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0x50

            val opcode = Opcode(
                instruction = Instruction.ADC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0x90
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0xE0, cpu.register.a)
            assertFalse(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertTrue(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }

        @Test
        fun `Carry Positive NoOverflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0x50

            val opcode = Opcode(
                instruction = Instruction.ADC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0xD0
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0x20, cpu.register.a)
            assertTrue(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertFalse(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }

        @Test
        fun `NoCarry Negative NoOverflow 2`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0xD0

            val opcode = Opcode(
                instruction = Instruction.ADC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0x10
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0xE0, cpu.register.a)
            assertFalse(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertTrue(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }

        @Test
        fun `Carry Positive NoOverflow 2`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0xD0

            val opcode = Opcode(
                instruction = Instruction.ADC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0x50
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0x20, cpu.register.a)
            assertTrue(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertFalse(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }

        @Test
        fun `Carry Positive Overflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0xD0

            val opcode = Opcode(
                instruction = Instruction.ADC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0x90
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0x60, cpu.register.a)
            assertTrue(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertFalse(cpu.register.statusNegative)
            assertTrue(cpu.register.statusOverflow)
        }

        @Test
        fun `Carry Negative Overflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0xD0

            val opcode = Opcode(
                instruction = Instruction.ADC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0xD0
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0xA0, cpu.register.a)
            assertTrue(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertTrue(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }
    }

    @Nested
    inner class Sbc {
        @Test
        fun `NoCarry Borrow Positive NoOverflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0x50
            cpu.register.statusCarry = true

            val opcode = Opcode(
                instruction = Instruction.SBC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0xF0
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0x60, cpu.register.a)
            assertFalse(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertFalse(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }

        @Test
        fun `NoCarry Borrow Negative Overflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0x50
            cpu.register.statusCarry = true

            val opcode = Opcode(
                instruction = Instruction.SBC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0xB0
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0xA0, cpu.register.a)
            assertFalse(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertTrue(cpu.register.statusNegative)
            assertTrue(cpu.register.statusOverflow)
        }

        @Test
        fun `NoCarry Borrow Negative NoOverflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0x50
            cpu.register.statusCarry = true

            val opcode = Opcode(
                instruction = Instruction.SBC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0x70
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0xE0, cpu.register.a)
            assertFalse(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertTrue(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }

        @Test
        fun `Carry NoBorrow Positive NoOverflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0x50
            cpu.register.statusCarry = true

            val opcode = Opcode(
                instruction = Instruction.SBC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0x30
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0x20, cpu.register.a)
            assertTrue(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertFalse(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }

        @Test
        fun `NoCarry Borrow Negative NoOverflow 2`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0xD0
            cpu.register.statusCarry = true

            val opcode = Opcode(
                instruction = Instruction.SBC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0xF0
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0xE0, cpu.register.a)
            assertFalse(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertTrue(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }

        @Test
        fun `Carry NoBorrow Positive NoOverflow 2`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0xD0
            cpu.register.statusCarry = true

            val opcode = Opcode(
                instruction = Instruction.SBC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0xB0
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0x20, cpu.register.a)
            assertTrue(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertFalse(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }

        @Test
        fun `Carry NoBorrow Positive Overflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0xD0
            cpu.register.statusCarry = true

            val opcode = Opcode(
                instruction = Instruction.SBC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0x70
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0x60, cpu.register.a)
            assertTrue(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertFalse(cpu.register.statusNegative)
            assertTrue(cpu.register.statusOverflow)
        }

        @Test
        fun `Carry NoBorrow Negative NoOverflow`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0xD0
            cpu.register.statusCarry = true

            val opcode = Opcode(
                instruction = Instruction.SBC,
                addressingMode = AddressingMode.IMMEDIATE,
                cycle = 42
            )
            val value = 0x30
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0xA0, cpu.register.a)
            assertTrue(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertTrue(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }
    }

    @Nested
    inner class Asl {
        @Test
        fun `Accumulator NoCarry`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0x1F

            val opcode = Opcode(
                instruction = Instruction.ASL,
                addressingMode = AddressingMode.ACCUMULATOR,
                cycle = 42
            )
            val value = cpu.register.a
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0x3E, cpu.register.a)
            assertFalse(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertFalse(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }

        @Test
        fun `Accumulator Carry`() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0xE7

            val opcode = Opcode(
                instruction = Instruction.ASL,
                addressingMode = AddressingMode.ACCUMULATOR,
                cycle = 42
            )
            val value = cpu.register.a
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0xCE, cpu.register.a)
            assertTrue(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertTrue(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }
    }

    @Nested
    inner class Eor {
        @Test
        fun test() {
            cpu.register.sp = 0xFF
            cpu.register.a = 0xD6

            val opcode = Opcode(
                instruction = Instruction.EOR,
                addressingMode = AddressingMode.ZERO_PAGE,
                cycle = 42
            )
            // a: 11010110
            // m: 10101010
            //-------------
            //    01111100
            val value = 0xAA
            val operand = Operand(valueFunction = { value })
            cpu.execOpcode(opcode = opcode, operand = operand)

            assertEquals(0x7C, cpu.register.a)
            assertFalse(cpu.register.statusCarry)
            assertFalse(cpu.register.statusZero)
            assertFalse(cpu.register.statusNegative)
            assertFalse(cpu.register.statusOverflow)
        }
    }
}
