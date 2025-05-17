package edu.aibu.ancat.ui.views.home_screen

import QrPayload
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect


import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.core.helper.imageProccess.MLKitBarcodeScanner
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
import kotlinx.serialization.json.Json
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import androidx.core.graphics.createBitmap
import edu.aibu.ancat.utils.DocumentConstants.PAGE_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import org.opencv.core.Core
import kotlin.math.absoluteValue

@HiltViewModel
class AnalyzeScreenViewModel @Inject constructor() : ViewModel() {


    lateinit var jsonObject : List<SurveyItem>
    private val option = GmsDocumentScannerOptions.Builder()
        .setScannerMode(SCANNER_MODE_FULL)
        .setGalleryImportAllowed(true)
        .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
        .build()

    val scanner = GmsDocumentScanning.getClient(option)
    val jsonHelper = JsonHelper()

    private val _imageUris = mutableStateOf<List<Uri>>(emptyList())
    val imageUris: List<Uri> get() = _imageUris.value

    // Barkod tarama için MLKit tarayıcı
    private val barcodeScanner = MLKitBarcodeScanner()

    // Barkod analizi durum bilgisi
    val analyzeProgress = mutableStateOf(0f)
    val isAnalyzing = mutableStateOf(false)
    val analyzeStatus = mutableStateOf("")

    // Tespit edilen barkod verilerini tutacak liste
    private val _detectedBarcodes = mutableStateOf<List<BarcodeData>>(emptyList())
    val detectedBarcodes: List<BarcodeData> get() = _detectedBarcodes.value

    fun handleScanResult(scanner: ActivityResult) {
        if (scanner.resultCode == RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(scanner.data)
            if (result != null) {
                _imageUris.value = result.pages?.map { it.imageUri } ?: emptyList()
                analyzeStatus.value = "Belgeler tarandı, analiz için hazır"
            }
        }
    }

    // Tüm resimlerdeki barkodları analiz et
    fun analyzeAllImages(context: Context) {
        if (_imageUris.value.isEmpty()) {
            analyzeStatus.value = "Analiz edilecek resim bulunamadı"
            return
        }

        isAnalyzing.value = true
        analyzeProgress.value = 0f
        analyzeStatus.value = "Analiz başlıyor..."

        val tempBarcodes = mutableListOf<BarcodeData>()
        val barcodeBounds = mutableListOf<Rect?>()
        var processedCount = 0

        // Her bir resim için barkod taraması yap
        _imageUris.value.forEachIndexed { index, uri ->
            val progressMessage = "Resim ${index + 1}/${_imageUris.value.size} analiz ediliyor..."
            analyzeStatus.value = progressMessage

            val barcodeResult = mutableStateOf<String?>(null)

            barcodeScanner.processImageFromUri(
                context = context,
                imageUri = uri,
                result = barcodeResult,
                onComplete = { success, barcodes, savedImagePath ->

                    processedCount++
                    analyzeProgress.value = processedCount.toFloat() / _imageUris.value.size


                    if (success && barcodes.isNotEmpty()) {
                        barcodes.forEach { barcode ->
                            try {
                                val barcodeValue = barcode.rawValue ?: "{}"
                                val jsonData = Json.decodeFromString<QrPayload>(barcodeValue)


                                println("Bulunan barkod: format=${barcode.format}, data=$barcodeValue")
                                println("barkod noktaları: ${barcode.cornerPoints?.joinToString(",")}")
                                println(" barkod boindingBox" +barcode.boundingBox)

                                val questionJson = jsonHelper.readJsonFile(jsonData.jsonFileName, context)
                                jsonObject = Json.decodeFromString<List<SurveyItem>>(questionJson)
                                println((jsonObject[1].questions[0] as Question.MultipleChoiceQuestion).marks)

                                barcodeBounds.add(
                                    barcode.boundingBox
                                )

                                tempBarcodes.add(
                                    BarcodeData(
                                        content = barcodeValue,
                                        format = getBarcodeFormatName(barcode.format),
                                        imageUri = uri,
                                        processedImagePath = savedImagePath,
                                        jsonData = jsonData,
                                        barcode = barcode,
                                    )
                                )

                            analyzeStatus.value = "${barcodes.size} barkod tespit edildi"
                            }catch (e: Exception){
                                Log.e("AnalyzeScreenViewModel", "Barkod verisi işlenirken hata: ${e.message}")
                                analyzeStatus.value = "Barkod verisi işlenirken hata: ${e.message}"
                            }
                        }




                    } else {
                        analyzeStatus.value = "Bu resimde barkod tespit edilemedi"
                    }

                    // Tüm resimler işlendiyse sonuçları güncelle
                    if (processedCount >= _imageUris.value.size) {
                        _detectedBarcodes.value = tempBarcodes
                        isAnalyzing.value = false

                        if (tempBarcodes.isEmpty()) {
                            analyzeStatus.value = "Hiç barkod tespit edilemedi"
                        } else {
                            analyzeStatus.value = "Analiz tamamlandı, ${tempBarcodes.size} barkod bulundu"
                            processDetectedData(tempBarcodes, context, uri,jsonObject,barcodeBounds )
                        }
                    }
                }
            )
        }
    }

