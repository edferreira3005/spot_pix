package com.example.spotpix.model

import android.graphics.Color
import android.graphics.PointF
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import com.example.spotpix.view.GalleryShareActivity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*
import java.util.stream.Collectors


@Entity(
    foreignKeys = [ForeignKey(
        parentColumns = arrayOf("pexId"),
        childColumns = arrayOf("pexId"),
        onDelete = ForeignKey.CASCADE
    , entity = GalleryShareActivity::class)], indices = [Index(value = arrayOf("pexId"), name = "pocExhibitionPictureIndex", unique = false)]
)
class PocExhibitionPicture : Serializable {
    @PrimaryKey(autoGenerate = true)
    @Expose
    @SerializedName("pepId")
    var pepId: Long? = null
    @Ignore
    var byteShare: ByteArray? = null
    @Expose
    @SerializedName("PicId")
    var picId: String? = null

    @Expose
    @SerializedName("pexId")
    var pexId: Long? = null

    @Expose
    @SerializedName("PicInsDate")
    var picInsDate: Date? = null

    @Expose
    @SerializedName("ComId")
    var comId: Int? = null

    @Expose
    @SerializedName("UsrId")
    var usrId: Int? = null

    @Expose
    @SerializedName("EstId")
    var estId: Int? = null

    @Expose
    @SerializedName("IosId")
    var iosId: Int? = null

    @Expose
    @SerializedName("PicExhibition")
    var picExhibition: String? = null

    @Expose
    @SerializedName("PicSubcategory")
    var picSubCategory: String? = null

    @Expose
    @SerializedName("PicSection")
    var picSection: String? = null

    @Expose
    @SerializedName("PicOwnership")
    var picOwnerShip: String? = null

    @Expose
    @SerializedName("satId")
    var satId: Int? = null

    @Expose
    @SerializedName("PicPartialExhibitionPoint")
    var pepSequence: Int? = null

    @Expose
    @SerializedName("PicFileName")
    var pepFileName: String? = null

    @Expose
    @SerializedName("pepFilePath")
    var pepFilePath: String? = null

    @Expose
    @SerializedName("RstId")
    var rstId: Int? = null

    @Expose
    @SerializedName("QstId")
    var qstId: Int? = null
        get() {
            if (field == null) {
                field = 0
            }
            return field
        }

    @Expose
    @SerializedName("pepFilePathS3")
    var pepFilePathS3: String? = null

    @Expose
    @SerializedName("PepWidth")
    var pepWidth: Int? = null

    @Expose
    @SerializedName("PepHeight")
    var pepHeight: Int? = null

    @Expose
    @SerializedName("markerShareList")
    private var markerShareList: MutableList<MarkerShare>? = null

    @Expose
    @SerializedName("shareAbsoluteOfPoc")
    var shareAbsoluteOfPoc: Double? = null

    @Expose
    @SerializedName("shareRelativeOfPoc")
    var shareRelativeOfPoc: Double? = null

    @Expose
    @SerializedName("shareAbsoluteOfPicture")
    var shareAbsoluteOfPicture: Double? = null

    @Expose
    @SerializedName("shareRelativeOfPicture")
    var shareRelativeOfPicture: Double? = null

    @Expose
    @SerializedName("pictureWithShare")
    var pictureWithShare: Boolean? = null


    constructor(
        pexId: Long?,
        satId: Int?,
        pepSequence: Int?,
        pepFileName: String?,
        pepFilePath: String?
    ) {
        this.pexId = pexId
        this.satId = satId
        this.pepSequence = pepSequence
        this.pepFileName = pepFileName
        this.pepFilePath = pepFilePath
    }

    // Remove um marcador da lista
    fun remove(): MarkerShare? {
        return if (markerShareList!!.isEmpty()) null else markerShareList!!.removeAt(
            markerShareList!!.size - 1
        )
    }

