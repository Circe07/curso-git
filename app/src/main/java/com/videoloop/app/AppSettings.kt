package com.videoloop.app

import android.content.Context
import androidx.media3.ui.AspectRatioFrameLayout

/** Ajustes elegidos por el usuario, guardados en el dispositivo. */
class AppSettings(context: Context) {

    private val prefs = context.getSharedPreferences("ajustes", Context.MODE_PRIVATE)

    /** true cuando ya se han guardado los ajustes al menos una vez. */
    val configured: Boolean
        get() = prefs.contains(KEY_SCREENS)

    var screens: Int
        get() = prefs.getInt(KEY_SCREENS, 2)
        set(value) = prefs.edit().putInt(KEY_SCREENS, value).apply()

    var portrait: Boolean
        get() = prefs.getBoolean(KEY_PORTRAIT, false)
        set(value) = prefs.edit().putBoolean(KEY_PORTRAIT, value).apply()

    /** true = estirar el vídeo; false = rellenar recortando los bordes. */
    var stretch: Boolean
        get() = prefs.getBoolean(KEY_STRETCH, false)
        set(value) = prefs.edit().putBoolean(KEY_STRETCH, value).apply()

    /** Modo de ajuste para PlayerView: siempre ocupa toda la pantalla, sin bordes negros. */
    val resizeMode: Int
        get() = if (stretch) AspectRatioFrameLayout.RESIZE_MODE_FILL else AspectRatioFrameLayout.RESIZE_MODE_ZOOM

    companion object {
        private const val KEY_SCREENS = "pantallas"
        private const val KEY_PORTRAIT = "vertical"
        private const val KEY_STRETCH = "estirar"
    }
}
