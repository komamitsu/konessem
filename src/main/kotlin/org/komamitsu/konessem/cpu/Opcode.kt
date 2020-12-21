package org.komamitsu.konessem.cpu

data class Opcode(
    val instruction: Instruction,
    val addressingMode: AddressingMode,
    val cycle: Int
) {
    companion object {
        val opcodeMap = mapOf(
            0x69 to Opcode(Instruction.ADC, AddressingMode.IMMEDIATE, 2),
            0x65 to Opcode(Instruction.ADC, AddressingMode.ZERO_PAGE, 3),
            0x75 to Opcode(Instruction.ADC, AddressingMode.ZERO_PAGE_X, 4),
            0x6D to Opcode(Instruction.ADC, AddressingMode.ABSOLUTE, 4),
            0x7D to Opcode(Instruction.ADC, AddressingMode.ABSOLUTE_X, 4),
            0x79 to Opcode(Instruction.ADC, AddressingMode.ABSOLUTE_Y, 4),
            0x61 to Opcode(Instruction.ADC, AddressingMode.INDEXED_INDIRECT, 6),
            0x71 to Opcode(Instruction.ADC, AddressingMode.INDIRECT_INDEXED, 5),

            0x29 to Opcode(Instruction.AND, AddressingMode.IMMEDIATE, 2),
            0x25 to Opcode(Instruction.AND, AddressingMode.ZERO_PAGE, 3),
            0x35 to Opcode(Instruction.AND, AddressingMode.ZERO_PAGE_X, 4),
            0x2D to Opcode(Instruction.AND, AddressingMode.ABSOLUTE, 4),
            0x3D to Opcode(Instruction.AND, AddressingMode.ABSOLUTE_X, 4),
            0x39 to Opcode(Instruction.AND, AddressingMode.ABSOLUTE_Y, 4),
            0x21 to Opcode(Instruction.AND, AddressingMode.INDEXED_INDIRECT, 6),
            0x31 to Opcode(Instruction.AND, AddressingMode.INDIRECT_INDEXED, 5),

            0x0A to Opcode(Instruction.ASL, AddressingMode.ACCUMULATOR, 2),
            0x06 to Opcode(Instruction.ASL, AddressingMode.ZERO_PAGE, 5),
            0x16 to Opcode(Instruction.ASL, AddressingMode.ZERO_PAGE_X, 6),
            0x0E to Opcode(Instruction.ASL, AddressingMode.ABSOLUTE, 6),
            0x1E to Opcode(Instruction.ASL, AddressingMode.ABSOLUTE_X, 7),

            0x90 to Opcode(Instruction.BCC, AddressingMode.RELATIVE, 2),

            0xB0 to Opcode(Instruction.BCS, AddressingMode.RELATIVE, 2),

            0xF0 to Opcode(Instruction.BEQ, AddressingMode.RELATIVE, 2),

            0x24 to Opcode(Instruction.BIT, AddressingMode.ZERO_PAGE, 3),
            0x2C to Opcode(Instruction.BIT, AddressingMode.ABSOLUTE, 4),

            0x30 to Opcode(Instruction.BMI, AddressingMode.RELATIVE, 2),

            0xD0 to Opcode(Instruction.BNE, AddressingMode.RELATIVE, 2),

            0x10 to Opcode(Instruction.BPL, AddressingMode.RELATIVE, 2),

            0x00 to Opcode(Instruction.BRK, AddressingMode.IMPLICIT, 7),

            0x50 to Opcode(Instruction.BVC, AddressingMode.RELATIVE, 2),

            0x70 to Opcode(Instruction.BVS, AddressingMode.RELATIVE, 2),

            0x18 to Opcode(Instruction.CLC, AddressingMode.IMPLICIT, 2),

            0xD8 to Opcode(Instruction.CLD, AddressingMode.IMPLICIT, 2),

            0x58 to Opcode(Instruction.CLI, AddressingMode.IMPLICIT, 2),

            0xB8 to Opcode(Instruction.CLV, AddressingMode.IMPLICIT, 2),

            0xC9 to Opcode(Instruction.CMP, AddressingMode.IMMEDIATE, 2),
            0xC5 to Opcode(Instruction.CMP, AddressingMode.ZERO_PAGE, 3),
            0xD5 to Opcode(Instruction.CMP, AddressingMode.ZERO_PAGE_X, 4),
            0xCD to Opcode(Instruction.CMP, AddressingMode.ABSOLUTE, 4),
            0xDD to Opcode(Instruction.CMP, AddressingMode.ABSOLUTE_X, 4),
            0xD9 to Opcode(Instruction.CMP, AddressingMode.ABSOLUTE_Y, 4),
            0xC1 to Opcode(Instruction.CMP, AddressingMode.INDEXED_INDIRECT, 6),
            0xD1 to Opcode(Instruction.CMP, AddressingMode.INDIRECT_INDEXED, 5),

            0xE0 to Opcode(Instruction.CPX, AddressingMode.IMMEDIATE, 2),
            0xE4 to Opcode(Instruction.CPX, AddressingMode.ZERO_PAGE, 3),
            0xEC to Opcode(Instruction.CPX, AddressingMode.ABSOLUTE, 4),

            0xC0 to Opcode(Instruction.CPY, AddressingMode.IMMEDIATE, 2),
            0xC4 to Opcode(Instruction.CPY, AddressingMode.ZERO_PAGE, 3),
            0xCC to Opcode(Instruction.CPY, AddressingMode.ABSOLUTE, 4),

            0xC6 to Opcode(Instruction.DEC, AddressingMode.ZERO_PAGE, 5),
            0xD6 to Opcode(Instruction.DEC, AddressingMode.ZERO_PAGE_X, 6),
            0xCE to Opcode(Instruction.DEC, AddressingMode.ABSOLUTE, 6),
            0xDE to Opcode(Instruction.DEC, AddressingMode.ABSOLUTE_X, 7),

            0xCA to Opcode(Instruction.DEX, AddressingMode.IMPLICIT, 2),

            0x88 to Opcode(Instruction.DEY, AddressingMode.IMPLICIT, 2),

            0x49 to Opcode(Instruction.EOR, AddressingMode.IMMEDIATE, 2),
            0x45 to Opcode(Instruction.EOR, AddressingMode.ZERO_PAGE, 3),
            0x55 to Opcode(Instruction.EOR, AddressingMode.ZERO_PAGE_X, 4),
            0x4D to Opcode(Instruction.EOR, AddressingMode.ABSOLUTE, 4),
            0x5D to Opcode(Instruction.EOR, AddressingMode.ABSOLUTE_X, 4),
            0x59 to Opcode(Instruction.EOR, AddressingMode.ABSOLUTE_Y, 4),
            0x41 to Opcode(Instruction.EOR, AddressingMode.INDEXED_INDIRECT, 6),
            0x51 to Opcode(Instruction.EOR, AddressingMode.INDIRECT_INDEXED, 5),

            0xE6 to Opcode(Instruction.INC, AddressingMode.ZERO_PAGE, 5),
            0xF6 to Opcode(Instruction.INC, AddressingMode.ZERO_PAGE_X, 6),
            0xEE to Opcode(Instruction.INC, AddressingMode.ABSOLUTE, 6),
            0xFE to Opcode(Instruction.INC, AddressingMode.ABSOLUTE_X, 7),

            0xE8 to Opcode(Instruction.INX, AddressingMode.IMPLICIT, 2),

            0xC8 to Opcode(Instruction.INY, AddressingMode.IMPLICIT, 2),

            0x4C to Opcode(Instruction.JMP, AddressingMode.ABSOLUTE, 3),
            0x6C to Opcode(Instruction.JMP, AddressingMode.INDIRECT, 5),

            0x20 to Opcode(Instruction.JSR, AddressingMode.ABSOLUTE, 6),

            0xA9 to Opcode(Instruction.LDA, AddressingMode.IMMEDIATE, 2),
            0xA5 to Opcode(Instruction.LDA, AddressingMode.ZERO_PAGE, 3),
            0xB5 to Opcode(Instruction.LDA, AddressingMode.ZERO_PAGE_X, 4),
            0xAD to Opcode(Instruction.LDA, AddressingMode.ABSOLUTE, 4),
            0xBD to Opcode(Instruction.LDA, AddressingMode.ABSOLUTE_X, 4),
            0xB9 to Opcode(Instruction.LDA, AddressingMode.ABSOLUTE_Y, 4),
            0xA1 to Opcode(Instruction.LDA, AddressingMode.INDEXED_INDIRECT, 6),
            0xB1 to Opcode(Instruction.LDA, AddressingMode.INDIRECT_INDEXED, 5),

            0xA2 to Opcode(Instruction.LDX, AddressingMode.IMMEDIATE, 2),
            0xA6 to Opcode(Instruction.LDX, AddressingMode.ZERO_PAGE, 3),
            0xB6 to Opcode(Instruction.LDX, AddressingMode.ZERO_PAGE_Y, 4),
            0xAE to Opcode(Instruction.LDX, AddressingMode.ABSOLUTE, 4),
            0xBE to Opcode(Instruction.LDX, AddressingMode.ABSOLUTE_Y, 4),

            0xA0 to Opcode(Instruction.LDY, AddressingMode.IMMEDIATE, 2),
            0xA4 to Opcode(Instruction.LDY, AddressingMode.ZERO_PAGE, 3),
            0xB4 to Opcode(Instruction.LDY, AddressingMode.ZERO_PAGE_X, 4),
            0xAC to Opcode(Instruction.LDY, AddressingMode.ABSOLUTE, 4),
            0xBC to Opcode(Instruction.LDY, AddressingMode.ABSOLUTE_X, 4),

            0x4A to Opcode(Instruction.LSR, AddressingMode.ACCUMULATOR, 2),
            0x46 to Opcode(Instruction.LSR, AddressingMode.ZERO_PAGE, 5),
            0x56 to Opcode(Instruction.LSR, AddressingMode.ZERO_PAGE_X, 6),
            0x4E to Opcode(Instruction.LSR, AddressingMode.ABSOLUTE, 6),
            0x5E to Opcode(Instruction.LSR, AddressingMode.ABSOLUTE_X, 7),

            0xEA to Opcode(Instruction.NOP, AddressingMode.IMPLICIT, 2),

            0x09 to Opcode(Instruction.ORA, AddressingMode.IMMEDIATE, 2),
            0x05 to Opcode(Instruction.ORA, AddressingMode.ZERO_PAGE, 3),
            0x15 to Opcode(Instruction.ORA, AddressingMode.ZERO_PAGE_X, 4),
            0x0D to Opcode(Instruction.ORA, AddressingMode.ABSOLUTE, 4),
            0x1D to Opcode(Instruction.ORA, AddressingMode.ABSOLUTE_X, 4),
            0x19 to Opcode(Instruction.ORA, AddressingMode.ABSOLUTE_Y, 4),
            0x01 to Opcode(Instruction.ORA, AddressingMode.INDEXED_INDIRECT, 6),
            0x11 to Opcode(Instruction.ORA, AddressingMode.INDIRECT_INDEXED, 5),

            0x48 to Opcode(Instruction.PHA, AddressingMode.IMPLICIT, 3),

            0x08 to Opcode(Instruction.PHP, AddressingMode.IMPLICIT, 3),

            0x68 to Opcode(Instruction.PLA, AddressingMode.IMPLICIT, 4),

            0x28 to Opcode(Instruction.PLP, AddressingMode.IMPLICIT, 4),

            0x2A to Opcode(Instruction.ROL, AddressingMode.ACCUMULATOR, 2),
            0x26 to Opcode(Instruction.ROL, AddressingMode.ZERO_PAGE, 5),
            0x36 to Opcode(Instruction.ROL, AddressingMode.ZERO_PAGE_X, 6),
            0x2E to Opcode(Instruction.ROL, AddressingMode.ABSOLUTE, 6),
            0x3E to Opcode(Instruction.ROL, AddressingMode.ABSOLUTE_X, 7),

            0x6A to Opcode(Instruction.ROR, AddressingMode.ACCUMULATOR, 2),
            0x66 to Opcode(Instruction.ROR, AddressingMode.ZERO_PAGE, 5),
            0x76 to Opcode(Instruction.ROR, AddressingMode.ZERO_PAGE_X, 6),
            0x6E to Opcode(Instruction.ROR, AddressingMode.ABSOLUTE, 6),
            0x7E to Opcode(Instruction.ROR, AddressingMode.ABSOLUTE_X, 7),

            0x40 to Opcode(Instruction.RTI, AddressingMode.IMPLICIT, 6),

            0x60 to Opcode(Instruction.RTS, AddressingMode.IMPLICIT, 6),

            0xE9 to Opcode(Instruction.SBC, AddressingMode.IMMEDIATE, 2),
            // Unofficial
            0xEB to Opcode(Instruction.SBC, AddressingMode.IMMEDIATE, 2),
            0xE5 to Opcode(Instruction.SBC, AddressingMode.ZERO_PAGE, 3),
            0xF5 to Opcode(Instruction.SBC, AddressingMode.ZERO_PAGE_X, 4),
            0xED to Opcode(Instruction.SBC, AddressingMode.ABSOLUTE, 4),
            0xFD to Opcode(Instruction.SBC, AddressingMode.ABSOLUTE_X, 4),
            0xF9 to Opcode(Instruction.SBC, AddressingMode.ABSOLUTE_Y, 4),
            0xE1 to Opcode(Instruction.SBC, AddressingMode.INDEXED_INDIRECT, 6),
            0xF1 to Opcode(Instruction.SBC, AddressingMode.INDIRECT_INDEXED, 5),

            0x38 to Opcode(Instruction.SEC, AddressingMode.IMPLICIT, 2),

            0xF8 to Opcode(Instruction.SED, AddressingMode.IMPLICIT, 2),

            0x78 to Opcode(Instruction.SEI, AddressingMode.IMPLICIT, 2),

            0x85 to Opcode(Instruction.STA, AddressingMode.ZERO_PAGE, 3),
            0x95 to Opcode(Instruction.STA, AddressingMode.ZERO_PAGE_X, 4),
            0x8D to Opcode(Instruction.STA, AddressingMode.ABSOLUTE, 4),
            0x9D to Opcode(Instruction.STA, AddressingMode.ABSOLUTE_X, 5),
            0x99 to Opcode(Instruction.STA, AddressingMode.ABSOLUTE_Y, 5),
            0x81 to Opcode(Instruction.STA, AddressingMode.INDEXED_INDIRECT, 6),
            0x91 to Opcode(Instruction.STA, AddressingMode.INDIRECT_INDEXED, 6),

            0x86 to Opcode(Instruction.STX, AddressingMode.ZERO_PAGE, 3),
            0x96 to Opcode(Instruction.STX, AddressingMode.ZERO_PAGE_Y, 4),
            0x8E to Opcode(Instruction.STX, AddressingMode.ABSOLUTE, 4),

            0x84 to Opcode(Instruction.STY, AddressingMode.ZERO_PAGE, 3),
            0x94 to Opcode(Instruction.STY, AddressingMode.ZERO_PAGE_X, 4),
            0x8C to Opcode(Instruction.STY, AddressingMode.ABSOLUTE, 4),

            0xAA to Opcode(Instruction.TAX, AddressingMode.IMPLICIT, 2),

            0xA8 to Opcode(Instruction.TAY, AddressingMode.IMPLICIT, 2),

            0xBA to Opcode(Instruction.TSX, AddressingMode.IMPLICIT, 2),

            0x8A to Opcode(Instruction.TXA, AddressingMode.IMPLICIT, 2),

            0x9A to Opcode(Instruction.TXS, AddressingMode.IMPLICIT, 2),

            0x98 to Opcode(Instruction.TYA, AddressingMode.IMPLICIT, 2),

            0x4B to Opcode(Instruction.ALR, AddressingMode.IMMEDIATE, 2),
            0x0B to Opcode(Instruction.ANC, AddressingMode.IMMEDIATE, 2),
            0x2B to Opcode(Instruction.ANC, AddressingMode.IMMEDIATE, 2),
            0x6B to Opcode(Instruction.ARR, AddressingMode.IMMEDIATE, 2),
            0xCB to Opcode(Instruction.AXS, AddressingMode.IMMEDIATE, 2),

            0xA3 to Opcode(Instruction.LAX, AddressingMode.INDEXED_INDIRECT, 6),
            0xA7 to Opcode(Instruction.LAX, AddressingMode.ZERO_PAGE, 3),
            0xAF to Opcode(Instruction.LAX, AddressingMode.ABSOLUTE, 4),
            0xB3 to Opcode(Instruction.LAX, AddressingMode.INDIRECT_INDEXED, 5),
            0xB7 to Opcode(Instruction.LAX, AddressingMode.ZERO_PAGE_Y, 4),
            0xBF to Opcode(Instruction.LAX, AddressingMode.ABSOLUTE_Y, 4),

            0x83 to Opcode(Instruction.SAX, AddressingMode.INDEXED_INDIRECT, 6),
            0x87 to Opcode(Instruction.SAX, AddressingMode.ZERO_PAGE, 3),
            0x8F to Opcode(Instruction.SAX, AddressingMode.ABSOLUTE, 4),
            0x97 to Opcode(Instruction.SAX, AddressingMode.ZERO_PAGE_Y, 4),

            0xC3 to Opcode(Instruction.DCP, AddressingMode.INDEXED_INDIRECT, 8),
            0xC7 to Opcode(Instruction.DCP, AddressingMode.ZERO_PAGE, 5),
            0xCF to Opcode(Instruction.DCP, AddressingMode.ABSOLUTE, 6),
            0xD3 to Opcode(Instruction.DCP, AddressingMode.INDIRECT_INDEXED, 8),
            0xD7 to Opcode(Instruction.DCP, AddressingMode.ZERO_PAGE_X, 6),
            0xDB to Opcode(Instruction.DCP, AddressingMode.ABSOLUTE_Y, 7),
            0xDF to Opcode(Instruction.DCP, AddressingMode.ABSOLUTE_X, 7),

            0xE3 to Opcode(Instruction.ISC, AddressingMode.INDEXED_INDIRECT, 8),
            0xE7 to Opcode(Instruction.ISC, AddressingMode.ZERO_PAGE, 5),
            0xEF to Opcode(Instruction.ISC, AddressingMode.ABSOLUTE, 6),
            0xF3 to Opcode(Instruction.ISC, AddressingMode.INDIRECT_INDEXED, 8),
            0xF7 to Opcode(Instruction.ISC, AddressingMode.ZERO_PAGE_X, 6),
            0xFB to Opcode(Instruction.ISC, AddressingMode.ABSOLUTE_Y, 7),
            0xFF to Opcode(Instruction.ISC, AddressingMode.ABSOLUTE_X, 7),

            0x23 to Opcode(Instruction.RLA, AddressingMode.INDEXED_INDIRECT, 8),
            0x27 to Opcode(Instruction.RLA, AddressingMode.ZERO_PAGE, 5),
            0x2F to Opcode(Instruction.RLA, AddressingMode.ABSOLUTE, 6),
            0x33 to Opcode(Instruction.RLA, AddressingMode.INDIRECT_INDEXED, 8),
            0x37 to Opcode(Instruction.RLA, AddressingMode.ZERO_PAGE_X, 6),
            0x3B to Opcode(Instruction.RLA, AddressingMode.ABSOLUTE_Y, 7),
            0x3F to Opcode(Instruction.RLA, AddressingMode.ABSOLUTE_X, 7),

            0x63 to Opcode(Instruction.RRA, AddressingMode.INDEXED_INDIRECT, 8),
            0x67 to Opcode(Instruction.RRA, AddressingMode.ZERO_PAGE, 5),
            0x6F to Opcode(Instruction.RRA, AddressingMode.ABSOLUTE, 6),
            0x73 to Opcode(Instruction.RRA, AddressingMode.INDIRECT_INDEXED, 8),
            0x77 to Opcode(Instruction.RRA, AddressingMode.ZERO_PAGE_X, 6),
            0x7B to Opcode(Instruction.RRA, AddressingMode.ABSOLUTE_Y, 7),
            0x7F to Opcode(Instruction.RRA, AddressingMode.ABSOLUTE_X, 7),

            0x03 to Opcode(Instruction.SLO, AddressingMode.INDEXED_INDIRECT, 8),
            0x07 to Opcode(Instruction.SLO, AddressingMode.ZERO_PAGE, 5),
            0x0F to Opcode(Instruction.SLO, AddressingMode.ABSOLUTE, 6),
            0x13 to Opcode(Instruction.SLO, AddressingMode.INDIRECT_INDEXED, 8),
            0x17 to Opcode(Instruction.SLO, AddressingMode.ZERO_PAGE_X, 6),
            0x1B to Opcode(Instruction.SLO, AddressingMode.ABSOLUTE_Y, 7),
            0x1F to Opcode(Instruction.SLO, AddressingMode.ABSOLUTE_X, 7),

            0x43 to Opcode(Instruction.SRE, AddressingMode.INDEXED_INDIRECT, 8),
            0x47 to Opcode(Instruction.SRE, AddressingMode.ZERO_PAGE, 5),
            0x4F to Opcode(Instruction.SRE, AddressingMode.ABSOLUTE, 6),
            0x53 to Opcode(Instruction.SRE, AddressingMode.INDIRECT_INDEXED, 8),
            0x57 to Opcode(Instruction.SRE, AddressingMode.ZERO_PAGE_X, 6),
            0x5B to Opcode(Instruction.SRE, AddressingMode.ABSOLUTE_Y, 7),
            0x5F to Opcode(Instruction.SRE, AddressingMode.ABSOLUTE_X, 7),

            0x1A to Opcode(Instruction.NOP, AddressingMode.IMPLICIT, 2),
            0x3A to Opcode(Instruction.NOP, AddressingMode.IMPLICIT, 2),
            0x5A to Opcode(Instruction.NOP, AddressingMode.IMPLICIT, 2),
            0x7A to Opcode(Instruction.NOP, AddressingMode.IMPLICIT, 2),
            0xDA to Opcode(Instruction.NOP, AddressingMode.IMPLICIT, 2),
            0xFA to Opcode(Instruction.NOP, AddressingMode.IMPLICIT, 2),

            0x80 to Opcode(Instruction.SKB, AddressingMode.IMMEDIATE, 2),
            0x82 to Opcode(Instruction.SKB, AddressingMode.IMMEDIATE, 2),
            0x89 to Opcode(Instruction.SKB, AddressingMode.IMMEDIATE, 2),
            0xC2 to Opcode(Instruction.SKB, AddressingMode.IMMEDIATE, 2),
            0xE2 to Opcode(Instruction.SKB, AddressingMode.IMMEDIATE, 2),

            0x0C to Opcode(Instruction.IGN, AddressingMode.ABSOLUTE, 4),
            0x1C to Opcode(Instruction.IGN, AddressingMode.ABSOLUTE_X, 5),
            0x3C to Opcode(Instruction.IGN, AddressingMode.ABSOLUTE_X, 5),
            0x5C to Opcode(Instruction.IGN, AddressingMode.ABSOLUTE_X, 5),
            0x7C to Opcode(Instruction.IGN, AddressingMode.ABSOLUTE_X, 5),
            0xDC to Opcode(Instruction.IGN, AddressingMode.ABSOLUTE_X, 5),
            0xFC to Opcode(Instruction.IGN, AddressingMode.ABSOLUTE_X, 5),
            0x04 to Opcode(Instruction.IGN, AddressingMode.ZERO_PAGE, 3),
            0x44 to Opcode(Instruction.IGN, AddressingMode.ZERO_PAGE, 3),
            0x64 to Opcode(Instruction.IGN, AddressingMode.ZERO_PAGE, 3),
            0x14 to Opcode(Instruction.IGN, AddressingMode.ZERO_PAGE_X, 4),
            0x34 to Opcode(Instruction.IGN, AddressingMode.ZERO_PAGE_X, 4),
            0x54 to Opcode(Instruction.IGN, AddressingMode.ZERO_PAGE_X, 4),
            0x74 to Opcode(Instruction.IGN, AddressingMode.ZERO_PAGE_X, 4),
            0xD4 to Opcode(Instruction.IGN, AddressingMode.ZERO_PAGE_X, 4),
            0xF4 to Opcode(Instruction.IGN, AddressingMode.ZERO_PAGE_X, 4)
        )

        fun find(code: Int): Opcode {
            return opcodeMap.getValue(code)
        }
    }
}

