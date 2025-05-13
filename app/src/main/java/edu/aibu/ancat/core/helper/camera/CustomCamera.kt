package edu.aibu.ancat.core.helper.camera

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

import kotlin.hashCode
import kotlin.text.toFloat

class CustomCamera(
    private val onQrCodesDetected: (qrCodesCount: Int, corners: List<List<Pair<Float, Float>>>) -> Unit,
    private val onAllQrCodesDetected: () -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .enableAllPotentialBarcodes() // Tüm potansiyel barkodları etkinleştir
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    // Son 5 taramanın sonuçlarını tutan liste
    private val lastResults = mutableListOf<List<Barcode>>()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // QR kodlarını tara
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Sonucu son sonuçlar listesine ekle
                    if (lastResults.size >= 5) lastResults.removeAt(0)
                    lastResults.add(barcodes)

                    // Son 5 taramanın ortalamasını al (daha kararlı sonuç için)
                    val detectedBarcodes = aggregateResults()

                    // QR kodlarının köşe koordinatlarını al
                    val corners = detectedBarcodes.mapNotNull { barcode ->
                        barcode.cornerPoints?.map { point ->
                            Pair(point.x.toFloat(), point.y.toFloat())
                        }
                    }

                    val qrCount = detectedBarcodes.size

                    // QR kodu sayısını ve köşe koordinatlarını geri bildir
                    onQrCodesDetected(qrCount, corners)

                    // 4 QR kodu tespit edildiyse fotoğraf çekilmesini tetikle
                    if (qrCount >= 4) {
                        onAllQrCodesDetected()
                    }
                }
                .addOnCompleteListener {
                    // Her zaman ImageProxy'i kapat
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    // Son birkaç taramanın sonuçlarını birleştirerek daha kararlı bir sonuç üretir
    private fun aggregateResults(): List<Barcode> {
        if (lastResults.isEmpty()) return emptyList()

        // Tüm sonuçları birleştir, unique barcode'ları bul
        val allBarcodes = mutableMapOf<String, Barcode>()

        lastResults.forEach { barcodes ->
            barcodes.forEach { barcode ->
                val key = barcode.rawValue ?: barcode.hashCode().toString()
                // Sadece eğer daha önce eklenmemişse ekle
                if (!allBarcodes.containsKey(key)) {
                    allBarcodes[key] = barcode
                }
            }
        }

        return allBarcodes.values.toList()
    }

    fun checkCameraPermissions(context: Context) {
        if (!hasCameraPermissions(context)) {
            ActivityCompat.requestPermissions(context as Activity, CAMERAX_PERMISSIONS, 0)
        }
    }

    private fun hasCameraPermissions(context: Context): Boolean {
        return CAMERAX_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA
        )
    }
}