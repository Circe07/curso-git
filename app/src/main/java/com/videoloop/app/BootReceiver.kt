package com.videoloop.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper

/** Abre la app automáticamente al encender la pantalla. */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        // Pequeña espera para que el sistema y los USB terminen de montarse
        Handler(Looper.getMainLooper()).postDelayed({
            val launch = Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(launch)
            pending.finish()
        }, 5000)
    }
}
