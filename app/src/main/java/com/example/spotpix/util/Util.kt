package com.example.spotpix.util

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.FileProvider
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import br.com.spotpromo.nestle_dpa_novo.regras.model.*
import br.com.spotpromo.nestle_dpa_novo.util.SupportImagem
import com.amazonaws.services.s3.model.ObjectMetadata
import com.example.spotpix.R
import com.example.spotpix.dao.MetadadoFotoDaoHelper
import com.example.spotpix.model.MetadadoFoto
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.ortiz.touchview.TouchImageView
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Util {

    @Throws(Exception::class)
    fun retornaDataFotoShelfPix(dateFoto: String?): String {
        var formattedDateString = ""

        if (dateFoto != null && dateFoto.isNotEmpty()) {
            val file = File(dateFoto)

            if (file != null && file.isFile) {

                /**
                 * PEGA DATA DA FOTO
                 */
                val lastModified = Date(file.lastModified())
                val formatter = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")

                formattedDateString = FormataData[FormataData.getCalendar(
                    formatter.format(lastModified),
                    FormataData.ShelfPixDate
                ), FormataData.SQL]

            } else {
                /**
                 * SE NÃO ACHAR A DATA ELE INSERI UMA ATUAL
                 */
                formattedDateString = FormataData.retornaDataFormat(FormataData.ShelfPixDate)

            }
        } else {
            /**
             * SE NÃO ACHAR A DATA ELE INSERI UMA ATUAL
             */
            formattedDateString = FormataData.retornaDataFormat(FormataData.ShelfPixDate)


        }

        return formattedDateString
    }

    /**
     * NAPER
     */
    fun retonarNomeFoto(caminhoFoto: String): String {
        val foto = caminhoFoto.split("/")

        return foto[foto.size - 1]
    }

    fun retonarNomeFotoSemExtensao(caminhoFoto: String): String {
        val foto = caminhoFoto.split("/")

        val fotoSemExtensao = foto[foto.size - 1]
            .replace(".jpeg", "")
            .replace(".png", "")
            .replace(".jpg", "")

        return fotoSemExtensao
    }



    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri
            .authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri
            .authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri
            .authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri
            .authority
    }

    fun createPictureMetadata(
        nomeFoto: String,
        codPesquisa: Int,
        context: Context
    ): ObjectMetadata {
        val metadado = ObjectMetadata()
        try {
            val metadadoFotoDaoHelper = MetadadoFotoDaoHelper(SqliteDataBaseHelper.openDB(context))
            val metadadosFoto = metadadoFotoDaoHelper
                .selectInfoMetadado(
                    nomeFoto,
                    codPesquisa,
                    context.resources.getInteger(R.integer.cod_campanha_shelfpix),
                    retornaDataFotoShelfPix(nomeFoto),
                    retonarNomeFotoSemExtensao(nomeFoto)
                )

            if (metadadosFoto != null) {
                val gsonMetadado = GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .serializeNulls()
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .create()

                val gsonStringMetadado = URLEncoder.encode(
                    gsonMetadado.toJson(metadadosFoto, MetadadoFoto::class.java),
                    StandardCharsets.UTF_8.name()
                )

                val gsonStringMarkerShare = URLEncoder.encode(
                    gsonMetadado.toJson(metadadosFoto.markerShareList),
                    StandardCharsets.UTF_8.name()
                )

                metadado.contentType = "application/json; charset=utf-8"
                metadado.addUserMetadata("picture", gsonStringMetadado)
                metadado.addUserMetadata("markerShareList", gsonStringMarkerShare)
                Log.e("METADADO", gsonStringMetadado)
                Log.e("SHARE", gsonStringMarkerShare)
            }
        } catch (e: Exception) {
            Log.e("erro metadado shelfpix", "createPictureMetadata: fail: ${e.cause}")
        }
        return metadado
    }

}