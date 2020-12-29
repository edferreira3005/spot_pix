package com.example.spotpix.view

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.spotpix.util.SqliteDataBaseHelper
import com.bumptech.glide.Glide
import com.example.spotpix.R
import com.example.spotpix.dao.MetadadoFotoDaoHelper
import com.example.spotpix.databinding.ActivityGalleryShareBinding
import com.example.spotpix.model.Pesquisa
import com.example.spotpix.model.PocExhibitionPicture
import com.example.spotpix.service.TakePictureService
import com.example.spotpix.util.Alerta
import com.example.spotpix.viewmodel.ShareImageViewModel
import kotlinx.android.synthetic.main.activity_gallery_share.*


class GalleryShareActivity : AppCompatActivity() {
    private var shareImageViewModel: ShareImageViewModel? = null
    private var position = 0
    private var codColetaFotoShelfPix = 0
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpFoto()
        setFunctionShare()
    }

    private fun setUpFoto() {
        val binding: ActivityGalleryShareBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_gallery_share)

        val txtClient = findViewById<TextView>(R.id.txtClient)
        val imagem = intent.extras!!.getString("foto")!!
        position = intent.extras!!.getInt("posicao")
        codColetaFotoShelfPix = intent.extras!!.getInt("codColetaFotoShelfPix")
        shareImage.setPocExhibitionPicture(imagem, Pesquisa().retornar(this)!!.id!!)

        Glide.with(this)
            .asBitmap()
            .load(imagem)
            .into(shareImage)

        txtClient?.text = "DPA"
        shareImageViewModel = ShareImageViewModel()
        binding.shareViewModel = shareImageViewModel
        binding.lifecycleOwner = this
    }

    fun setFunctionShare() {
        shareImageViewModel!!.isSaveShare.observe(this) { aBoolean ->
            val pocExhibitionPicture: PocExhibitionPicture = shareImage!!.shareImage!!
            pocExhibitionPicture.pictureWithShare = java.lang.Boolean.TRUE


            val takePictureService = TakePictureService()
            val request: TakePictureService.Request = takePictureService.Request()
            request.context = (this)
            request.bytes = pocExhibitionPicture.byteShare
            request.pocExhibitionPicture = pocExhibitionPicture
            val result: TakePictureService.Result = takePictureService.execute(request, this, Pesquisa().retornar(this)!!.codRoteiro!!)
            if (result?.exception != null) {
                Toast.makeText(this, "Erro ao salvar foto!", Toast.LENGTH_SHORT).show()
            }

            val share = "Porcentagem de Share\nDPA: ${String.format("%.1f", pocExhibitionPicture.calculateClient())}%" +
                    "\nConcorrente: ${String.format("%.1f", pocExhibitionPicture.calculateCompetitor())}%" +
                    "\nVazio: ${String.format("%.1f", pocExhibitionPicture.calculateEmpty())}%"

            MetadadoFotoDaoHelper(SqliteDataBaseHelper.openDB(this)).insertPositions(pocExhibitionPicture.getMarkerShareList(), codColetaFotoShelfPix,
            Pesquisa().retornar(this)!!.id!!)

            Alerta.showT(this, getString(R.string.share), share, getString(R.string.btn_ok),
                { dialog, which ->
                    dialog.dismiss()
                    finish()
                }, false)
        }

        shareImageViewModel!!.isUndo.observe(this, { shareImage!!.undoMarker() })
        shareImageViewModel!!.isClient.observe(
            this
        ) {
            shareImage!!.setColorPaint(
                ContextCompat.getColor(
                    this,
                    R.color.projeto_3
                ), java.lang.Boolean.FALSE
            )
        }
        shareImageViewModel!!.isCompetitor.observe(
            this
        ) {
            shareImage!!.setColorPaint(
                ContextCompat.getColor(this,R.color.amarelo),
                java.lang.Boolean.FALSE
            )
        }
        shareImageViewModel!!.isEmpty.observe(
            this
        ) {
            shareImage!!.setColorPaint(
                ContextCompat.getColor(this,R.color.cinza),
                java.lang.Boolean.FALSE
            )
        }
        shareImageViewModel!!.isInvaders.observe(
            this
        ) {
            shareImage!!.setColorPaint(
                ContextCompat.getColor(
                    this,
                    R.color.branco
                ), java.lang.Boolean.TRUE
            )
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
