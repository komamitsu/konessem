package org.komamitsu.konessem.ppu

import javafx.scene.image.WritableImage
import javafx.scene.image.WritablePixelFormat
import org.komamitsu.konessem.Address
import org.komamitsu.konessem.daemonizedThreadFactory
import org.komamitsu.konessem.toUint
import java.nio.IntBuffer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

internal class Renderer(
    private val ram: PpuRam,
    private val colors: Colors,
    private val image: WritableImage
) {
    private val screenBuffer = IntBuffer.allocate(image.width.toInt() * image.height.toInt())
    private val screenBufferFormat = WritablePixelFormat.getIntArgbInstance()

    private val executor = Executors.newSingleThreadExecutor(daemonizedThreadFactory())

    enum class Layer(val items: MutableList<Item>) {
        BACKGROUND(CopyOnWriteArrayList()),
        REMOTE_SPRITE(CopyOnWriteArrayList()),
        CLOSE_SPRITE(CopyOnWriteArrayList())
    }

    private class Item(
        val addrOfPatternTable: Address,
        val addrOfPaletteTable: Address,
        val spriteId: Int,
        val paletteId: Int,
        val position: Pixel,
        val reverseVertical: Boolean,
        val reverseHorizontal: Boolean
    )

    fun add(
        layer: Layer,
        addrOfPatternTable: Address,
        addrOfPaletteTable: Address,
        spriteId: Int,
        paletteId: Int,
        position: Pixel,
        reverseVertical: Boolean = false,
        reverseHorizontal: Boolean = false
    ) {
        layer.items.add(
            Item(
                addrOfPatternTable = addrOfPatternTable,
                addrOfPaletteTable = addrOfPaletteTable,
                spriteId = spriteId,
                paletteId = paletteId,
                position = position,
                reverseVertical = reverseVertical,
                reverseHorizontal = reverseHorizontal
            )
        )
    }

    private fun renderItem(
        item: Item,
        renderingRemoteBackground: Boolean = false,
        renderingSprite: Boolean = false,
        renderingCloseBackground: Boolean = false
    ) {
        val spriteData = ram.spriteData(item.addrOfPatternTable, item.spriteId)
        val paletteData = ram.paletteData(item.addrOfPaletteTable, item.paletteId)

        for (bitY in 0.until(Ppu.bitsInChar)) {
            val byte0 = spriteData[bitY].toUint()
            val byte1 = spriteData[bitY + Ppu.bytesOfOneColorSprite].toUint()
            for (bitX in 0.until(Ppu.bitsInChar)) {
                val patternValue = run {
                    val bitInByte0 = if (byte0 and (0x80 shr bitX) != 0) 1 else 0
                    val bitInByte1 = if (byte1 and (0x80 shr bitX) != 0) 1 else 0
                    bitInByte0 + bitInByte1 * 2
                }
                // When rendering remote background, do nothing if it's not the default color
                if (renderingRemoteBackground && patternValue != 0) {
                    continue
                }
                // When rendering sprites, do nothing if it's the default color
                if (renderingSprite && patternValue == 0) {
                    continue
                }
                // When rendering close background, do nothing if it's the default color
                if (renderingCloseBackground && patternValue == 0) {
                    continue
                }

                val color: Int = run {
                    val colorIdInPalette = paletteData[patternValue].toUint()
                    colors.argb(colorIdInPalette)
                }

                val zoomedBase = item.position.zoomed()
                val adjustedBitX = if (item.reverseHorizontal) {
                    Ppu.bitsInChar - bitX - 1
                } else {
                    bitX
                }
                val adjustedBitY = if (item.reverseVertical) {
                    Ppu.bitsInChar - bitY - 1
                } else {
                    bitY
                }
                for (zoomBitY in 0.until(Ppu.zoomRate)) {
                    val calculatedY = zoomedBase.y.incr((adjustedBitY * Ppu.zoomRate) + zoomBitY)
                    for (zoomBitX in 0.until(Ppu.zoomRate)) {
                        val calculatedX = zoomedBase.x.incr((adjustedBitX * Ppu.zoomRate) + zoomBitX)
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

    fun render() {
        executor.execute {
            for (item in Layer.BACKGROUND.items) {
                renderItem(item = item, renderingRemoteBackground = true)
            }
            for (item in Layer.REMOTE_SPRITE.items) {
                renderItem(item = item, renderingSprite = true)
            }
            for (item in Layer.BACKGROUND.items) {
                renderItem(item = item, renderingCloseBackground = true)
            }
            for (item in Layer.CLOSE_SPRITE.items) {
                renderItem(item = item, renderingSprite = true)
            }
            Layer.values().forEach { it.items.clear() }

            image.pixelWriter.setPixels<IntBuffer>(0, 0,
                image.width.toInt(), image.height.toInt(),
                screenBufferFormat, screenBuffer, image.width.toInt())
        }
    }
}