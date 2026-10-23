# VideoLoop

App Android que reproduce un vídeo **en bucle, en horizontal y en las dos pantallas**
a la vez, y que **arranca sola al encender** el equipo.

Pensada para la placa **RK3568 con Android 11** (AIoT3568-V), pero funciona en
cualquier Android 5.0 o superior.

## Instalación

1. Copia `apk/VideoLoop.apk` a un USB, conéctalo a la pantalla e instálalo con el
   explorador de archivos (si lo pide, permite "instalar apps de origen desconocido").
   También con `adb install apk/VideoLoop.apk`.
2. Abre **VideoLoop** una vez. Te pedirá dos permisos, actívalos y vuelve atrás:
   - **Acceso a todos los archivos** → para leer la carpeta del vídeo.
   - **Mostrar sobre otras aplicaciones** → necesario en Android 10+ para que se
     abra solo al encender.

## Ajustes

La primera vez que abres la app sale la pantalla de **Ajustes**:

- **Número de pantallas**: 1 o 2.
- **Orientación**: Horizontal o Vertical (para pantallas colgadas en vertical).
- **Ajuste del vídeo**: el vídeo siempre ocupa toda la pantalla, sin bordes negros.
  - *Rellenar* (por defecto): mantiene la proporción y recorta lo que sobra.
  - *Estirar*: no recorta nada, pero deforma un poco la imagen si el vídeo no
    tiene la misma proporción que la pantalla.

Pulsa **Guardar y reproducir**. Desde entonces, al encender, el vídeo arranca
directamente con esos ajustes.

Para volver a los ajustes: **mantén pulsada la pantalla** o pulsa **OK / Menú**
en el mando.

## Poner el vídeo

Crea una carpeta llamada **`VideoLoop`** y mete dentro tu vídeo:

- En la memoria interna: `/sdcard/VideoLoop/mi_video.mp4`
- O en la raíz de un USB / tarjeta SD: `USB/VideoLoop/mi_video.mp4`
  (si hay un USB con la carpeta, tiene prioridad sobre la memoria interna)

Formatos: mp4, mkv, mov, avi, webm, ts, m4v, 3gp, mpg, wmv, flv.

Por defecto **las dos pantallas reproducen el mismo vídeo**. Si quieres un vídeo
distinto en cada una, llámalos `pantalla1.mp4` y `pantalla2.mp4`.

Para cambiar el vídeo, sustituye el archivo y reinicia la pantalla (o vuelve a
abrir la app). Si la app no encuentra vídeo, lo vuelve a buscar cada 10 segundos.

## Cómo funciona

- **Pantalla 1**: la app a pantalla completa, con sonido.
- **Pantalla 2** (si eliges 2 pantallas): se usa la API `Presentation` de Android
  para mostrar el vídeo en la segunda pantalla (sin sonido, para que no se oiga
  duplicado).
- Si la orientación elegida no coincide con la de la pantalla, el vídeo se gira 90º.
- Reproductor **ExoPlayer (Media3)**, que hace el bucle sin cortes y se recupera solo
  si hay un error.
- La pantalla nunca se apaga mientras se reproduce.

> Si Android ve las dos pantallas como **una sola pantalla grande** (modo "espejo"
> o "extendido" configurado en la placa), el vídeo saldrá en esa única pantalla.
> En ese caso hay que configurar la doble pantalla en Ajustes → Pantalla de la
> placa como pantallas independientes.

## Si no arranca solo al encender

Algunas placas bloquean el arranque automático. Comprueba que el permiso
"Mostrar sobre otras aplicaciones" está activado. Como alternativa, puedes poner
VideoLoop como app de inicio con adb:

```
adb shell cmd package set-home-activity com.videoloop.app/.MainActivity
```

## Compilar

```
./gradlew assembleRelease
```

El APK queda en `app/build/outputs/apk/release/app-release.apk`. Cada `push` a
GitHub también lo compila (pestaña *Actions*).
