package org.komamitsu.konessem.ppu

import javafx.scene.image.WritableImage
import org.komamitsu.konessem.Address
import org.komamitsu.konessem.Interrupt
import org.komamitsu.konessem.rom.ChrRom
import org.komamitsu.konessem.toUint

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

    val image = createImage()

    private val spriteRam = SpriteRam()
    private val ram = PpuRam(ramBytes)
    private val register = Register()
    private val renderer = Renderer(ram, Colors(), image)

    private var cycle = 0
    private var line = Line(0)
    private var tileY = Tile.Unit(0)

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

    private fun addrOfNameTable(nametableId: Int) = addrOfNameTable.plus(0x400 * nametableId)

    private fun addrOfAttrTable(nametableId: Int) = addrOfAttrTable.plus(0x400 * nametableId)

    private fun buildBgLine() {
        val baseY = tileY.toPixel()
        val absoluteY = scroll.absoluteY(baseY)
        val displayY = scroll.displayY(baseY)

        // Iterate Tile.maxX + 1 times for scroll
        for (tileXIndex in 0.until(Tile.maxX + 1)) {
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

            renderer.add(
                layer = Renderer.Layer.BACKGROUND,
                addrOfPatternTable = if (register.bgPatternAddrMode) {
                    addrOfPatternTable1
                }
                else {
                    addrOfPatternTable0
                },
                addrOfPaletteTable = addrOfBgPaletteTable,
                spriteId = spriteId,
                paletteId = paletteId,
                position = Pixel(displayX, displayY)
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
                renderer.add(
                    layer = if (sprite.prioritizedBg) {
                        Renderer.Layer.REMOTE_SPRITE
                    } else {
                        Renderer.Layer.CLOSE_SPRITE
                    },
                    addrOfPatternTable = sprite.addrOfPatternTable,
                    addrOfPaletteTable = addrOfSpritePaletteTable,
                    spriteId = sprite.id + indexOfY,
                    paletteId = sprite.paletteId,
                    position = Pixel(
                        sprite.position.x,
                        sprite.position.y.plus(Pixel.Unit(indexOfY * Tile.pixelsInTile))
                    ),
                    reverseHorizontal = sprite.reverseHorizontal,
                    reverseVertical = sprite.reverseVertical
                )
            }
        }
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
            // Iterate once more for scroll
            if (register.bgEnabled) {
                buildBgLine()
                updateSpriteHit()
            }

            register.vblank = true
            if (register.interruptOnVBlank) {
                interrupt.nmi = true
            }
        }
        else if (line.readyForNewLine()) {
            if (register.spriteEnabled) {
                buildSprites()
            }
            renderer.render()
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