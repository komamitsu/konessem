package org.komamitsu.konessem.view

import tornadofx.*


class InitialView: View() {
    override val root = vbox {
        title = "Konessem"

        padding = insets(all = 24)

        button(text = "Choose NES ROM file") {
            setOnAction {
                val romFile = chooseFile(
                    filters = arrayOf()
                )
                close()
                if (romFile.isNotEmpty()) {
                    Game(romFile[0].toPath()).openWindow()
                }
            }
        }
    }
}