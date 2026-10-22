package com.videoloop.app

import android.util.DisplayMetrics
import android.view.Display
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout

/**
 * Gira la vista del vídeo 90º cuando la orientación elegida (horizontal o
 * vertical) no coincide con la de la pantalla física.
 */
object VideoRotation {

    fun apply(view: View, display: Display, portrait: Boolean) {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        display.getRealMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val displayIsPortrait = height > width

        if (displayIsPortrait == portrait) {
            view.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            )
            view.rotation = 0f
        } else {
            view.layoutParams = FrameLayout.LayoutParams(height, width, Gravity.CENTER)
            view.rotation = 90f
        }
    }
}
