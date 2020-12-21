package org.komamitsu.konessem

import org.komamitsu.konessem.view.InitialView
import tornadofx.App
import tornadofx.launch

class Main : App(InitialView::class)

fun main(args: Array<String>) {
    launch<Main>(args)
}
