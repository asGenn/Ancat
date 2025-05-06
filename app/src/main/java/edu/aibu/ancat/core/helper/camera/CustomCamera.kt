package edu.aibu.ancat.core.helper.camera

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CustomCamera {

    fun checkCameraPermissions(context: Context) {
        if (!hasCameraPermissions(context)) {
            // İzin yoksa, izin isteme işlemini başlat
            ActivityCompat.requestPermissions(context as Activity, CAMERAX_PERMISSIONS, 0)
        }
    }

    private fun hasCameraPermissions(context : Context): Boolean {
        return CAMERAX_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object{
        private val CAMERAX_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
        )
    }
}
