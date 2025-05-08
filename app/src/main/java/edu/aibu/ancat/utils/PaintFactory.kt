package edu.aibu.ancat.utils

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

object PaintFactory {

    fun line(): Paint = Paint().apply {
        textSize = 8f
        color = Color.BLACK
    }

    fun option(): Paint = Paint().apply {
        textSize = 15f
        color = Color.BLACK
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    fun rating(): Paint = Paint().apply {
        textSize = 8f
        color = Color.GRAY
    }

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
