package org.komamitsu.konessem

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory


fun daemonizedThreadFactory(): ThreadFactory {
    return ThreadFactory {
        val thread = Thread(it)
        thread.isDaemon = true
        thread
    }
}
