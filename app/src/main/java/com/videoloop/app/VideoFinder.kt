package com.videoloop.app

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import java.io.File

/**
 * Busca los vídeos en la carpeta "VideoLoop".
 *
 * Se mira primero en los USB / tarjetas SD conectados y después en la memoria
 * interna (/sdcard/VideoLoop). Si hay un archivo llamado "pantalla2.xxx" se usa
 * para la segunda pantalla; si no, las dos pantallas reproducen el mismo vídeo.
 */
object VideoFinder {

    const val FOLDER_NAME = "VideoLoop"

    private val VIDEO_EXTENSIONS = setOf(
        "mp4", "mkv", "mov", "avi", "webm", "3gp", "ts", "m4v", "mpg", "mpeg", "wmv", "flv"
    )

    data class Videos(val screen1: File, val screen2: File)

    val internalFolder: File
        get() = File(Environment.getExternalStorageDirectory(), FOLDER_NAME)

    fun find(context: Context): Videos? {
        for (folder in candidateFolders(context)) {
            val files = folder.listFiles()
                ?.filter { it.isFile && !it.name.startsWith(".") && it.extension.lowercase() in VIDEO_EXTENSIONS }
                ?.sortedBy { it.name.lowercase() }
                .orEmpty()
            if (files.isEmpty()) continue

            val second = files.firstOrNull { it.nameWithoutExtension.equals("pantalla2", ignoreCase = true) }
            val first = files.firstOrNull { it.nameWithoutExtension.equals("pantalla1", ignoreCase = true) }
                ?: files.firstOrNull { it != second }
                ?: second!!
            return Videos(first, second ?: first)
        }
        return null
    }

    private fun candidateFolders(context: Context): List<File> {
        val roots = mutableListOf<File>()

        // USB y tarjetas SD
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val sm = context.getSystemService(StorageManager::class.java)
            sm?.storageVolumes
                ?.filter { it.isRemovable && it.state == Environment.MEDIA_MOUNTED }
                ?.mapNotNull { it.directory }
                ?.let { roots += it }
        }
        File("/storage").listFiles()
            ?.filter { it.isDirectory && it.name != "emulated" && it.name != "self" }
            ?.let { roots += it }
        File("/mnt/media_rw").listFiles()?.let { roots += it }

        // Memoria interna
        roots += Environment.getExternalStorageDirectory()

        return roots.distinctBy { it.absolutePath }
            .map { File(it, FOLDER_NAME) }
            .filter { it.isDirectory }
    }
}