enum class Instruction {
    ADC,    // add with carry
    AND,    // and (with accumulator)
    ASL,    // arithmetic shift left
    BCC,    // branch on carry clear
    BCS,    // branch on carry set
    BEQ,    // branch on equal (zero set)
    BIT,    // bit test
    BMI,    // branch on minus (negative set)
    BNE,    // branch on not equal (zero clear)
    BPL,    // branch on plus (negative clear)
    BRK,    // break / interrupt
    BVC,    // branch on overflow clear
    BVS,    // on overflow set
    CLC,    // clear carry
    CLD,    // clear decimal
    CLI,    // clear interrupt disable
    CLV,    // clear overflow
    CMP,    // compare (with accumulator)
    CPX,    // compare with X
    CPY,    // compare with Y
    DEC,    // decrement
    DEX,    // decrement X
    DEY,    // decrement Y
    EOR,    // exclusive or (with accumulator)
    INC,    // increment
    INX,    // increment X
    INY,    // increment Y
    JMP,    // jump
    JSR,    // jump subroutine
    LDA,    // load accumulator
    LDX,    // load X
    LDY,    // load Y
    LSR,    // logical shift right
    NOP,    // no operation
    ORA,    // or with accumulator
    PHA,    // push accumulator
    PHP,    // push processor status (SR)
    PLA,    // pull accumulator
    PLP,    // pull processor status (SR)
    ROL,    // rotate left
    ROR,    // rotate right
    RTI,    // return from interrupt
    RTS,    // return from subroutine
    SBC,    // subtract with carry
    SEC,    // set carry
    SED,    // set decimal
    SEI,    // set interrupt disable
    STA,    // store accumulator
    STX,    // store X
    STY,    // store Y
    TAX,    // transfer accumulator to X
    TAY,    // transfer accumulator to Y
    TSX,    // transfer stack pointer to X
    TXA,    // transfer X to accumulator
    TXS,    // transfer X to stack pointer
    TYA,    // transfer Y to accumulator

