package com.example.spotpix.customviews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import com.example.spotpix.util.SqliteDataBaseHelper
import com.example.spotpix.R
import com.example.spotpix.dao.MetadadoFotoDaoHelper
import com.example.spotpix.model.MarkerShare
import com.example.spotpix.model.PocExhibitionPicture
import com.example.spotpix.util.Util
import java.io.ByteArrayOutputStream
import java.util.function.Consumer


class ShareImageView(
    context: Context?,
    attrs: AttributeSet?
) :
    AppCompatImageView(context!!, attrs) {
    private var pocExhibitionPicture: PocExhibitionPicture? = null
        private set
    private var paint: Paint? = null
    private var colorPaint: Int? = null
    private var initPosition: PointF? = null
    private var endPosition: PointF? = null
    private var widthx = 0
    private var heightx = 0
    private var countMarker = 0
    private var isDraw = false
    private var isInvaders = false
    private var markerShare: MarkerShare.Builder? =
        null

    private fun createPaint() {
        if (colorPaint != null) {
            paint = Paint()
            paint!!.color = colorPaint!!
            if (!isInvaders) {
                paint!!.isAntiAlias = true
                paint!!.isDither = true
                paint!!.strokeJoin = Paint.Join.ROUND
                paint!!.style = Paint.Style.STROKE
                paint!!.strokeWidth = 7f
            }
        }
    }

    private fun createPaint(colorPaint: Int?, isInvaders: Boolean): Paint {
        val paint = Paint()
        paint.color = colorPaint!!
        if (!isInvaders) {
            paint.isAntiAlias = true
            paint.isDither = true
            paint.strokeJoin = Paint.Join.ROUND
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 7f
        }
        return paint
    }

    private fun createMarkerShare() {
        markerShare =
            MarkerShare.Builder()
    }

    private fun insertMarkerShare(x: Float, y: Float) {
        if (pocExhibitionPicture != null) {
            val markerShare = markerShare!!
                .endPositionX(x)
                .endPositionY(y)
                .color(paint!!.color)
                .build()
            pocExhibitionPicture!!.add(markerShare)
        }
    }

    fun undoMarker() {
        if (pocExhibitionPicture!!.getMarkerShareList().size > countMarker) pocExhibitionPicture!!.remove()
        invalidate()
    }

    fun setColorPaint(color: Int?, invaders: Boolean) {
        colorPaint = color
        isInvaders = invaders
    }

    val shareImage: PocExhibitionPicture?
        @RequiresApi(Build.VERSION_CODES.N)
        get() {
            try {
                val bitmap = (drawable as BitmapDrawable).bitmap
                val canvas = Canvas(bitmap)
                drawPicture(canvas, java.lang.Boolean.TRUE)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val bytes = stream.toByteArray()
                pocExhibitionPicture!!.byteShare = bytes
                setImageDrawable(null)
                colorPaint = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return pocExhibitionPicture
        }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (pocExhibitionPicture != null) {
            pocExhibitionPicture!!.pepWidth = width
            pocExhibitionPicture!!.pepHeight = height
            drawPicture(canvas, java.lang.Boolean.FALSE)
            if (isDraw && endPosition != null) {
                canvas.drawLine(
                    initPosition!!.x, initPosition!!.y, endPosition!!.x, endPosition!!.y,
                    paint!!
                )
            } else if (isDraw && isInvaders && endPosition != null) {
                canvas.drawRect(
                    initPosition!!.x, initPosition!!.y, endPosition!!.x, endPosition!!.y,
                    paint!!
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun drawPicture(canvas: Canvas, save: Boolean) {
        if (pocExhibitionPicture != null) {
            pocExhibitionPicture!!.getMarkerShareList()
                .forEach(Consumer { marker: MarkerShare ->
                    val init = PointF(marker.initPositionX!!, marker.initPositionY!!)
                    val end = PointF(marker.endPositionX!!, marker.endPositionY!!)
                    if (!save) {
                        if (marker.invaders!!) {
                            canvas.drawRect(
                                init.x,
                                init.y,
                                end.x,
                                end.y,
                                createPaint(marker.color, marker.invaders)
                            )
                        } else {
                            canvas.drawLine(
                                init.x,
                                init.y,
                                end.x,
                                end.y,
                                createPaint(marker.color, marker.invaders)
                            )
                        }
                    } else {
                        if (marker.invaders!!) {
                            canvas.drawRect(
                                init.x,
                                init.y,
                                end.x,
                                end.y,
                                createPaint(marker.color, marker.invaders)
                            )
                        }
                    }
                })
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (colorPaint != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStart(event.x, event.y)
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    touchMove(event.x, event.y)
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    touchUp(event.x, event.y)
                    invalidate()
                }
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(pocExhibitionPicture != null) {
            pocExhibitionPicture!!.pepHeight = h
            pocExhibitionPicture!!.pepWidth = w
        }
        heightx = h
        widthx = w
    }

    private fun touchStart(x: Float, y: Float) {
        isDraw = java.lang.Boolean.TRUE
        createPaint()
        createMarkerShare()
        initPosition = PointF(x, y)
        endPosition = null
        markerShare!!.invaders(isInvaders)
        markerShare!!.initPositionX(x)
        markerShare!!.initPositionY(y)
    }

    private fun touchMove(x: Float, y: Float) {
        if (isDraw && !isInvaders) {
            endPosition = PointF(x, y)
        }
    }

    private fun touchUp(x: Float, y: Float) {
        if (endPosition == null) {
            endPosition = PointF()
        }
        endPosition = PointF(x, y)
        isDraw = java.lang.Boolean.FALSE
        insertMarkerShare(x, y)
    }

    fun setPocExhibitionPicture(foto: String, codPesquisa: Int) {
        try {
            val metadadoFotoDaoHelper = MetadadoFotoDaoHelper(SqliteDataBaseHelper.openDB(context))

            val picture: PocExhibitionPicture = metadadoFotoDaoHelper
                .selectInfoMetadadoNew(
                    foto,
                    codPesquisa,
                    context.resources.getInteger(R.integer.cod_campanha_shelfpix),
                    Util.retornaDataFotoShelfPix(foto),
                    Util.retonarNomeFotoSemExtensao(foto)
                )!!

            countMarker = picture.getMarkerShareList().size
            this.pocExhibitionPicture = picture

        } catch (e: Exception) {
            Log.e("erro imagem", e.toString())
            this.pocExhibitionPicture = null
        }
    }

    init {
        createMarkerShare()
    }
}
