package com.videoloop.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * Pantalla principal: reproduce el vídeo en bucle en la pantalla 1 y abre una
 * [SecondScreenPresentation] en cada pantalla secundaria que detecte Android.
 */
class MainActivity : Activity() {

    private lateinit var playerView: PlayerView
    private lateinit var message: TextView
    private lateinit var displayManager: DisplayManager

    private var player: ExoPlayer? = null
    private var videos: VideoFinder.Videos? = null
    private val presentations = mutableMapOf<Int, SecondScreenPresentation>()
    private val handler = Handler(Looper.getMainLooper())
    private val askedPermissions = mutableSetOf<String>()

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = updatePresentations()
        override fun onDisplayRemoved(displayId: Int) = updatePresentations()
        override fun onDisplayChanged(displayId: Int) {}
    }

    // Si no hay vídeo todavía, se vuelve a buscar cada 10 segundos
    private val retryFind = Runnable { startPlayback() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.player_view)
        message = findViewById(R.id.message)
        displayManager = getSystemService(DisplayManager::class.java)
    }

    override fun onResume() {
        super.onResume()
        SecondScreenPresentation.hideSystemUi(window.decorView)
        if (requestMissingPermissions()) return
        displayManager.registerDisplayListener(displayListener, handler)
        startPlayback()
    }

    override fun onPause() {
        displayManager.unregisterDisplayListener(displayListener)
        stopPlayback()
        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) SecondScreenPresentation.hideSystemUi(window.decorView)
    }

    private fun startPlayback() {
        stopPlayback()
        val found = VideoFinder.find(this)
        videos = found
        if (found == null) {
            showMessage(
                "No se ha encontrado ningún vídeo.\n\n" +
                    "Copia tu vídeo en la carpeta:\n${VideoFinder.internalFolder.absolutePath}\n" +
                    "o en una carpeta \"${VideoFinder.FOLDER_NAME}\" de un USB."
            )
            handler.postDelayed(retryFind, 10_000)
            return
        }
        message.visibility = View.GONE
        player = LoopingPlayer.create(this, found.screen1, muted = false).also { playerView.player = it }
        updatePresentations()
    }

    private fun stopPlayback() {
        handler.removeCallbacks(retryFind)
        presentations.values.forEach { it.dismiss() }
        presentations.clear()
        playerView.player = null
        player?.release()
        player = null
    }

    /** Abre o cierra el vídeo en las pantallas secundarias según estén conectadas. */
    private fun updatePresentations() {
        val video = videos?.screen2 ?: return
        val ownDisplayId = window.decorView.display?.displayId ?: Display.DEFAULT_DISPLAY
        val secondary = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
            .filter { it.displayId != ownDisplayId }
            .associateBy { it.displayId }

        presentations.keys.filter { it !in secondary }.forEach { presentations.remove(it)?.dismiss() }

        for ((id, display) in secondary) {
            if (id in presentations) continue
            try {
                val presentation = SecondScreenPresentation(this, display, video)
                presentation.show()
                presentations[id] = presentation
            } catch (e: WindowManager.InvalidDisplayException) {
                // La pantalla se ha desconectado mientras se abría
            }
        }
    }

    private fun showMessage(text: String) {
        message.text = text
        message.visibility = View.VISIBLE
    }

    /**
     * Pide, de una en una, los permisos que hacen falta (cada uno solo una vez
     * por arranque). Devuelve true si ha abierto una pantalla de permisos; la
     * reproducción empieza al volver.
     */
    private fun requestMissingPermissions(): Boolean {
        // 1. Acceso a los archivos para poder leer la carpeta del vídeo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager() && askedPermissions.add("files")) {
                showMessage("Activa \"Acceso a todos los archivos\" para VideoLoop")
                openSettings(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                return true
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            askedPermissions.add("read")
        ) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE)
            return true
        }

        // 2. "Mostrar sobre otras apps": Android 10+ lo exige para abrir la app al encender
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this) &&
            askedPermissions.add("overlay")
        ) {
            showMessage("Activa \"Mostrar sobre otras aplicaciones\" para que VideoLoop arranque al encender")
            openSettings(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            return true
        }
        return false
    }

    private fun openSettings(action: String) {
        val intent = Intent(action, Uri.parse("package:$packageName"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Algunas placas no tienen la pantalla por app; abrimos la general
            try {
                startActivity(Intent(action))
            } catch (_: Exception) {
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // onResume se vuelve a llamar y continúa el proceso
    }

    companion object {
        private const val REQUEST_STORAGE = 1
    }
}
