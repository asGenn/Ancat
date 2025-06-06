package edu.aibu.ancat.ui.views.camera_screen

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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_BASE_WITH_FILTER
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.core.helper.imageProccess.MLKitBarcodeScanner
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyAnalysisResult
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.utils.DocumentConstants.PAGE_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream

import javax.inject.Inject

import kotlin.math.abs
import kotlin.math.atan2

@HiltViewModel
class CameraScreenViewModel @Inject constructor() : ViewModel() {


    //lateinit var jsonObject: List<SurveyItem>
    // CameraScreenViewModel.kt içinde
// Standart tarama için seçenekler (sayfa limiti yok)
    private val standardOption = GmsDocumentScannerOptions.Builder()
        .setScannerMode(SCANNER_MODE_FULL )
        .setGalleryImportAllowed(true)
        .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
        .build()

    // Yeniden çekim için seçenekler (sayfa limiti 1)
    private val retakeOption = GmsDocumentScannerOptions.Builder()
        .setScannerMode(SCANNER_MODE_FULL)
        .setGalleryImportAllowed(true)
        .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
        .setPageLimit(1) // Sayfa sınırını 1 olarak ayarla
        .build()

    // İki ayrı scanner nesnesi oluştur
    val scanner = GmsDocumentScanning.getClient(standardOption)
    val retakeScanner = GmsDocumentScanning.getClient(retakeOption)


    val jsonHelper = JsonHelper()

    private val _imageUris = mutableStateOf<List<Uri>>(emptyList())
    val imageUris: List<Uri> get() = _imageUris.value

    // Barkod tarama için MLKit tarayıcı
    private val barcodeScanner = MLKitBarcodeScanner()

    // Dialog görünürlüğü için state
    private val _showProblemDialog = mutableStateOf(false)
    val showProblemDialog: Boolean get() = _showProblemDialog.value

    // Problemli görüntüler için state
    private val _problemImages = mutableStateOf<List<ProblemImage>>(emptyList())
    val problemImages: List<ProblemImage> get() = _problemImages.value



    // CameraScreenViewModel içinde
    private val _retakeImageTrigger = mutableStateOf<Boolean>(false)
    val retakeImageTrigger: Boolean get() = _retakeImageTrigger.value

    private val _imageToRetake = mutableStateOf<Uri?>(null)
    //val imageToRetake: Uri? get() = _imageToRetake.value

    // CameraScreenViewModel.kt içinde
// Otomatik analiz bayrağı ekleyelim
    private val _autoAnalyzeAfterScan = mutableStateOf(false)
    val autoAnalyzeAfterScan: Boolean get() = _autoAnalyzeAfterScan.value

    fun retakeSpecificImage(uri: Uri) {
        // Seçilen resmi listeden kaldır
        val updatedUris = _imageUris.value.toMutableList()
        val position = updatedUris.indexOf(uri)
        if (position != -1) {
            updatedUris.removeAt(position)
            _imageUris.value = updatedUris
        }

        // Dialog kapat
        _showProblemDialog.value = false

        // Otomatik analiz bayrağını aktif et
        _autoAnalyzeAfterScan.value = true

        // Yeniden çekim için tetikleyiciyi ayarla
        _imageToRetake.value = uri
        _retakeImageTrigger.value = true
    }
    // Scanner başlatıldıktan sonra tetikleyiciyi sıfırla
    fun resetRetakeTrigger() {
        _retakeImageTrigger.value = false
        _imageToRetake.value = null
    }

    fun handleScanResult(scanner: ActivityResult) {
        if (scanner.resultCode == RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(scanner.data)
            if (result != null) {
                // Mevcut resimleri koru ve yenilerini ekle
                val currentUris = _imageUris.value.toMutableList()
                val newUris = result.pages?.map { it.imageUri } ?: emptyList()
                currentUris.addAll(newUris)
                _imageUris.value = currentUris
                analyzeStatus.value = "Belgeler tarandı, analiz için hazır"

                // Eğer otomatik analiz bayrağı aktifse tarama sonrası hemen analiz yap
                if (_autoAnalyzeAfterScan.value) {
                    _autoAnalyzeAfterScan.value = false
                    // Kontekst gerektiği için bunu dışarıdan tetikleyeceğiz
                }
            }
        }
    }