    // MediaStore'a taranan görüntüleri kaydetme fonksiyonu
    fun saveScannedImagesToMediaStore(context: Context) {
        _imageUris.value.forEach { uri ->
            saveImageToMediaStore(context, uri)
        }
    }

    // Bir Uri'yi MediaStore'a kaydetme
    private fun saveImageToMediaStore(context: Context, imageUri: Uri) {
        val contentResolver = context.contentResolver
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "ANCAT_SCAN_$timestamp.jpg"

        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        if (inputStream != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Ancat")
                }

                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let { outputUri ->
                    val outputStream: OutputStream? = contentResolver.openOutputStream(outputUri)
                    outputStream?.use { output ->
                        inputStream.copyTo(output)
                    }
                }
            } else {
                // API 29'dan düşük sürümler için klasik dosya sistemini kullan
                val imagesDir = File(context.getExternalFilesDir(null), "Ancat")
                if (!imagesDir.exists()) imagesDir.mkdirs()

                val file = File(imagesDir, fileName)
                val outputStream = FileOutputStream(file)
                outputStream.use { output ->
                    inputStream.copyTo(output)
                }

                // MediaStore'a ekle
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.DATA, file.absolutePath)
                }
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            }
            inputStream.close()
        }
    }

    // MediaStore'dan görüntüleri al
    fun getImagesFromMediaStore(context: Context): List<Uri> {
        val images = mutableListOf<Uri>()
        val contentResolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("ANCAT_SCAN_%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                images.add(contentUri)
            }
        }

        return images
    }

    // Barkod formatının adını döndür
    private fun getBarcodeFormatName(format: Int): String {
        return when(format) {
            Barcode.FORMAT_QR_CODE -> "QR Code"
            Barcode.FORMAT_AZTEC -> "Aztec"
            Barcode.FORMAT_CODABAR -> "Codabar"
            Barcode.FORMAT_CODE_39 -> "Code 39"
            Barcode.FORMAT_CODE_93 -> "Code 93"
            Barcode.FORMAT_CODE_128 -> "Code 128"
            Barcode.FORMAT_DATA_MATRIX -> "Data Matrix"
            Barcode.FORMAT_EAN_8 -> "EAN-8"
            Barcode.FORMAT_EAN_13 -> "EAN-13"
            Barcode.FORMAT_ITF -> "ITF"
            Barcode.FORMAT_PDF417 -> "PDF417"
            Barcode.FORMAT_UPC_A -> "UPC-A"
            Barcode.FORMAT_UPC_E -> "UPC-E"
            else -> "Diğer"
        }
    }



    // Tespit edilen verileri işle (burada kendi özel mantığınızı uygulayabilirsiniz)
    private fun processDetectedData(
        barcodes: List<BarcodeData>,
        context: Context,
        uri: Uri,
        jsonObject: List<SurveyItem>,
        barcodeBounds: MutableList<Rect?>,
    ) {
        val resultBitmap = drawBoxOnImage(context, uri, barcodes,jsonObject, barcodeBounds)
        saveAndGallery(context, resultBitmap!!)




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

    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        }
    }
    fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        return mat
    }
    fun drawBoxOnImage(
        context: Context,
        uri: Uri,
        barcodes: List<BarcodeData>,
        jsonObject: List<SurveyItem>,
        barcodeBounds: MutableList<Rect?>,
    ): Bitmap? {
        val bitmap = getBitmapFromUri(context, uri) ?: return null
        val mat = bitmapToMat(bitmap)
        val height = mat.height()
        val width = mat.width()
        var photoHeight = 0
        var photoWidth = 0
        var topValue = 0

        val top = minOf(barcodeBounds[0]!!.top, barcodeBounds[1]!!.top)
        val bottom = maxOf(barcodeBounds[0]!!.bottom, barcodeBounds[1]!!.bottom)
        val left = minOf(barcodeBounds[0]!!.left, barcodeBounds[1]!!.left)
        val right = maxOf(barcodeBounds[0]!!.right, barcodeBounds[1]!!.right)

        topValue = top
        photoHeight = bottom - top
        photoWidth = right - left
        val x = photoWidth.toDouble() / PAGE_WIDTH
        val y = height.toDouble() / PAGE_HEIGHT

        if (mat.channels() == 1) {
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB)
        }

        for (barcode in barcodes) {
            val sectionIndex = barcode.jsonData?.lastQuestion?.sectionIndex ?: continue
            for (k in 0..3) {
                val questionIndex = barcode.jsonData?.lastQuestion?.questionIndex!! - k
                val question = (jsonObject[1].questions[questionIndex] as Question.MultipleChoiceQuestion).marks

                val topLeft = Point(440.0 * x, ((question[0].toDouble() - 10) * y))
                val bottomRight = Point(452 * x, ((question[0].toDouble() + 2) * y))

                // ROI alanını kırp
                val roiRect =org.opencv.core.Rect(
                    topLeft.x.toInt(),
                    topLeft.y.toInt(),
                    (bottomRight.x - topLeft.x).toInt(),
                    (bottomRight.y - topLeft.y).toInt()
                )
                val roi = Mat(mat , roiRect)

                // Griye çevir
                val grayROI = Mat()
                Imgproc.cvtColor(roi, grayROI, Imgproc.COLOR_BGR2GRAY)

                // Eşikleme (karanlık alanları tespit etmek için)
                Imgproc.threshold(grayROI, grayROI, 127.0, 255.0, Imgproc.THRESH_BINARY_INV)

                val blackPixels = Core.countNonZero(grayROI)
                val totalPixels = grayROI.rows() * grayROI.cols()
                val fillRatio = blackPixels.toDouble() / totalPixels

                val isFilled = fillRatio > 0.5

                // Kutu çiz
                Imgproc.rectangle(
                    mat,
                    topLeft,
                    bottomRight,
                    if (isFilled) Scalar(0.0, 255.0, 0.0) else Scalar(255.0, 0.0, 0.0), // Yeşil = dolu, Kırmızı = boş
                    2
                )

                Log.d("CheckBox", "Filled: $isFilled (Ratio: ${String.format("%.2f", fillRatio)})")
            }
        }

        val resultBitmap = createBitmap(mat.cols(), mat.rows())
        Utils.matToBitmap(mat, resultBitmap)
        return resultBitmap
    }



    // Barkod verisi veri sınıfı
    data class BarcodeData(
        val content: String,
        val format: String,
        val imageUri: Uri,
        val processedImagePath: String?,
        val jsonData: QrPayload? = null,
        val barcode: Barcode
    )
}