    // Adiciona marker a lista depois de conferir os limitsPixel
    fun add(markerShare: MarkerShare) {
        limitsPixel(PointF(markerShare.initPositionX!!, markerShare.initPositionY!!))
        limitsPixel(PointF(markerShare.endPositionX!!, markerShare.endPositionY!!))
        markerShareList!!.add(markerShare)
    }

    // Tratar limites do pixel, zera marcador caso altura e largura sejam == 0
    private fun limitsPixel(position: PointF) {
        if (position.x < 0) {
            position.x = 0f
        }
        if (position.x > pepWidth!!) {
            position.x = pepWidth!!.toFloat()
        }
        if (position.y < 0) {
            position.y = 0f
        }
        if (position.y > pepHeight!!) {
            position.y = pepHeight!!.toFloat()
        }
    }

    val pixelsClient: Float
        @RequiresApi(Build.VERSION_CODES.N)
        get() {
            var pixelsClient = 0f
            if (markerShareList != null && markerShareList!!.isNotEmpty()) {
                val markerShareList =
                    markerShareList!!.stream()
                        .filter { markerShare: MarkerShare -> markerShare.color != Color.parseColor("#FFFF00")
                                && markerShare.color !=  Color.parseColor("#A5A5A5")  && markerShare.color != Color.parseColor("#FFFFFF") }
                        .collect(Collectors.toList())
                for (markerShare in markerShareList) {
                    pixelsClient += markerShare.calculateDistance()
                }
            }
            return pixelsClient
        }

    val pixelsCompetitor: Float
        @RequiresApi(Build.VERSION_CODES.N)
        get() {
            var pixelsCompetitor = 0f
            if (markerShareList != null && markerShareList!!.isNotEmpty()) {
                val markerShareList =
                    markerShareList!!.stream().filter { markerShare: MarkerShare ->
                            markerShare.color == Color.parseColor("#FFFF00")
                    }.collect(Collectors.toList())
                for (markerShare in markerShareList) {
                    pixelsCompetitor += markerShare.calculateDistance()
                }
            }
            return pixelsCompetitor
        }

    val pixelsEmpty: Float
        @RequiresApi(Build.VERSION_CODES.N)
        get() {
            var pixelsEmpty = 0f
            if (markerShareList != null && markerShareList!!.isNotEmpty()) {
                val markerShareList =
                    markerShareList!!.stream().filter { markerShare: MarkerShare ->
                        markerShare.color == Color.parseColor("#A5A5A5")
                    }.collect(Collectors.toList())
                for (markerShare in markerShareList) {
                    pixelsEmpty += markerShare.calculateDistance()
                }
            }
            return pixelsEmpty
        }

    fun calculateClient() : Float {
        val qtdTotal = pixelsClient + pixelsCompetitor + pixelsEmpty

        return pixelsClient*100/qtdTotal
    }

    fun calculateCompetitor() : Float {
        val qtdTotal = pixelsClient + pixelsCompetitor + pixelsEmpty

        return pixelsCompetitor*100/qtdTotal
    }

    fun calculateEmpty() : Float {
        val qtdTotal = pixelsClient + pixelsCompetitor + pixelsEmpty

        return pixelsEmpty*100/qtdTotal
    }

    override fun toString(): String {
        return """
            PocExhibitionPicture{pepId=$pepId, pexId=$pexId, picInsDate=$picInsDate, comId=$comId, usrId=$usrId, estId=$estId, iosId=$iosId, picExhibition='$picExhibition
            , picSubCategory='$picSubCategory
            , picSection='$picSection
            , picOwnerShip='$picOwnerShip
            , satId=$satId, pepSequence=$pepSequence, pepFileName='$pepFileName
            , pepFilePath='$pepFilePath
            , rstId=$rstId}
            """.trimIndent()
    }

    fun getMarkerShareList(): List<MarkerShare> {
        if (markerShareList == null) {
            markerShareList = ArrayList()
        }
        return markerShareList!!
    }

    fun setMarkerShareList(markerShareList: MutableList<MarkerShare>?) {
        this.markerShareList = markerShareList
    }

}
