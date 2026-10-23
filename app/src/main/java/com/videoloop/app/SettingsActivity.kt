package com.videoloop.app

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView

/** Pantalla para elegir el número de pantallas, la orientación y el ajuste del vídeo. */
class SettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settings = AppSettings(this)
        val screensGroup = findViewById<RadioGroup>(R.id.screens_group)
        val orientationGroup = findViewById<RadioGroup>(R.id.orientation_group)
        val fitGroup = findViewById<RadioGroup>(R.id.fit_group)

        screensGroup.check(if (settings.screens == 1) R.id.screens_1 else R.id.screens_2)
        orientationGroup.check(if (settings.portrait) R.id.orientation_vertical else R.id.orientation_horizontal)
        fitGroup.check(if (settings.stretch) R.id.fit_stretch else R.id.fit_zoom)

        val videos = VideoFinder.find(this)
        findViewById<TextView>(R.id.video_info).text = if (videos == null) {
            "No se ha encontrado ningún vídeo.\nCópialo en ${VideoFinder.internalFolder.absolutePath}\n" +
                "o en una carpeta \"${VideoFinder.FOLDER_NAME}\" de un USB."
        } else {
            "Vídeo: ${videos.screen1.absolutePath}"
        }

        findViewById<Button>(R.id.save).apply {
            requestFocus()
            setOnClickListener {
                settings.screens = if (screensGroup.checkedRadioButtonId == R.id.screens_1) 1 else 2
                settings.portrait = orientationGroup.checkedRadioButtonId == R.id.orientation_vertical
                settings.stretch = fitGroup.checkedRadioButtonId == R.id.fit_stretch
                finish()
            }
        }
    }
}
