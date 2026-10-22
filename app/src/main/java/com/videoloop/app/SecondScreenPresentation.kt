package com.videoloop.app

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.View
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

/** Muestra el vídeo en bucle en una pantalla secundaria, en la orientación elegida. */
class SecondScreenPresentation(
    context: Context,
    display: Display,
    private val video: File,
    private val portrait: Boolean,
) : Presentation(context, display) {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.presentation)
        hideSystemUi(window?.decorView)

        val playerView = findViewById<PlayerView>(R.id.player_view)

        VideoRotation.apply(playerView, display, portrait)

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
