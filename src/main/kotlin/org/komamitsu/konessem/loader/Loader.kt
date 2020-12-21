package org.komamitsu.konessem.loader

import org.komamitsu.konessem.rom.ChrRom
import org.komamitsu.konessem.rom.PrgRom
import java.nio.file.Files
import java.nio.file.Path

data class INesHeader(
    val sizeOfPrgRomIn16KbUnit: Int,
    val sizeOfChrRomIn8KbUnit: Int,
    val lowerNybbleOfMapperNumber: Int,
    val ignoreMirroring: Boolean,
    val trainerAt0x70xxF: Boolean,
    val otherExternalMemory: Boolean,
    val nametableMirroringVertical: Boolean,
    val upperNybbleOfMapperNumber: Int,
    val nes2Format: Boolean,
    val playChoice10: Boolean,
    val vsUnisystem: Boolean,
    val prgRamSize: Int,
    val tvSystemPal: Boolean,
    val boardHasBusConflicts: Boolean,
    val prgRamNotPresentAfter0x6xxx: Boolean,
    val tvSystemExt: Int
) {
    companion object {
        fun from(bytes: ByteArray): INesHeader {

            if (bytes[0] == 0x4E.toByte()
                && bytes[1] == 0x45.toByte()
                && bytes[2] == 0x53.toByte()
                && bytes[3] == 0x1A.toByte()) {
                // Valid
            }
            else {
                throw IllegalArgumentException("Invalid magic number")
            }

            val byte4 = bytes[4].toInt()
            val byte5 = bytes[5].toInt()
            val byte6 = bytes[6].toInt()
            val byte7 = bytes[7].toInt()
            val byte8 = bytes[8].toInt()
            val byte9 = bytes[9].toInt()
            val byte10 = bytes[10].toInt()

            return INesHeader(
                sizeOfPrgRomIn16KbUnit = byte4,
                sizeOfChrRomIn8KbUnit = byte5,
                lowerNybbleOfMapperNumber = byte6 and 0xF0 shr 4,
                ignoreMirroring = byte6 and 0x08 != 0x00,
                trainerAt0x70xxF = byte6 and 0x04 != 0x00,
                otherExternalMemory = byte6 and 0x02 != 0x00,
                nametableMirroringVertical = byte6 and 0x01 != 0x00,
                upperNybbleOfMapperNumber = byte7 and 0xF0 shr 4,
                nes2Format = byte7 and 0x0C shr 2 == 0x02,
                playChoice10 = byte7 and 0x02 != 0x00,
                vsUnisystem = byte7 and 0x01 != 0x00,
                prgRamSize = byte8,
                tvSystemPal = byte9 and 0x01 != 0x00,
                boardHasBusConflicts = byte10 and 0x20 != 0x00,
                prgRamNotPresentAfter0x6xxx = byte10 and 0x10 != 0x00,
                tvSystemExt = byte10 and 0x03
            )
        }
    }
}

class PrgRam(private val bytes: ByteArray)

class Loader {
    lateinit var iNesHeader: INesHeader
    lateinit var prgRom: PrgRom
    lateinit var chrRom: ChrRom
    lateinit var prgRam: PrgRam

    fun load(path: Path) {
        Files.newInputStream(path).use { input ->
            iNesHeader = ByteArray(16).let {
                input.read(it)
                INesHeader.from(it)
            }
            prgRom = ByteArray(iNesHeader.sizeOfPrgRomIn16KbUnit * 16 * 1024).let {
                input.read(it)
                PrgRom(it)
            }
            chrRom = ByteArray(iNesHeader.sizeOfChrRomIn8KbUnit * 8 * 1024).let {
                input.read(it)
                ChrRom(it)
            }
            prgRam = PrgRam(ByteArray(iNesHeader.prgRamSize * 8 * 1024))
        }
    }
}