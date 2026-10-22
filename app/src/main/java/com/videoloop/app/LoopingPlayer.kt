package com.videoloop.app

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import java.io.File

/** Crea un reproductor que repite el vídeo para siempre y se recupera solo si falla. */
object LoopingPlayer {

    private const val TAG = "VideoLoop"
    private const val RETRY_DELAY_MS = 3000L

    fun create(context: Context, file: File, muted: Boolean): ExoPlayer {
        val player = ExoPlayer.Builder(context).build()
        player.setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.volume = if (muted) 0f else 1f
        player.playWhenReady = true

        val handler = Handler(Looper.getMainLooper())
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "Error reproduciendo ${file.absolutePath}", error)
                handler.postDelayed({
                    player.seekTo(0)
                    player.prepare()
                }, RETRY_DELAY_MS)
            }
        })

        player.prepare()
        return player
    }
}
