package com.example.spotpix.service

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import br.com.spotpromo.nestle_dpa_novo.util.SupportImagem
import com.example.spotpix.model.PocExhibitionPicture
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class TakePictureService {
    private val result: Result
    fun execute(request: Request, context: Context, roteiro: Int): Result {
        try {
            val context: Context = context
            val picture = request.bytes
            val pocExhibitionPicture = request.pocExhibitionPicture
            val picKey: String
            val picFileName: String
            val picDirectory: String
            var filePath: String?
            val folder = context.getExternalFilesDir(null)

            if (!folder!!.exists()) {
                if (!folder.mkdirs()) {
                    Log.e(
                        TAG,
                        "Directory not created",
                        Exception()
                    )
                    result.message = "Directory not created"
                }
            }
            picKey = UUID.randomUUID().toString()
            picFileName = "$picKey.jpeg"
            picDirectory = folder.absolutePath + "/SPOT_NESTLE_DPA/SPOT_FOTOS/$roteiro"
            filePath = picDirectory + File.separator + picFileName
            if (pocExhibitionPicture?.pepFilePath != null) {
                filePath = pocExhibitionPicture.pepFilePath
                val photo = File(filePath)
                if (photo.exists()) {
                    photo.delete() //Deleta foto existente e salva com a marcacao
                }
            }
            val byteArrayOutputStream =
                ByteArrayOutputStream()
            val bitmap: Bitmap = SupportImagem.decodeSampledBitmap(picture!!, 1080,1920)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val outputStream = FileOutputStream(filePath)
            outputStream.write(byteArrayOutputStream.toByteArray())
            outputStream.flush()
            outputStream.close()
            byteArrayOutputStream.close()
            result.picKey = picKey
            result.picFileName = picFileName
            result.filePath = filePath
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
            result.message = e.message
            result.exception = e
            result.picKey = null
            result.picFileName = null
            result.filePath = null
        }
        return result
    }

    inner class Request  {
        var bytes: ByteArray? = null
        var pocExhibitionPicture: PocExhibitionPicture? = null
        var context: Context? = null

    }

    inner class Result {
        var picKey: String? = null
        var picFileName: String? = null
        var filePath: String? = null
        var message: String? = null
        var exception: java.lang.Exception? = null
    }

    companion object {
        private val TAG = TakePictureService::class.java.simpleName
    }

    init {
        result = this.Result()
    }
}