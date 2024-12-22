package edu.aibu.ancat.utils

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

object PaintFactory {

    fun text(): Paint = Paint().apply {
        textSize = 12f
        color = Color.BLACK
    }

    fun title(): Paint = Paint().apply {
        textSize = 16f
        color = Color.BLACK
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
}
