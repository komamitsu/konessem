package org.komamitsu.konessem

import javafx.scene.input.KeyCode

class KeyPad {
    private enum class Event(val index: Int, val code: KeyCode) {
        A(0, KeyCode.K),
        B(1, KeyCode.L),
        START(2, KeyCode.COMMA),
        SELECT(3, KeyCode.PERIOD),
        UP(4, KeyCode.W),
        DOWN(5, KeyCode.S),
        LEFT(6, KeyCode.A),
        RIGHT(7, KeyCode.D);

        companion object {
            fun fromKeyCode(code: KeyCode): Event? {
                for (event in values()) {
                    if (event.code == code) {
                        return event
                    }
                }
                return null
            }
        }
    }

    private class Events : Iterator<Boolean> {
        private val maxEvents = 8
        private val events: Array<Boolean> = Array(maxEvents) { false }
        private var index = 0

        fun onKeyPressed(keyCode: KeyCode) {
            Event.fromKeyCode(keyCode)?.let {
                events[it.index] = true
            }
        }

        fun onKeyReleased(keyCode: KeyCode) {
            Event.fromKeyCode(keyCode)?.let {
                events[it.index] = false
            }
        }

        fun commit(): Iterator<Boolean> {
            val captured = Events()
            events.copyInto(captured.events)
            captured.index = 0
            return captured.iterator()
        }

        override fun hasNext(): Boolean {
            return index < maxEvents
        }

        override fun next(): Boolean {
            return events[index++]
        }
    }

    private val transientEvents = Events()
    private var capturedEventIter: Iterator<Boolean>? = null
    private var firstByteWritten = false

    fun onKeyPressed(keyCode: KeyCode) {
        transientEvents.onKeyPressed(keyCode)
    }

    fun onKeyReleased(keyCode: KeyCode) {
        transientEvents.onKeyReleased(keyCode)
    }

    fun write(value: Int) {
        if (!firstByteWritten) {
            if (value and 0x07 != 0) {
                firstByteWritten = true
            }
        }
        else {
            if (value and 0x07 == 0x00) {
                capturedEventIter = transientEvents.commit()
            }
            firstByteWritten = false
        }
    }

    fun read(): Int {
        val iter = capturedEventIter ?: return 0
        return if (iter.next()) {
            1
        }
        else {
            0
        }
    }
}