package com.videoloop.app

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.widget.FrameLayout
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

/** Muestra el vídeo en bucle en una pantalla secundaria, siempre en horizontal. */
class SecondScreenPresentation(
    context: Context,
    display: Display,
    private val video: File,
) : Presentation(context, display) {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.presentation)
        hideSystemUi(window?.decorView)

        val playerView = findViewById<PlayerView>(R.id.player_view)

        // Si la pantalla está en vertical, giramos el vídeo 90º para que se vea en horizontal
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        display.getRealMetrics(metrics)
        if (metrics.heightPixels > metrics.widthPixels) {
            playerView.layoutParams = FrameLayout.LayoutParams(metrics.heightPixels, metrics.widthPixels).apply {
                gravity = android.view.Gravity.CENTER
            }
            playerView.rotation = 90f
        }

        // Sin sonido para que no se oiga el audio duplicado
        player = LoopingPlayer.create(context, video, muted = true).also { playerView.player = it }
    }

    override fun onStop() {
        player?.release()
        player = null
        super.onStop()
    }

    companion object {
        @Suppress("DEPRECATION")
        fun hideSystemUi(view: View?) {
            view?.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }
}
