package org.komamitsu.konessem.ppu

import javafx.scene.image.WritableImage
import javafx.scene.image.WritablePixelFormat
import org.komamitsu.konessem.Address
import org.komamitsu.konessem.Interrupt
import org.komamitsu.konessem.rom.ChrRom
import org.komamitsu.konessem.toUint
import java.nio.IntBuffer

class Ppu(
    private val interrupt: Interrupt,
    private val nametableMirroringVertical: Boolean,
    ramBytes: ByteArray
) {
    companion object {
        const val sizeOfSpriteData = 16

        const val cyclesPerLine = 341

        const val zoomRate = 4
        const val bitsInChar = 8
        const val bytesOfOneColorSprite = 8

        const val registerAddrOfWriteStatus0 = 0x0000
        const val registerAddrOfWriteStatus1 = 0x0001
        const val registerAddrOfReadStatus = 0x0002
        const val registerAddrOfSpriteRamAddr = 0x0003
        const val registerAddrOfSpriteRamData = 0x0004
        const val registerAddrOfScrollOffset = 0x0005
        const val registerAddrOfPpuAddr = 0x0006
        const val registerAddrOfPpuData = 0x0007

        val addrOfPatternTable0 = Address(0x0000)
        val addrOfPatternTable1 = Address(0x1000)
        val addrOfNameTable = Address(0x2000)
        val addrOfAttrTable = Address(0x23C0)
        val addrOfBgPaletteTable = Address(0x3F00)
        val addrOfSpritePaletteTable = Address(0x3F10)

        fun from(
            interrupt: Interrupt,
            nametableMirroringVertical: Boolean,
            chrRom: ChrRom
        ): Ppu {
            val bytes = ByteArray(0x4000)
            chrRom.copyInto(bytes)
            return Ppu(
                interrupt = interrupt,
                nametableMirroringVertical = nametableMirroringVertical,
                ramBytes = bytes
            )
        }
    }

    private val spriteRam = SpriteRam()
    private val ram = PpuRam(ramBytes)
    private val register = Register()
    private val colors = Colors()

    private var cycle = 0
    private var line = Line(0)
    private var tileY = Tile.Unit(0)

    val image = createImage()

    private val scroll = Scroll(register)

    private fun createImage(): WritableImage {
        return WritableImage(Pixel.maxX * zoomRate, Pixel.maxY * zoomRate)
    }

    fun read(addr: Address): Int {
        val result = when (addr.value) {
            registerAddrOfReadStatus -> {
                register.readByte.toUint().let { orig ->
                    // "cleared after reading $2002 and at dot 1 of the pre-render line"
                    // https://wiki.nesdev.com/w/index.php/PPU_registers
                    register.vblank = false
                    orig
                }
            }
            registerAddrOfSpriteRamData -> spriteRam.read()
            registerAddrOfPpuData -> ram.read(register.ppuAddrIncrBytes)
            else -> throw IllegalArgumentException("Read operation isn't permitted at addr:$addr")
        }
        return result
    }

    fun write(addr: Address, value: Byte) {
        when (addr.value) {
            registerAddrOfWriteStatus0 -> register.writeByte0 = value.toUint()
            registerAddrOfWriteStatus1 -> register.writeByte1 = value.toUint()
            registerAddrOfSpriteRamAddr -> spriteRam.addr(Address(value.toUint()))
            registerAddrOfSpriteRamData -> spriteRam.write(value)
            registerAddrOfScrollOffset -> scroll.write(value)
            registerAddrOfPpuAddr -> ram.addr(Address(value.toUint()))
            registerAddrOfPpuData -> ram.write(value, register.ppuAddrIncrBytes)
            else -> throw IllegalArgumentException("Write operation isn't permitted at addr:$addr")
        }
    }

    fun transferToSprite(bytes: ByteArray) {
        spriteRam.transfer(bytes)
    }

    private val screenBuffer = IntBuffer.allocate(image.width.toInt() * image.height.toInt())
    private val screenBufferFormat = WritablePixelFormat.getIntArgbInstance()

    private fun buildPixels(
        addrOfPatternTable: Address,
        addrOfPaletteTable: Address,
        spriteId: Int,
        paletteId: Int,
        position: Pixel,
        transparentByDefault: Boolean,
        reverseVertical: Boolean = false,
        reverseHorizontal: Boolean = false
    ) {
        val spriteData = ram.spriteData(addrOfPatternTable, spriteId)
        val paletteData = ram.paletteData(addrOfPaletteTable, paletteId)

        for (bitY in 0.until(bitsInChar)) {
            val byte0 = spriteData[bitY].toUint()
            val byte1 = spriteData[bitY + bytesOfOneColorSprite].toUint()
            for (bitX in 0.until(bitsInChar)) {
                val patternValue = run {
                    val bitInByte0 = if (byte0 and (0x80 shr bitX) != 0) 1 else 0
                    val bitInByte1 = if (byte1 and (0x80 shr bitX) != 0) 1 else 0
                    bitInByte0 + bitInByte1 * 2
                }
                if (transparentByDefault && patternValue == 0) {
                    continue
                }

                val color: Int = run {
                    val colorIdInPalette = paletteData[patternValue].toUint()
                    colors.argb(colorIdInPalette)
                }

                val zoomedBase = position.zoomed()
                val adjustedBitX = if (reverseHorizontal) { bitsInChar - bitX - 1 } else { bitX }
                val adjustedBitY = if (reverseVertical) { bitsInChar - bitY - 1 } else { bitY }
                for (zoomBitY in 0.until(zoomRate)) {
                    val calculatedY = zoomedBase.y.incr((adjustedBitY * zoomRate) + zoomBitY)
                    for (zoomBitX in 0.until(zoomRate)) {
                        val calculatedX = zoomedBase.x.incr((adjustedBitX * zoomRate) + zoomBitX)
                        if (calculatedX.validWidth(image) && calculatedY.validHeight(image)) {
                            screenBufferFormat.setArgb(
                                screenBuffer,
                                calculatedX.value,
                                calculatedY.value,
                                image.width.toInt(),
                                color
                            )
                        }
                    }
                }
            }
        }
    }

    private fun addrOfNameTable(nametableId: Int) = addrOfNameTable.plus(0x400 * nametableId)

    private fun addrOfAttrTable(nametableId: Int) = addrOfAttrTable.plus(0x400 * nametableId)

    private fun buildBgLine() {
        val baseY = tileY.toPixel()
        val absoluteY = scroll.absoluteY(baseY)
        val displayY = scroll.displayY(baseY)

        for (tileXIndex in 0.until(Tile.maxX)) {
            val baseX = Tile.Unit(tileXIndex).toPixel()
            val absoluteX = scroll.absoluteX(baseX)
            val displayX = scroll.displayX(baseX)

            val nameTableId = ((absoluteX.value / Pixel.maxX) % 2) + ((absoluteY.value / Pixel.maxY) % 2) * 2
            val normalizedTile = Tile(
                x = Tile.Unit.fromPixel(Pixel.Unit(absoluteX.value % Pixel.maxX)),
                y = Tile.Unit.fromPixel(Pixel.Unit(absoluteY.value % Pixel.maxY))
            )
            val spriteId = ram.spriteId(addrOfNameTable(nameTableId), normalizedTile)

            val paletteId = ram.attribute(
                addrOfAttrTable = addrOfAttrTable(nametableId = nameTableId),
                tile = normalizedTile
            ).paletteId(tile = normalizedTile)

            buildPixels(
                addrOfPatternTable = if (register.bgPatternAddrMode) {
                    addrOfPatternTable1
                }
                else {
                    addrOfPatternTable0
                },
                addrOfPaletteTable = addrOfBgPaletteTable,
                spriteId = spriteId,
                paletteId = paletteId,
                position = Pixel(displayX, displayY),
                transparentByDefault = false
            )
        }
    }

    private fun buildSprites() {
        for (i in 0.until(0x100) step 4) {
            val sprite = Sprite.get(
                register = register,
                image = image,
                spriteRam = spriteRam,
                index = i
            ) ?: continue
            for (indexOfY in 0.until(sprite.heightOfSprite)) {
                buildPixels(
                    addrOfPatternTable = sprite.addrOfPatternTable,
                    addrOfPaletteTable = addrOfSpritePaletteTable,
                    spriteId = sprite.id + indexOfY,
                    paletteId = sprite.paletteId,
                    position = Pixel(
                        sprite.position.x,
                        sprite.position.y.plus(Pixel.Unit(indexOfY * Tile.pixelsInTile))
                    ),
                    transparentByDefault = true,
                    reverseHorizontal = sprite.reverseHorizontal,
                    reverseVertical = sprite.reverseVertical
                )
            }
        }
    }

    private fun buildScreen() {
        image.pixelWriter.setPixels<IntBuffer>(0, 0, image.width.toInt(), image.height.toInt(), screenBufferFormat, screenBuffer, image.width.toInt())
    }

    private fun updateSpriteHit() {
        val sprite = Sprite.get(
            register = register,
            image = image,
            spriteRam = spriteRam,
            index = 0
        ) ?: return
        if (tileY.contains(sprite.position.y) && register.bgEnabled && register.spriteEnabled) {
            register.spriteHit = true
        }
    }

    fun run(cycle: Int): Boolean {
        this.cycle += cycle
        if (this.cycle < cyclesPerLine) {
            return false
        }

        this.cycle -= cyclesPerLine
        line = line.incr(1)

        if (line.lineTiming()) {
            if (register.bgEnabled) {
                buildBgLine()
                updateSpriteHit()
            }
            tileY = tileY.incr(1)
        }
        else if (line.vblankTiming()) {
            register.vblank = true
            if (register.interruptOnVBlank) {
                interrupt.nmi = true
            }
        }
        else if (line.readyForNewLine()) {
            if (register.spriteEnabled) {
                buildSprites()
            }
            buildScreen()
            register.vblank = false
            register.spriteHit = false
            line = Line(0)
            tileY = Tile.Unit(0)
            return true
        }
        return false
    }

    override fun toString(): String {
        return "Ppu(interrupt=$interrupt, cycle=$cycle, line=$line, register=$register)"
    }
}