    // Dialog kapatma
    fun closeProblemDialog() {
        _showProblemDialog.value = false
    }






    // Barkod analizi durum bilgisi
    val analyzeProgress = mutableFloatStateOf(0f)
    val isAnalyzing = mutableStateOf(false)
    val analyzeStatus = mutableStateOf("")

    // Tespit edilen barkod verilerini tutacak liste
    private val _detectedBarcodes = mutableStateOf<List<BarcodeData>>(emptyList())


    fun analyzeAllImages(context: Context) {
        if (_imageUris.value.isEmpty()) {
            analyzeStatus.value = "Analiz edilecek resim bulunamadı"
            return
        }

        isAnalyzing.value = true
        analyzeProgress.floatValue = 0f
        analyzeStatus.value = "Analiz başlıyor..."

        val tempBarcodes = mutableListOf<BarcodeData>()
        val barcodeBounds = mutableListOf<Rect?>()
        var processedCount = 0
        val problemImages = mutableListOf<ProblemImage>()
        val successfullyAnalyzedUris = mutableListOf<Uri>() // Başarıyla analiz edilen URI'ler

        // Her bir resim için barkod taraması yap
        _imageUris.value.forEachIndexed { index, uri ->
            val progressMessage = "Resim ${index + 1}/${_imageUris.value.size} analiz ediliyor..."
            analyzeStatus.value = progressMessage

            val barcodeResult = mutableStateOf<String?>(null)
            val currentBarcodeBounds = mutableListOf<Rect?>()
            val currentBarcodes = mutableListOf<BarcodeData>()

            barcodeScanner.processImageFromUri(
                context = context,
                imageUri = uri,
                result = barcodeResult,
                onComplete = { success, barcodes, savedImagePath ->
                    processedCount++
                    analyzeProgress.floatValue = processedCount.toFloat() / _imageUris.value.size

                    // Başarısız tarama veya yetersiz barkod sayısı kontrolü
                    if (!success || barcodes.size < 2) {
                        problemImages.add(
                            ProblemImage(
                                uri = uri,
                                reason = if (!success) "Tarama başarısız" else "Yetersiz QR kod (${barcodes.size})",
                                index = index
                            )
                        )
                        analyzeStatus.value = "Bu resimde yeterli QR kod tespit edilemedi"
                    } else {
                        // QR içeriği kontrolü
                        var validBarcodeCount = 0
                        var hasSkewedQRCode = false
                        currentBarcodeBounds.clear()
                        currentBarcodes.clear()

                        barcodes.forEach { barcode ->
                            try {
                                // QR kodunun eğimini kontrol et
                                val cornerPoints = barcode.cornerPoints
                                if (cornerPoints != null && cornerPoints.size >= 4) {
                                    // Köşe noktalarını kullanarak eğimi hesapla
                                    val isSkewed = isQRCodeSkewed(cornerPoints)
                                    if (isSkewed) {
                                        hasSkewedQRCode = true
                                    }
                                }

                                val jsonData = barcode.displayValue?.let { content ->
                                    Json.decodeFromString<QrPayload>(content)
                                }

                                currentBarcodeBounds.add(barcode.boundingBox)
                                currentBarcodes.add(
                                    BarcodeData(
                                        content = barcode.displayValue ?: "",
                                        format = getBarcodeFormatName(barcode.format),
                                        imageUri = uri,
                                        processedImagePath = savedImagePath,
                                        jsonData = jsonData,
                                        barcode = barcode
                                    )
                                )
                                validBarcodeCount++
                            } catch (e: Exception) {
                                analyzeStatus.value = "Bu QR kod geçerli bir JSON verisi içermiyor"
                                Log.e("BarcodeScanner", "JSON parse hatası", e)
                            }
                        }

                        if (hasSkewedQRCode) {
                            // Eğik QR kodu tespit edildiğinde problem olarak işaretle
                            problemImages.add(
                                ProblemImage(
                                    uri = uri,
                                    reason = "QR kodlar düz değil, lütfen kamerayı dik tutarak yeniden çekin",
                                    index = index
                                )
                            )
                            analyzeStatus.value = "QR kodlar eğik çekilmiş, düz çekim yapınız"
                        } else if (validBarcodeCount < 2) {
                            problemImages.add(
                                ProblemImage(
                                    uri = uri,
                                    reason = "Geçerli QR kod sayısı yetersiz",
                                    index = index
                                )
                            )
                            analyzeStatus.value = "Bu resimde geçerli QR kod sayısı yetersiz"
                        } else {
                            analyzeStatus.value = "$validBarcodeCount geçerli QR kod tespit edildi"

                            // JSON verilerini işle ve barkodları ekle
                            tempBarcodes.addAll(currentBarcodes)
                            barcodeBounds.addAll(currentBarcodeBounds)

                            // Barkod verileri işleme
                            try {
                                val jsonObject = jsonHelper.readJsonFile(
                                    currentBarcodes[0].jsonData!!.jsonFileName,
                                    context
                                )
                                val decodedObject = Json.decodeFromString<List<SurveyItem>>(jsonObject)

                                // Barkod verilerini işle
                                processDetectedData(
                                    barcodes = currentBarcodes,
                                    context = context,
                                    uri = uri,
                                    jsonObject = decodedObject,
                                    barcodeBounds = barcodeBounds.toMutableList()
                                )

                                // Başarıyla analiz edilen URI'leri listeye ekle
                                successfullyAnalyzedUris.add(uri)
                            } catch (e: Exception) {
                                problemImages.add(
                                    ProblemImage(
                                        uri = uri,
                                        reason = "JSON dosyası okunamadı: ${e.message}",
                                        index = index
                                    )
                                )
                                analyzeStatus.value = "JSON dosyası okunamadı"
                                Log.e("BarcodeScanner", "JSON dosyası okuma hatası", e)
                            }
                        }
                    }

                    // Tüm resimler işlendiyse sonuçları güncelle
                    if (processedCount >= _imageUris.value.size) {
                        // Başarıyla analiz edilen resimleri listeden kaldır
                        if (successfullyAnalyzedUris.isNotEmpty()) {
                            val remainingUris = _imageUris.value.toMutableList()
                            remainingUris.removeAll(successfullyAnalyzedUris)
                            _imageUris.value = remainingUris

                            // Başarıyla analiz edilen resimleri problem listesinden de kaldır
                            val updatedProblemImages = problemImages.filter { problemImage ->
                                !successfullyAnalyzedUris.contains(problemImage.uri)
                            }
                            problemImages.clear()
                            problemImages.addAll(updatedProblemImages)
                        }

                        _detectedBarcodes.value = tempBarcodes
                        isAnalyzing.value = false

                        if (tempBarcodes.isEmpty()) {
                            analyzeStatus.value = "Hiç QR kod tespit edilemedi"
                            showProblemImagesDialog( problemImages)
                        } else if (problemImages.isNotEmpty()) {
                            analyzeStatus.value = "Bazı resimlerde QR kod tespit edilemedi"
                            showProblemImagesDialog( problemImages)
                        } else {
                            analyzeStatus.value = "Analiz tamamlandı, ${tempBarcodes.size} QR kod bulundu"
                        }
                    }
                }
            )
        }
    }
    // QR kodunun eğimli olup olmadığını kontrol et
    private fun isQRCodeSkewed(cornerPoints: Array<android.graphics.Point>): Boolean {
        if (cornerPoints.size < 4) return false

        // Yatay kenarların eğimini hesapla
        val topEdgeAngle = calculateAngle(cornerPoints[0], cornerPoints[1])
        val bottomEdgeAngle = calculateAngle(cornerPoints[3], cornerPoints[2])


        // Dikey kenarların eğimini hesapla
        val leftEdgeAngle = calculateAngle(cornerPoints[0], cornerPoints[3])
        val rightEdgeAngle = calculateAngle(cornerPoints[1], cornerPoints[2])

        println( " $topEdgeAngle $bottomEdgeAngle $leftEdgeAngle $rightEdgeAngle")

        // Kabul edilebilir eğim açısı (derece)
        val maxAllowedAngle = 2

        // Yatay kenarlar yaklaşık olarak yatay (0 derece), dikey kenarlar yaklaşık olarak dikey (90 derece) olmalı
        val isHorizontalSkewed = abs(topEdgeAngle) > maxAllowedAngle || Math.abs(bottomEdgeAngle) > maxAllowedAngle
        val isVerticalSkewed = abs(leftEdgeAngle - 90) > maxAllowedAngle || abs(rightEdgeAngle - 90) > maxAllowedAngle

        return isHorizontalSkewed || isVerticalSkewed
    }

