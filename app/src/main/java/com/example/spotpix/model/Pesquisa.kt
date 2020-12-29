package com.example.spotpix.model

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat

class Pesquisa : Serializable {

    val PREFS_PESQUISA = "prefs_pesquisa"
    val PREFS_PESQUISA_DADOS = "prefs_dados_pesquisa"

    var id: Int? = null
    var codRoteiro: Int? = null
    var codPessoa: Int? = null
    var codLoja: Int? = null
    var codRota: Int? = null
    var codBu: Int? = null
    var data: String? = null
    var codJustificativa: Int? = null
    var desJustificativa: String? = null
    var foto: String? = null
    var flFotoEnviada: Int? = null
    var flPesquisaEnviada: Int? = null

    var dataHoraEnvio: String? = null
    var nversao: String? = null

    /**
     * RETORAR LOGIN GRAVADO
     */

    fun retornar(context: Context): Pesquisa? {
        val prefs = context.getSharedPreferences(PREFS_PESQUISA, Context.MODE_PRIVATE)

        val json = prefs.getString(PREFS_PESQUISA_DADOS, "")
        val pesquisa = Gson().fromJson(json, Pesquisa::class.java)

        return pesquisa
    }
}