package br.com.spotpromo.nestle_dpa_novo.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.core.content.FileProvider
import android.util.Base64
import android.util.Log
import android.widget.Toast
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class SupportImagem(private val mContext: Context) {

    /**
     * Metodo que obtem file do ResultRequest de retorno da Camera/Galeria
     *
     * @param data
     * @return
     */
    fun obtemImagemIntentData(data: Intent?): File {
        if (data == null)
            return mFile

        val selectedImage = data.data
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        val nUri = data.data!!.lastPathSegment

        val cursor = mContext.contentResolver.query(
            selectedImage!!,
            filePathColumn, null, null, null
        )
        cursor!!.moveToFirst()

        val picturePath: String
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        picturePath = cursor.getString(columnIndex)
        cursor.close()

        return File(picturePath)
    }

    /**
     * Inicializa camera a partir de uma activity
     *
     * @param activity
     */
    fun showCamera(activity: Activity) {
        this.showCameraPadrao(activity)
    }

    /**
     * Inicializa camera a partir de um fragment
     *
     * @param fragment
     */
    fun showCamera(fragment: androidx.fragment.app.Fragment) {
        this.showCameraPadrao(fragment)
    }

    /**
     * Inicializa camera
     *
     * @param context
     */
    private fun showCameraPadrao(context: Any) {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile))

        if (context is androidx.fragment.app.Fragment)
            context.startActivityForResult(intent, RESULT_CAMERA)
        else if (context is Activity)
            context.startActivityForResult(intent, RESULT_CAMERA)
    }

    /**
     * Inicializa galeria a partir de uma activity
     *
     * @param activity
     */
    fun showGaleria(activity: Activity) {
        this.showGaleriaPadrao(activity)
    }

    /**
     * Inicializa galeria a partir de um fragment
     *
     * @param fragment
     */
    fun showGaleria(fragment: androidx.fragment.app.Fragment) {
        this.showGaleriaPadrao(fragment)
    }

    /**
     * Inicializa galeria
     *
     * @param context
     */
    private fun showGaleriaPadrao(context: Any) {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        if (context is androidx.fragment.app.Fragment)
            context.startActivityForResult(intent, RESULT_GALERIA)
        else if (context is Activity)
            context.startActivityForResult(intent, RESULT_GALERIA)
    }

    /**
     * Inicializa opcao a partir de uma activity
     *
     * @param activity
     */
    @Throws(Exception::class)
    fun showOpcao(activity: Activity) {
        this.showOpcaoPadrao(activity)
    }

    /**
     * Inicializa camera a partir de um fragment
     *
     * @param fragment
     */
    @Throws(Exception::class)
    fun showOpcao(fragment: androidx.fragment.app.Fragment) {
        this.showOpcaoPadrao(fragment)
    }

    /**
     * Inicializa opcao
     *
     * @param object
     */
    @Throws(Exception::class)
    private fun showOpcaoPadrao(`object`: Any) {

        val intentGaleria = Intent(Intent.ACTION_PICK)
        intentGaleria.type = "image/*"

        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile))

        val chooserIntent = Intent.createChooser(intentGaleria, "Selecione uma imagem")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intentCamera))

        if (`object` is androidx.fragment.app.Fragment)
            `object`.startActivityForResult(chooserIntent, RESULT_OPCAO)
        else if (`object` is Activity)
            `object`.startActivityForResult(chooserIntent, RESULT_OPCAO)
    }

    companion object {

        val RESULT_OPCAO = 0
        val RESULT_CAMERA = 1
        val RESULT_GALERIA = 2
        var mFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "supportImagem.jpg"
        )

        /**
         * Metodo encontrado no site do Android para mostrar corretamento um bitmap
         *
         * @param bytes
         * @param reqWidth
         * @param reqHeight
         * @return
         * @link http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
         */
        fun decodeSampledBitmap(bytes: ByteArray, reqWidth: Int, reqHeight: Int): Bitmap {

            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        }

        @Throws(FileNotFoundException::class)
        fun decodeSampledBitmapFromFile(file: File, reqWidth: Int, reqHeight: Int): Bitmap? {
            // calculating image size
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            BitmapFactory.decodeStream(FileInputStream(file), null, options)

            val scale = calculateInSampleSize(options, reqWidth, reqHeight)

            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale

            return BitmapFactory.decodeStream(FileInputStream(file), null, o2)

        }

        internal fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
        ): Int {
            // Raw height and width of image.
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        /**
         * Retorna bitmap com a posição da foto ajustada
         *
         * @param file
         * @param reqWidth
         * @param reqHeight
         * @return
         * @throws Exception
         */
        @Throws(Exception::class)
        fun ajustOrientation(file: File, reqWidth: Int, reqHeight: Int): Bitmap? {
            val matrix: Matrix
            var bitmap = SupportImagem.decodeSampledBitmapFromFile(file, reqWidth, reqHeight)
            val exif = ExifInterface(file.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            var rotate = 0
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }

            val outWidth: Int
            val outHeight: Int
            val inWidth = bitmap!!.width
            val inHeight = bitmap.height
            if (inWidth > inHeight) {
                outWidth = reqWidth
                outHeight = inHeight * reqWidth / inWidth
            } else {
                outHeight = reqWidth
                outWidth = inWidth * reqWidth / inHeight
            }

            if (rotate > 0) {
                matrix = Matrix()
                matrix.postRotate(rotate.toFloat())

                bitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false)
                bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } else {

                matrix = Matrix()
                matrix.postRotate(0f)

                bitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false)
                bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)

            }

            return bitmap
        }

        /**
         * Retorna bitmap com a posição da foto ajustada
         *
         * @param file
         * @param reqWidth
         * @param reqHeight
         * @return
         * @throws Exception
         */
        @Throws(Exception::class)
        fun ajustOrientationFile(file: File, reqWidth: Int, reqHeight: Int): File {
            val matrix: Matrix
            val os: OutputStream
            var bitmap = SupportImagem.decodeSampledBitmapFromFile(file, reqWidth, reqHeight)
            val exif = ExifInterface(file.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            var rotate = 0
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }

            val outWidth: Int
            val outHeight: Int
            val inWidth = bitmap!!.width
            val inHeight = bitmap.height
            if (inWidth > inHeight) {
                outWidth = reqWidth
                outHeight = inHeight * reqWidth / inWidth
            } else {
                outHeight = reqWidth
                outWidth = inWidth * reqWidth / inHeight
            }

            if (rotate > 0) {
                matrix = Matrix()
                matrix.postRotate(rotate.toFloat())

                bitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false)
                bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } else {

                matrix = Matrix()
                matrix.postRotate(0f)

                bitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false)
                bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)

            }

            os = FileOutputStream(file)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()

            return file
        }

        /**
         * Converte o arquivo em base64
         *
         * @return
         * @throws Exception
         */
        @Throws(Exception::class)
        fun convertBase64(bitmap: Bitmap): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        /**
         * Converte o arquivo em base64
         *
         * @return
         * @throws Exception
         */
        @Throws(Exception::class)
        fun convertBase64(file: File, width: Int, height: Int): String {
            val bitmap = SupportImagem.decodeSampledBitmapFromFile(file, width, height)
            return SupportImagem.convertBase64(bitmap!!)
        }

        /**
         * Converte base64 em bitmap
         *
         * @return
         * @throws Exception
         */
        @Throws(Exception::class)
        fun convertBase64(base64: String, width: Int, height: Int): Bitmap {
            val decodedString = Base64.decode(base64, Base64.DEFAULT)
            return SupportImagem.decodeSampledBitmap(decodedString, width, height)
        }

        /**
         * ABRE CAMERA
         * @param activity
         * @param nomeDiretorio
         * @param APPLICATION_ID
         * @return
         */
        fun abrirCamera(activity: Activity, nomeDiretorio: String, APPLICATION_ID: String): File {
            var photo: File? = null
            try {
                val intent = Intent("android.media.action.IMAGE_CAPTURE")
                photo = CriarFileTemporario(nomeDiretorio)

                val mImageUri = FileProvider.getUriForFile(activity, "$APPLICATION_ID.provider", photo!!)

                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
                activity.startActivityForResult(intent, 1)
            } catch (e: Exception) {
                Log.v("TAG", "Can't create file to take picture!")
                Toast.makeText(activity, "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG).show()
            }

            return photo!!
        }


        /**
         * ABRE GALERIA
         * @param activity
         * @return
         */
        fun abrirGaleria(activity: Activity, retorno: Int?) {

            try {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                activity.startActivityForResult(Intent.createChooser(intent, "Selecione as Fotos"), retorno!!)
            } catch (e: Exception) {
                Log.v("TAG", "Não é possível selecionar a foto.")
            }

        }



        /**
         * ABRE CAMERA
         *
         * @param activity
         * @param nomeDiretorio
         * @param APPLICATION_ID
         * @return
         */
        fun abrirCameraString(activity: Activity, nomeDiretorio: String, APPLICATION_ID: String): String {
            var mFoto: String? = ""
            try {
                val intent = Intent("android.media.action.IMAGE_CAPTURE")
                mFoto = CriarFileTemporarioString(nomeDiretorio)

                val file = File(mFoto!!)
                val mImageUri = FileProvider.getUriForFile(activity, "$APPLICATION_ID.provider", file)

                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
                activity.startActivityForResult(intent, 1)
            } catch (e: Exception) {
                Log.v("TAG", "Can't create file to take picture!")
                Toast.makeText(activity, "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG).show()
            }

            return mFoto!!
        }


        /**
         * CRIA ARQUIVO TEMPORARIO
         *
         * @param nomeDiretorio
         * @return
         * @throws Exception
         */
        @Throws(Exception::class)
        fun CriarFileTemporario(nomeDiretorio: String): File? {

            val mediaStorageDir =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeDiretorio)
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null
                }
            }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val mediaFile: File
            mediaFile = File(mediaStorageDir.path + File.separator + nomeDiretorio + "_" + timeStamp + ".jpg")

            return mediaFile
        }

        /**
         * CRIA ARQUIVO TEMPORARIO STRING
         *
         * @param nomeDiretorio
         * @return
         * @throws Exception
         */
        @Throws(Exception::class)
        fun CriarFileTemporarioString(nomeDiretorio: String): String? {

            val mediaStorageDir =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeDiretorio)
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null
                }
            }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val mediaFile: File
            mediaFile = File(mediaStorageDir.path + File.separator + nomeDiretorio + "_" + timeStamp + ".jpg")

            return mediaFile.absolutePath
        }

        /**
         * ABRE GALERIA
         *
         * @param activity
         * @return
         */
        fun abrirDiretorioArquivo(activity: Activity, retorno: Int?) {

            try {
                val intent = Intent()
                intent.type = "*/*"
                intent.action = Intent.ACTION_GET_CONTENT
                activity.startActivityForResult(Intent.createChooser(intent, "Selecione (Foto ou Arquivo)"), retorno!!)
            } catch (e: Exception) {
                Log.v("TAG", "Não é possível selecionar.")
            }

        }



    }
}