    // https://wiki.nesdev.com/w/index.php/Programming_with_unofficial_opcodes

    ALR,    // Equivalent to AND #i then LSR A
    ANC,    // Does AND #i, setting N and Z flags based on the result. Then it copies N (bit 7) to C
    ARR,    // Similar to AND #i then ROR A, except sets the flags differently. N and Z are normal, but C is bit 6 and V is bit 6 xor bit 5
    AXS,    // Sets X to {(A AND X) - #value without borrow}, and updates NZC
    LAX,    // Shortcut for LDA value then TAX
    SAX,    // Stores the bitwise AND of A and X
    DCP,    // Equivalent to DEC value then CMP value
    ISC,    // Equivalent to INC value then SBC value
    RLA,    // Equivalent to ROL value then AND value
    RRA,    // Equivalent to ROR value then ADC value
    SLO,    // Equivalent to ASL value then ORA value
    SRE,    // Equivalent to LSR value then EOR value
    SKB,    // These unofficial opcodes just read an immediate byte and skip it
    IGN,    // Reads from memory at the specified address and ignores the value
}

enum class AddressingMode {
    IMPLICIT,
    ACCUMULATOR,
    IMMEDIATE,
    ZERO_PAGE,
    ZERO_PAGE_X,
    ZERO_PAGE_Y,
    RELATIVE,
    ABSOLUTE,
    ABSOLUTE_X,
    ABSOLUTE_Y,
    INDIRECT,
    INDEXED_INDIRECT,
    INDIRECT_INDEXED,
}
