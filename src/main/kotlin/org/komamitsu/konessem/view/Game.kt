package org.komamitsu.konessem.view

import javafx.scene.image.Image
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import mu.KotlinLogging
import org.komamitsu.konessem.Interrupt
import org.komamitsu.konessem.KeyPad
import org.komamitsu.konessem.cpu.Cpu
import org.komamitsu.konessem.cpu.CpuBus
import org.komamitsu.konessem.cpu.CpuRam
import org.komamitsu.konessem.daemonizedThreadFactory
import org.komamitsu.konessem.loader.Loader
import org.komamitsu.konessem.ppu.Ppu
import tornadofx.*
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

private val logger = KotlinLogging.logger {}

class Game(romFile: Path): View() {
    private var executorService = Executors.newSingleThreadScheduledExecutor(daemonizedThreadFactory())
    private val loader = Loader()
    private val ppu: Ppu
    private val cpu: Cpu
    private val keyPad = KeyPad()
    private var running = AtomicBoolean(true)

    init {
        loader.load(romFile)

        val interrupt = Interrupt()
        val cpuRam = CpuRam()

        ppu = Ppu.from(
            interrupt = interrupt,
            nametableMirroringVertical = loader.iNesHeader.nametableMirroringVertical,
            chrRom = loader.chrRom
        )

        cpu = Cpu(
            cpuBus = CpuBus(
                ppu = ppu,
                prgRom = loader.prgRom,
                cpuRam = cpuRam,
                keyPad = keyPad
            ),
            interrupt = interrupt
        )
        cpu.reset()
    }

    private fun calculateAndRender(): Image? {
        try {
            val start = Instant.now()
            while (running.get()) {
                val cycle = cpu.run()
                val ready = ppu.run(cycle * 3)
                if (ready) {
                    logger.trace { "Duration: ${Duration.between(start, Instant.now()).toMillis()}" }
                    return ppu.image
                }
            }
        }
        catch (e: Throwable) {
            logger.error(e) { "Unexpected exception is thrown. cpu:$cpu, ppu:$ppu" }
            executorService.shutdown()
            running.set(false)
        }
        return null
    }

    override val root = vbox {
        title = "Konessem"

        addEventHandler(KeyEvent.KEY_PRESSED) {
            keyPad.onKeyPressed(it.code)
        }
        addEventHandler(KeyEvent.KEY_RELEASED) {
            keyPad.onKeyReleased(it.code)
        }

        val imageView = imageview {
            image = ppu.image
            background = Color.BLACK.asBackground()
        }

        executorService.scheduleWithFixedDelay(
            {
                val image = calculateAndRender()
                if (image != null) {
                    imageView.image = image
                }
            },
            0,
            16600L,
            TimeUnit.MICROSECONDS
        )
    }

    override fun onDock() {
        root.requestFocus()
    }
}