    // İki nokta arasındaki çizginin yatay eksene göre açısını hesapla (derece)
    private fun calculateAngle(p1: android.graphics.Point, p2: android.graphics.Point): Double {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        val angleRadians = atan2(dy.toDouble(), dx.toDouble())
        return Math.toDegrees(angleRadians)
    }

    // Problem resim data class
    data class ProblemImage(
        val uri: Uri,
        val reason: String,
        val index: Int
    )

    // Compose Dialog
    fun showProblemImagesDialog( problemImages: List<ProblemImage>) {
        if (problemImages.isEmpty()) {
            // Problem kalmadıysa dialog gösterme
            _showProblemDialog.value = false
            return
        }

        // Compose Dialog için UI state'ini güncelleyin
        _showProblemDialog.value = true
        _problemImages.value = problemImages
    }

    // MediaStore'a taranan görüntüleri kaydetme fonksiyonu
//    fun saveScannedImagesToMediaStore(context: Context) {
//        _imageUris.value.forEach { uri ->
//            saveImageToMediaStore(context, uri)
//        }
//    }

//    // Bir Uri'yi MediaStore'a kaydetme
//    private fun saveImageToMediaStore(context: Context, imageUri: Uri) {
//        val contentResolver = context.contentResolver
//        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val fileName = "ANCAT_SCAN_$timestamp.jpg"
//
//        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
//        if (inputStream != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                val contentValues = ContentValues().apply {
//                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
//                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Ancat")
//                }
//
//                val uri = contentResolver.insert(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    contentValues
//                )
//                uri?.let { outputUri ->
//                    val outputStream: OutputStream? = contentResolver.openOutputStream(outputUri)
//                    outputStream?.use { output ->
//                        inputStream.copyTo(output)
//                    }
//                }
//            } else {
//                // API 29'dan düşük sürümler için klasik dosya sistemini kullan
//                val imagesDir = File(context.getExternalFilesDir(null), "Ancat")
//                if (!imagesDir.exists()) imagesDir.mkdirs()
//
//                val file = File(imagesDir, fileName)
//                val outputStream = FileOutputStream(file)
//                outputStream.use { output ->
//                    inputStream.copyTo(output)
//                }
//
//                // MediaStore'a ekle
//                val contentValues = ContentValues().apply {
//                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
//                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//                    put(MediaStore.Images.Media.DATA, file.absolutePath)
//                }
//                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//            }
//            inputStream.close()
//        }
//    }

//    // MediaStore'dan görüntüleri al
//    fun getImagesFromMediaStore(context: Context): List<Uri> {
//        val images = mutableListOf<Uri>()
//        val contentResolver = context.contentResolver
//
//        val projection = arrayOf(
//            MediaStore.Images.Media._ID,
//            MediaStore.Images.Media.DISPLAY_NAME
//        )
//
//        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
//        val selectionArgs = arrayOf("ANCAT_SCAN_%")
//        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
//
//        val query = contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            projection,
//            selection,
//            selectionArgs,
//            sortOrder
//        )
//
//        query?.use { cursor ->
//            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//
//            while (cursor.moveToNext()) {
//                val id = cursor.getLong(idColumn)
//                val contentUri = Uri.withAppendedPath(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    id.toString()
//                )
//                images.add(contentUri)
//            }
//        }
//
//        return images
//    }

