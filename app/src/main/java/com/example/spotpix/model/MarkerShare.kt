package com.example.spotpix.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class MarkerShare(
    @SerializedName("initPositionX")
    @Expose
    val initPositionX: Float?,
    @SerializedName("initPositionY")
    @Expose
    val initPositionY: Float?,
    @SerializedName("endPositionX")
    @Expose
    val endPositionX: Float?,
    @SerializedName("endPositionY")
    @Expose
    val endPositionY: Float?,
    @SerializedName("invaders")
    @Expose
    val invaders: Boolean?,
    @SerializedName("color")
    @Expose
    val color: Int?,
    @SerializedName("markerType")
    val markerType: String?
) :
    Serializable {

    private fun calculateWheelbaseX(): Float {
        return abs(endPositionX!! - initPositionX!!)
    }

    private fun calculateWheelbaseY(): Float {
        return abs(endPositionY!! - initPositionY!!)
    }

    fun calculateDistance(): Float {
        val axisX: BigDecimal =
            BigDecimal.valueOf(calculateWheelbaseX().toDouble().pow(2.0))
        val axisY: BigDecimal =
            BigDecimal.valueOf(calculateWheelbaseY().toDouble().pow(2.0))
        val summationAxis: BigDecimal = axisX.add(axisY)
        return sqrt(summationAxis.toDouble()).toString().toFloat()
    }

    class Builder {
        private var initPositionX: Float? = null
        private var initPositionY: Float? = null
        private var endPositionX: Float? = null
        private var endPositionY: Float? = null
        private var invaders: Boolean? = null
        private var color: Int? = null
        private var markerType: String? = null
        fun initPositionX(initPositionX: Float?): Builder {
            this.initPositionX = initPositionX
            return this
        }

        fun initPositionY(initPositionY: Float?): Builder {
            this.initPositionY = initPositionY
            return this
        }

        fun endPositionX(endPositionX: Float?): Builder {
            this.endPositionX = endPositionX
            return this
        }

        fun endPositionY(endPositionY: Float?): Builder {
            this.endPositionY = endPositionY
            return this
        }

        fun invaders(invaders: Boolean?): Builder {
            this.invaders = invaders
            return this
        }

        fun color(color: Int?): Builder {
            this.color = color
            return this
        }

        fun markerType(markerType: String?): Builder {
            this.markerType = markerType
            return this
        }

        fun build(): MarkerShare {
            return MarkerShare(
                initPositionX,
                initPositionY,
                endPositionX,
                endPositionY,
                invaders,
                color,
                markerType
            )
        }
    }

}
