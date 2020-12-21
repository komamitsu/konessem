package org.komamitsu.konessem.ppu

internal class Colors {
    companion object {
        private val colorTable = arrayOf(
            PaletteColor(0x80, 0x80, 0x80), PaletteColor(0x00, 0x3D, 0xA6), PaletteColor(0x00, 0x12, 0xB0), PaletteColor(0x44, 0x00, 0x96),
            PaletteColor(0xA1, 0x00, 0x5E), PaletteColor(0xC7, 0x00, 0x28), PaletteColor(0xBA, 0x06, 0x00), PaletteColor(0x8C, 0x17, 0x00),

            PaletteColor(0x5C, 0x2F, 0x00), PaletteColor(0x10, 0x45, 0x00), PaletteColor(0x05, 0x4A, 0x00), PaletteColor(0x00, 0x47, 0x2E),
            PaletteColor(0x00, 0x41, 0x66), PaletteColor(0x00, 0x00, 0x00), PaletteColor(0x05, 0x05, 0x05), PaletteColor(0x05, 0x05, 0x05),

            PaletteColor(0xC7, 0xC7, 0xC7), PaletteColor(0x00, 0x77, 0xFF), PaletteColor(0x21, 0x55, 0xFF), PaletteColor(0x82, 0x37, 0xFA),
            PaletteColor(0xEB, 0x2F, 0xB5), PaletteColor(0xFF, 0x29, 0x50), PaletteColor(0xFF, 0x22, 0x00), PaletteColor(0xD6, 0x32, 0x00),

            PaletteColor(0xC4, 0x62, 0x00), PaletteColor(0x35, 0x80, 0x00), PaletteColor(0x05, 0x8F, 0x00), PaletteColor(0x00, 0x8A, 0x55),
            PaletteColor(0x00, 0x99, 0xCC), PaletteColor(0x21, 0x21, 0x21), PaletteColor(0x09, 0x09, 0x09), PaletteColor(0x09, 0x09, 0x09),

            PaletteColor(0xFF, 0xFF, 0xFF), PaletteColor(0x0F, 0xD7, 0xFF), PaletteColor(0x69, 0xA2, 0xFF), PaletteColor(0xD4, 0x80, 0xFF),
            PaletteColor(0xFF, 0x45, 0xF3), PaletteColor(0xFF, 0x61, 0x8B), PaletteColor(0xFF, 0x88, 0x33), PaletteColor(0xFF, 0x9C, 0x12),

            PaletteColor(0xFA, 0xBC, 0x20), PaletteColor(0x9F, 0xE3, 0x0E), PaletteColor(0x2B, 0xF0, 0x35), PaletteColor(0x0C, 0xF0, 0xA4),
            PaletteColor(0x05, 0xFB, 0xFF), PaletteColor(0x5E, 0x5E, 0x5E), PaletteColor(0x0D, 0x0D, 0x0D), PaletteColor(0x0D, 0x0D, 0x0D),

            PaletteColor(0xFF, 0xFF, 0xFF), PaletteColor(0xA6, 0xFC, 0xFF), PaletteColor(0xB3, 0xEC, 0xFF), PaletteColor(0xDA, 0xAB, 0xEB),
            PaletteColor(0xFF, 0xA8, 0xF9), PaletteColor(0xFF, 0xAB, 0xB3), PaletteColor(0xFF, 0xD2, 0xB0), PaletteColor(0xFF, 0xEF, 0xA6),

            PaletteColor(0xFF, 0xF7, 0x9C), PaletteColor(0xD7, 0xE8, 0x95), PaletteColor(0xA6, 0xED, 0xAF), PaletteColor(0xA2, 0xF2, 0xDA),
            PaletteColor(0x99, 0xFF, 0xFC), PaletteColor(0xDD, 0xDD, 0xDD), PaletteColor(0x11, 0x11, 0x11), PaletteColor(0x11, 0x11, 0x11)
        )

        private val defaultAlpha = 0xFF shl 24
    }

    private class PaletteColor(val r: Int, val g: Int, val b: Int)

    fun argb(colorId: Int): Int {
        val paletteColor = colorTable[colorId]
        return defaultAlpha or (paletteColor.r shl 16) or (paletteColor.g shl 8) or paletteColor.b
    }
}