    // Barkod formatının adını döndür
    private fun getBarcodeFormatName(format: Int): String {
        return when (format) {
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
        var result: List<SurveyAnalysisResult>

        if (JsonHelper().checkFileExists(
                fileName = "${barcodes[0].jsonData!!.jsonFileName}_result",
                context = context
            )
        ) {

            val jsonResult = JsonHelper().readJsonFile(
                fileName = "${barcodes[0].jsonData!!.jsonFileName}_result",
                context = context
            )
            result = Json.decodeFromString<List<SurveyAnalysisResult>>(jsonResult)

        } else {
            result = emptyList()
        }

        val resultBitmap = drawBoxOnImage(context, uri, barcodes, jsonObject, barcodeBounds, result)
        //saveAndGallery(context, resultBitmap!!)


    }

    private fun saveAndGallery(ctx: Context, bmp: Bitmap): String {
        // 1) Cache’e kaydet
        val file = File(ctx.cacheDir, "qr_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { bmp.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        // 2) Galeriye kaydet
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AnCat")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val uri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            ctx.contentResolver.openOutputStream(it)?.use { out -> file.inputStream().copyTo(out) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear(); values.put(MediaStore.Images.Media.IS_PENDING, 0)
                ctx.contentResolver.update(it, values, null, null)
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
    /**
     * İki QR koduna bakarak belgenin X ve Y eksenindeki ötelemesini hesaplar.
     *
     * @param qrTL           Sol-üst QR kodunun bounding Rect’i (piksel cinsinden)
     * @param qrBR           Sağ-alt QR kodunun bounding Rect’i (piksel cinsinden)

     * @return Pair(width,height) Belge düzleminin kameraya göre ötelemesi, birim cinsinden
     */
    fun computeTranslationXY(
        qrTL: Rect,
        qrBR: Rect,

    ): Pair<Int, Int> {

        val qrTLHeight  = qrTL.bottom - qrTL.top
        val qrTLWidth = qrTL.right - qrTL.left
        val qrBRHeight  = qrBR.bottom - qrBR.top
        val qrBRWidth = qrBR.right - qrBR.left


        val width =  qrBRWidth - qrTLWidth
        val height =  qrBRHeight - qrTLHeight







        return Pair(width, height)
    }



    fun drawBoxOnImage(
        context: Context,
        uri: Uri,
        barcodes: List<BarcodeData>,
        jsonObject: List<SurveyItem>,
        barcodeBounds: MutableList<Rect?>,
        result: List<SurveyAnalysisResult>,
    ): Bitmap? {
        val bitmap = getBitmapFromUri(context, uri) ?: return null
        val mat = bitmapToMat(bitmap)
        //val height = mat.height()
        //val width = mat.width()
        var photoHeight = 0
        var photoWidth = 0
        var topValue = 0

        val translation : Pair<Int, Int> = if (barcodeBounds[0]!!.top < barcodeBounds[1]!!.top) {

            computeTranslationXY(qrTL =barcodeBounds[0]!! , qrBR = barcodeBounds[1]!! )


        }else {
            computeTranslationXY(qrTL =barcodeBounds[1]!! , qrBR = barcodeBounds[0]!! )
        }

        val top = minOf(barcodeBounds[0]!!.top, barcodeBounds[1]!!.top)
        val bottom = maxOf(barcodeBounds[0]!!.bottom, barcodeBounds[1]!!.bottom)
        val left = minOf(barcodeBounds[0]!!.left, barcodeBounds[1]!!.left)
        val right = maxOf(barcodeBounds[0]!!.right, barcodeBounds[1]!!.right)

        topValue = top
        photoHeight = bottom - top
        photoWidth = right - left
        val x = photoWidth.toDouble() / PAGE_WIDTH
        val y = photoHeight.toDouble() / PAGE_HEIGHT

        if (mat.channels() == 1) {
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB)
        }
        println("barcodeBounds $barcodeBounds")
        val barcode = barcodes[0]
        val barcodeFirstSectionIdx = barcode.jsonData?.firstQuestion?.sectionIndex ?: 0
        val barcodeLastSectionIdx = barcode.jsonData?.lastQuestion?.sectionIndex ?: 0

        val barcodeLastQuestionIdx = barcode.jsonData?.lastQuestion?.questionIndex ?: 0
        var newResult = result
        for (i in barcodeFirstSectionIdx..barcodeLastSectionIdx) {
            jsonObject[i].questions.forEachIndexed { l, k ->
                if (l == barcodeLastQuestionIdx && i == barcodeLastSectionIdx) {
                    when (jsonObject[i].type) {

                        "1" -> {
                            newResult =
                                scanRatingQuestion(k, x, left, y, topValue, mat, newResult, i, l, translation)


                        }

                        "2" -> {
                            newResult = scanMultiChoiceQuestion(
                                k,
                                x,
                                y,
                                mat,
                                topValue,
                                left,
                                newResult,
                                i,
                                l, translation
                            )
                        }

                        else -> {

                        }

                    }
                    val resultBitmap = createBitmap(mat.cols(), mat.rows())
                    Utils.matToBitmap(mat, resultBitmap)
                    JsonHelper().saveJsonToFile(
                        Json.encodeToString(newResult),
                        context = context,
                        fileName = "${barcodes[0].jsonData!!.jsonFileName}_result"
                    )

                    return resultBitmap


                } else {
                    when (jsonObject[i].type) {
                        "1" -> {
                            newResult =
                                scanRatingQuestion(
                                    k,
                                    x,
                                    left,
                                    y,
                                    topValue,
                                    mat,
                                    newResult,
                                    i,
                                    l,
                                    translation
                                )

                        }

                        "2" -> {
                            newResult = scanMultiChoiceQuestion(
                                k,
                                x,
                                y,
                                mat,
                                topValue,
                                left,
                                newResult,
                                i,
                                l,
                                translation
                            )
                        }

                        else -> {

                        }

                    }

                }
            }

        }


        val resultBitmap = createBitmap(mat.cols(), mat.rows())
        Utils.matToBitmap(mat, resultBitmap)

        println(
            JsonHelper().saveJsonToFile(
                Json.encodeToString(newResult),
                context = context,
                fileName = "${barcodes[0].jsonData!!.jsonFileName}_result"
            )
        )
        return resultBitmap
    }

    private fun scanRatingQuestion(
        k: Question,
        x: Double,
        left: Int,
        y: Double,
        topValue: Int,
        mat: Mat,
        result: List<SurveyAnalysisResult>,
        i1: Int,
        l: Int,
        translation: Pair<Int, Int>
    ): MutableList<SurveyAnalysisResult> {
        k as Question.RatingQuestion
        var analysisList = MutableList(5) { 0 }
        var newResult = result.toMutableList()
        var controlList = false
        var controlInt = 0
        result.forEachIndexed { idx, it ->
            if (it.questionIdx == l && it.sectionIdx == i1) {
                analysisList = it.analysis.toMutableList()
                controlList = true
                controlInt = idx
            }
        }

        var tempIdx = 0
        var tempFilled = 0.0


        for (i in 0..4) {

            val topLeft = Point((440.0 + i * 25) * x + left  + translation.first, (k.mark ) * y + topValue + translation.second)
            val bottomRight = Point((455.0 + i * 25) * x + left  -translation.first, (k.mark + 16) * y + topValue - translation.second)
            val roiRect = org.opencv.core.Rect(
                topLeft.x.toInt(),
                topLeft.y.toInt(),
                (bottomRight.x - topLeft.x).toInt(),
                (bottomRight.y - topLeft.y).toInt()
            )
            val roi = Mat(mat, roiRect)
            val grayROI = Mat()

            Imgproc.cvtColor(roi, grayROI, Imgproc.COLOR_BGR2GRAY)

            // Eşikleme (karanlık alanları tespit etmek için)
            Imgproc.threshold(grayROI, grayROI, 127.0, 255.0, Imgproc.THRESH_BINARY_INV)

            val blackPixels = Core.countNonZero(grayROI)
            val totalPixels = grayROI.rows() * grayROI.cols()
            val fillRatio = blackPixels.toDouble() / totalPixels
            println("fill ratio $fillRatio  question ${k.question}")

            val isFilled = fillRatio > 0.20

            // Kutu çiz
            Imgproc.rectangle(
                mat,
                topLeft,
                bottomRight,
                if (isFilled) Scalar(0.0, 255.0, 0.0) else Scalar(
                    255.0,
                    0.0,
                    0.0
                ), // Yeşil = dolu, Kırmızı = boş
                2
            )


            if (fillRatio > tempFilled) {
                tempFilled = fillRatio
                tempIdx = i
            }


        }
        analysisList[tempIdx] += 1
        if (controlList) {
            newResult[controlInt] = SurveyAnalysisResult(
                sectionIdx = i1,
                questionIdx = l,
                type = "1",
                analysis = analysisList

            )

        } else {
            newResult.add(
                SurveyAnalysisResult(
                    sectionIdx = i1,
                    questionIdx = l,
                    type = "1",
                    analysis = analysisList

                )
            )
        }
        return newResult

    }

    private fun scanMultiChoiceQuestion(
        k: Question,
        x: Double,
        y: Double,
        mat: Mat,
        topValue: Int,
        left: Int,
        result: List<SurveyAnalysisResult>,
        i1: Int,
        l: Int,
        translation: Pair<Int, Int>
    ): MutableList<SurveyAnalysisResult> {
        k as Question.MultipleChoiceQuestion
        var analysisList = MutableList(k.marks.size) { 0 }
        var newResult = result.toMutableList()
        var controlList = false
        var controlInt = 0

        var tempIdx = 0
        var tempFilled = 0.0

        result.forEachIndexed { idx, it ->
            if (it.questionIdx == l && it.sectionIdx == i1) {
                analysisList = it.analysis.toMutableList()
                controlList = true
                controlInt = idx
            }
        }


        k.marks.forEachIndexed { idx, it ->

            val topLeft = Point(436.0 * x + left +translation.first, (it - 10) * y + topValue + translation.second)
            val bottomRight = Point(451 * x + left - translation.first, (it + 4) * y + topValue - translation.second)
            val roiRect = org.opencv.core.Rect(
                topLeft.x.toInt(),
                topLeft.y.toInt(),
                (bottomRight.x - topLeft.x).toInt(),
                (bottomRight.y - topLeft.y).toInt()
            )
            val roi = Mat(mat, roiRect)
            val grayROI = Mat()
            Imgproc.cvtColor(roi, grayROI, Imgproc.COLOR_BGR2GRAY)

            // Eşikleme (karanlık alanları tespit etmek için)
            Imgproc.threshold(grayROI, grayROI, 127.0, 255.0, Imgproc.THRESH_BINARY_INV)

            val blackPixels = Core.countNonZero(grayROI)
            val totalPixels = grayROI.rows() * grayROI.cols()
            val fillRatio = blackPixels.toDouble() / totalPixels
            println("fill ratio $fillRatio  question ${k.question}")

            val isFilled = fillRatio > 0.25

            // Kutu çiz
            Imgproc.rectangle(
                mat,
                topLeft,
                bottomRight,
                if (isFilled) Scalar(0.0, 255.0, 0.0) else Scalar(
                    255.0,
                    0.0,
                    0.0
                ), // Yeşil = dolu, Kırmızı = boş
                2
            )
            if (fillRatio > tempFilled) {
                tempFilled = fillRatio
                tempIdx = idx
            }
        }
        analysisList[tempIdx] += 1
        if (controlList) {
            newResult[controlInt] = SurveyAnalysisResult(
                sectionIdx = i1,
                questionIdx = l,
                type = "2",
                analysis = analysisList

            )

        } else {
            newResult.add(
                SurveyAnalysisResult(
                    sectionIdx = i1,
                    questionIdx = l,
                    type = "2",
                    analysis = analysisList

                )
            )
        }
        return newResult
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