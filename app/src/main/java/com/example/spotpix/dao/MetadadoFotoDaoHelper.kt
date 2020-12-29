package com.example.spotpix.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.spotpromo.nestle_dpa_novo.regras.model.DAOHelper
import com.example.spotpix.model.MarkerShare
import com.example.spotpix.util.Util
import com.example.spotpix.model.MetadadoFoto
import com.example.spotpix.model.PocExhibitionPicture
import java.text.SimpleDateFormat
import java.util.ArrayList

class MetadadoFotoDaoHelper @Throws(Exception::class)
constructor(db: SQLiteDatabase) : DAOHelper(db) {


    @Throws(Exception::class)
    fun selectInfoMetadado(nomeFoto: String, codPesquisa: Int, codCampanha: Int, dataFoto: String, guid: String): MetadadoFoto? {
        val sbQuery = StringBuilder()
        sbQuery.append("SELECT DISTINCT " +
                "             a.codColetaShelfPix as pepId," +
                "             b.codRoteiro || '" + codCampanha + "' || '" + guid + "' as picId," +
                "             a.id as pexId," +
                "             '$codCampanha' as comId," +
                "             b.codPessoa as usrId," +
                "             b.codLoja as estId," +
                "             b.codRoteiro as iosID," +
                "             (SELECT DISTINCT desExposicao from TB_Vinculo_ShelfPix where codExposicao = c.codExposicao) as picExhibition," +
                "             (SELECT distinct desCategoria from TB_Produto where codCategoria = d.codSubCategoria) as picSubcategory," +
                "             (SELECT DISTINCT desTipoExposicao from TB_Vinculo_ShelfPix where codTipoExposicao = c.codTipoExposicao) as picOwnership" +
                "       FROM " +
                "          TB_Coleta_ShelfPix_Foto a INNER JOIN " +
                "          TB_Coleta_ShelfPix c on c.id = a.codColetaShelfPix and a.codPesquisa = c.codPesquisa INNER JOIN " +
                "          TB_Pesquisa b on b.id = a.codPesquisa and b.id = c.codPesquisa INNER JOIN" +
                "          TB_Coleta_ShelfPix_SubCategoria d on d.codPesquisa = b.id and c.id = d.codColetaShelfPix " +
                "       WHERE " +
                "           b.id = $codPesquisa and a.nomFoto like '$nomeFoto%'")

        val cursor = this.executaQuery(sbQuery.toString())
        val mMetadadoFoto = MetadadoFoto()
        if (cursor.moveToFirst()) {
            do {
                mMetadadoFoto.pepId = cursor.getInt(cursor.getColumnIndex("pepId"))
                mMetadadoFoto.picId = cursor.getString(cursor.getColumnIndex("picId"))
                mMetadadoFoto.pexId = cursor.getInt(cursor.getColumnIndex("pexId"))
                mMetadadoFoto.picInsDate = dataFoto
                mMetadadoFoto.comId = cursor.getInt(cursor.getColumnIndex("comId"))
                mMetadadoFoto.usrId = cursor.getInt(cursor.getColumnIndex("usrId"))
                mMetadadoFoto.estId = cursor.getInt(cursor.getColumnIndex("estId"))
                mMetadadoFoto.iosID = cursor.getInt(cursor.getColumnIndex("iosID"))
                mMetadadoFoto.picExhibition = cursor.getString(cursor.getColumnIndex("picExhibition"))
                mMetadadoFoto.picSubcategory = cursor.getString(cursor.getColumnIndex("picSubcategory"))
                mMetadadoFoto.picOwnership = cursor.getString(cursor.getColumnIndex("picOwnership"))
                mMetadadoFoto.picPartialExhibition = getPhotoPosition(codPesquisa, mMetadadoFoto.pepId!!, nomeFoto)
                mMetadadoFoto.picFileName = Util.retonarNomeFoto(nomeFoto)
                mMetadadoFoto.rstId = 1
                mMetadadoFoto.qstId = -1
                mMetadadoFoto.markerShareList = selectPosicoesShare(mMetadadoFoto.pexId!!.toLong(), codPesquisa)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return mMetadadoFoto
    }

    @Throws(Exception::class)
    fun selectInfoMetadadoNew(nomeFoto: String, codPesquisa: Int, codCampanha: Int, dataFoto: String, guid: String): PocExhibitionPicture? {
        val sbQuery = StringBuilder()
        sbQuery.append("SELECT DISTINCT " +
                "             a.codColetaShelfPix as pepId," +
                "             b.codRoteiro || '" + codCampanha + "' || '" + guid + "' as picId," +
                "             a.id as pexId," +
                "             '$codCampanha' as comId," +
                "             b.codPessoa as usrId," +
                "             b.codLoja as estId," +
                "             b.codRoteiro as iosID," +
                "             (SELECT DISTINCT desExposicao from TB_Vinculo_ShelfPix where codExposicao = c.codExposicao) as picExhibition," +
                "             (SELECT distinct desCategoria from TB_Produto where codCategoria = d.codSubCategoria) as picSubcategory," +
                "             (SELECT DISTINCT desTipoExposicao from TB_Vinculo_ShelfPix where codTipoExposicao = c.codTipoExposicao) as picOwnership" +
                "       FROM " +
                "          TB_Coleta_ShelfPix_Foto a INNER JOIN " +
                "          TB_Coleta_ShelfPix c on c.id = a.codColetaShelfPix and a.codPesquisa = c.codPesquisa INNER JOIN " +
                "          TB_Pesquisa b on b.id = a.codPesquisa and b.id = c.codPesquisa INNER JOIN" +
                "          TB_Coleta_ShelfPix_SubCategoria d on d.codPesquisa = b.id and c.id = d.codColetaShelfPix " +
                "       WHERE " +
                "           b.id = $codPesquisa and a.nomFoto like '$nomeFoto%'")

        val cursor = this.executaQuery(sbQuery.toString())

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var mMetadadoFoto : PocExhibitionPicture? = null
        if (cursor.moveToFirst()) {
            do {
                val mMetadadoFoto1 = PocExhibitionPicture(cursor.getLong(cursor.getColumnIndex("pexId")),
                    cursor.getInt(cursor.getColumnIndex("comId")),
                    cursor.getInt(cursor.getColumnIndex("pepId")),Util.retonarNomeFoto(nomeFoto) ,
                    nomeFoto
                )
                mMetadadoFoto1.pepId = cursor.getLong(cursor.getColumnIndex("pepId"))
                mMetadadoFoto1.picId = cursor.getString(cursor.getColumnIndex("picId"))
                mMetadadoFoto1.pexId = cursor.getLong(cursor.getColumnIndex("pexId"))
                mMetadadoFoto1.picInsDate = formatter.parse(dataFoto)
                mMetadadoFoto1.comId = cursor.getInt(cursor.getColumnIndex("comId"))
                mMetadadoFoto1.usrId = cursor.getInt(cursor.getColumnIndex("usrId"))
                mMetadadoFoto1.estId = cursor.getInt(cursor.getColumnIndex("estId"))
                mMetadadoFoto1.picExhibition = cursor.getString(cursor.getColumnIndex("picExhibition"))
                mMetadadoFoto1.rstId = 1
                mMetadadoFoto1.qstId = -1
                mMetadadoFoto1.setMarkerShareList(selectPosicoesShare(mMetadadoFoto1.pexId!!, codPesquisa))

                mMetadadoFoto = mMetadadoFoto1
            } while (cursor.moveToNext())
        }

        cursor.close()
        return mMetadadoFoto
    }

    private fun selectPosicoesShare(pexId: Long, codPesquisa: Int): MutableList<MarkerShare> {
        val cursor: Cursor?

        val sbQuery = StringBuilder()
        val lista: ArrayList<MarkerShare> = ArrayList()

        sbQuery.append( " SELECT * FROM TB_ShelfPix_Share WHERE codPesquisa = ? and codColetaFotoShelfPix = ? " )

        cursor = this.executaQuery(sbQuery.toString(), codPesquisa, pexId)
        if (cursor.moveToFirst()) {
            do {
                val markerShare = MarkerShare(
                    cursor.getFloat(cursor.getColumnIndex("initPositionX")),
                    cursor.getFloat(cursor.getColumnIndex("initPositionY")),
                    cursor.getFloat(cursor.getColumnIndex("endPositionX")),
                    cursor.getFloat(cursor.getColumnIndex("endPositionY")),
                    cursor.getInt(cursor.getColumnIndex("invaders")) == 1,
                    cursor.getInt(cursor.getColumnIndex("color")),
                    ""
                )

                lista.add(markerShare)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }


    private fun getPhotoPosition(codPesquisa: Int, codColetaShelfPix: Int, photo: String): Int? {
        var position = 0
        val cursor: Cursor?

        val sbQuery = StringBuilder()

        sbQuery.append( " SELECT nomFoto FROM TB_Coleta_ShelfPix_Foto WHERE codPesquisa = ? and codColetaShelfPix = ? order by id " )

        cursor = this.executaQuery(sbQuery.toString(), codPesquisa, codColetaShelfPix)
        if (cursor.moveToFirst()) {
            do {
                if (photo.trim() == cursor.getString(cursor.getColumnIndex("nomFoto")).trim()) {
                    position = cursor.position
                    break
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        return position
    }

    fun insertPositions(markerShareList: List<MarkerShare>, codColetaFotoShelfPix: Int, codPesquisa: Int) {
        deletar(codColetaFotoShelfPix, codPesquisa)

        for(posicoes in markerShareList) {
            val sbQuery = StringBuilder()
            sbQuery.append("INSERT INTO TB_ShelfPix_Share (codPesquisa, codColetaFotoShelfPix, initPositionX, initPositionY, endPositionX, endPositionY, invaders, color) " + "VALUES(?,?,?,?,?,?,?,?)")

            this.executaNoQuery(
                sbQuery.toString(),
                codPesquisa,
                codColetaFotoShelfPix,
                posicoes.initPositionX!!.toDouble(),
                posicoes.initPositionY!!.toDouble(),
                posicoes.endPositionX!!.toDouble(),
                posicoes.endPositionY!!.toDouble(),
                if(posicoes.invaders!!) 1 else 0,
                posicoes.color!!
            )
        }
    }

    @Throws(Exception::class)
    fun deletar(codColetaFotoShelfPix: Int, codPesquisa: Int) {
        val sbQuery = StringBuilder()
        sbQuery.append("DELETE FROM TB_ShelfPix_Share WHERE codPesquisa = ? and codColetaFotoShelfPix = ?")

        this.executaNoQuery(
            sbQuery.toString(), codPesquisa, codColetaFotoShelfPix
        )
    }
}