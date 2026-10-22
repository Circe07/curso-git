package com.videoloop.app

import android.content.Context

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

    companion object {
        private const val KEY_SCREENS = "pantallas"
        private const val KEY_PORTRAIT = "vertical"
    }
}
