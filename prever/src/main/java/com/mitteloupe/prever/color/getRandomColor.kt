package com.mitteloupe.prever.color

import android.graphics.Color
import kotlin.random.Random

fun getRandomColor(alpha: Int): Int {
    val red = Random.nextInt(0, 256)
    val green = Random.nextInt(0, 256)
    val blue = Random.nextInt(0, 256)
    return Color.argb(alpha, red, green, blue)
}
