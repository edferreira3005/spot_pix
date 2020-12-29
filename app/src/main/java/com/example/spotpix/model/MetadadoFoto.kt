package com.example.spotpix.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MetadadoFoto : Serializable {
    @SerializedName("pepId")
    @Expose
    var pepId: Int? = null
    @SerializedName("picId")
    @Expose
    var picId: String? = null
    @SerializedName("pexId")
    @Expose
    var pexId: Int? = null
    @SerializedName("picInsDate")
    @Expose
    var picInsDate: String? = null
    @SerializedName("comId")
    @Expose
    var comId: Int? = null
    @SerializedName("usrId")
    @Expose
    var usrId: Int? = null
    @SerializedName("estId")
    @Expose
    var estId: Int? = null
    @SerializedName("iosID")
    @Expose
    var iosID: Int? = null
    @SerializedName("picExhibition")
    @Expose
    var picExhibition: String? = null
    @SerializedName("picSubcategory")
    @Expose
    var picSubcategory: String? = null
    @SerializedName("picOwnership")
    @Expose
    var picOwnership: String? = null
    @SerializedName("picPartialExhibition")
    @Expose
    var picPartialExhibition: Int? = null
    @SerializedName("picFileName")
    @Expose
    var picFileName: String? = null
    @SerializedName("rstId")
    @Expose
    var rstId: Int? = null
    @SerializedName("qstId")
    @Expose
    var qstId: Int? = null
    @SerializedName("markerShareList")
    var markerShareList: MutableList<MarkerShare>? = null
}