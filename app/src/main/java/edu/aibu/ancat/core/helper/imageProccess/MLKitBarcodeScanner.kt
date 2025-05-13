package edu.aibu.ancat.core.helper.imageProccess


import android.content.ContentValues
import android.content.Context
import android.graphics.*
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.File
import java.io.FileOutputStream

import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class MLKitBarcodeScanner {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .enableAllPotentialBarcodes()
            .build()
    )
    fun processImageFromUri(
        context: Context,
        imageUri: Uri,
        result: MutableState<String?>,
        onComplete: (Boolean, List<Barcode>, String?) -> Unit
    ) {
        try {
            val bitmap = loadBitmap(context, imageUri)
            val image = InputImage.fromFilePath(context, imageUri)
            //val image = InputImage.fromBitmap(bitmap, 0)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->

                    result.value = "Barkod tespit edildi: ${barcodes.size} adet"

                    // Kutuları çiz
                    val marked = drawBoxes(bitmap, barcodes)
                    // Cache + galeriye kaydet
                    val savedPath = saveAndGallery(context, marked)
                    onComplete(barcodes.isNotEmpty(), barcodes, savedPath)
                }
                .addOnFailureListener { e ->
                    result.value = "Tarama hatası"
                    onComplete(false, emptyList(), null)
                }
        } catch (e: Exception) {
            result.value = "Görüntü işleme hatası: ${e.message}"
            onComplete(false, emptyList(), null)
        }
    }

    private fun loadBitmap(ctx: Context, uri: Uri): Bitmap {
        var tmp: File? = null
        try {
            tmp = File(ctx.cacheDir, "tmp_${System.currentTimeMillis()}.jpg").apply {
                ctx.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(this).use { output -> input.copyTo(output) }
                } ?: throw IllegalArgumentException("URI için akış açılamadı: $uri")
            }
            
            val ex = ExifInterface(tmp.path)
            val ori = ex.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            
            // Görüntünün boyutlarını önce kontrol et
            val opts = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(tmp.path, opts)
            
            // Bellek sorunlarını önlemek için büyük görüntüleri yeniden boyutlandır
            val sampleSize = calculateInSampleSize(opts, 2048, 2048)
            
            // Gerçek bitmap'i yükle
            opts.apply {
                inJustDecodeBounds = false
                inPreferredConfig = Bitmap.Config.ARGB_8888
                inSampleSize = sampleSize
                inScaled = false
            }
            
            val bmp = BitmapFactory.decodeFile(tmp.path, opts)
                ?: throw IllegalStateException("Görüntü dosyası okunamadı: ${tmp.path}")
            
            return when (ori) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bmp, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bmp, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bmp, 270f)
                else -> bmp
            }
        } catch (e: Exception) {
            throw IllegalStateException("Görüntü yüklenirken hata oluştu: ${e.message}", e)
        } finally {
            // İşlem bittiğinde geçici dosyayı temizle
            tmp?.delete()
        }
    }
    
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height, width) = options.outHeight to options.outWidth
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }

    private fun rotate(b: Bitmap, angle: Float): Bitmap {
        val m=Matrix().apply{postRotate(angle)}
        return Bitmap.createBitmap(b,0,0,b.width,b.height,m,true)
    }

    private fun drawBoxes(src: Bitmap, codes: List<Barcode>): Bitmap {
        val out=src.copy(Bitmap.Config.ARGB_8888,true)
        val c=Canvas(out)
        val p=Paint().apply{style=Paint.Style.STROKE;strokeWidth=6f;color=Color.GREEN}
        codes.forEach{ it.boundingBox?.let{ b->c.drawRect(b,p) } }
        return out
    }

    private fun saveAndGallery(ctx: Context, bmp: Bitmap): String {
        // 1) Cache’e kaydet
        val file=File(ctx.cacheDir,"qr_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use{ bmp.compress(Bitmap.CompressFormat.JPEG,100,it) }
        // 2) Galeriye kaydet
        val values=ContentValues().apply{
            put(MediaStore.Images.Media.DISPLAY_NAME,file.name)
            put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
            put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis())
            if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.Q){
                put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/AnCat")
                put(MediaStore.Images.Media.IS_PENDING,1)
            }
        }
        val uri=ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        uri?.let{
            ctx.contentResolver.openOutputStream(it)?.use{ out-> file.inputStream().copyTo(out) }
            if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.Q){
                values.clear(); values.put(MediaStore.Images.Media.IS_PENDING,0)
                ctx.contentResolver.update(it,values,null,null)
            }
        }
        return file.absolutePath
    }
}

