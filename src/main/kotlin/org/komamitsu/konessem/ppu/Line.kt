package org.komamitsu.konessem.ppu

internal data class Line(val value: Int) {
    companion object {
        const val visibleLines = 240
        const val vblankLines = 22
    }

    fun incr(value: Int) = Line(this.value + value)

    fun lineTiming() = value <= visibleLines && value % 8 == 0

    fun vblankTiming() = value == visibleLines + 1

    fun readyForNewLine() = value >= visibleLines + vblankLines
}