package com.fyp.vault.utilities

import java.util.Locale

fun calculateSizeInStringFormat(size: Long): String {
    val levels: Array<String> = arrayOf("B", "kB", "MB", "GB", "TB", "PB")
    var level: Int = 0
    var runningSize: Double = size.toDouble()
    while (runningSize.toLong() / 1024L > 0){
        runningSize /= 1024
        level++
        if (level == 5){
            // If the size reaches petaBytes
            break
        }
    }
    return String.format(Locale.getDefault() ,"%.1f %s", runningSize, levels[level])
}