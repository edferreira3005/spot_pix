package com.example.spotpix.util


import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.spotpix.R

import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader


/**
 * Created by Eloi Brito de Jesus on 17/06/2017.
 */

class SqliteDataBaseHelper(private val mContext: Context, name: String, version: Int) :
    SQLiteOpenHelper(mContext, name, null, version) {

    init {
        mNomeDB = name
        val sdcard = mContext.getExternalFilesDir(null)
    }

    override fun onCreate(db: SQLiteDatabase) {
        //Executa leitura e executação do arquivo
        this.executaLeitura(db, FILECREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //Executa leitura e executação do arquivo
        this.executaLeitura(db, FILEDROP)

        //Re-cria o banco
        this.onCreate(db)
    }

    /**
     * Leitura e execução das querys em determinado arquivo
     *
     * @param db
     * @param file
     */
    private fun executaLeitura(db: SQLiteDatabase, file: String) {
        val reader: BufferedReader
        val inputStream: InputStream
        var linhaQuery: String
        val fileId: Int

        try {

            //Obtem ID do arquivo
            fileId = mContext.resources.getIdentifier(file, RES, mContext.packageName)

            //Verifica se o ID existe
            if (fileId > 0) {

                //Obtem input stream do arquivo @FILETABLES no res @RAW
                inputStream = mContext.resources.openRawResource(fileId)

                //Verifica se tem dados no inputStream
                if (inputStream.available() > 0) {

                    //Inicializa BufferReader com todos os dados do inputStream
                    reader = BufferedReader(InputStreamReader(inputStream))

                    //Inicializa transaction
                    db.beginTransaction()

                    //Percorre linha a linha
                    do {
                        linhaQuery = reader.readLine()
                        linhaQuery = linhaQuery.trim { it <= ' ' }
                        if (!linhaQuery.isEmpty() && !linhaQuery.startsWith("--"))
                            db.execSQL(linhaQuery)
                    } while (reader.readLine() != null)

                    //Fecha reader
                    reader.close()

                    //Finaliza transaction
                    db.setTransactionSuccessful()
                }
            }
        } catch (err: Exception) {
            LogTrace.logCatch(mContext, this.javaClass, err, true)
        } finally {

            //Finaliza transaction
            if (db.inTransaction())
                db.endTransaction()
        }
    }

    companion object {


        val PREFS = "PREFS_DB"
        val PREFS_VERSAO = "PREFS_DB_VERSAO"

        val RES = "raw"
        val FILECREATE = "create"
        val FILEDROP = "drop"
        var mNomeDB = ""

        val FILE_DIRETORY = "/data/data/br.com.spotpromo.nestle_dpa_novo/databases/"

        /**
         * Recebi versao do banco
         *
         * @param context
         * @return
         */
        fun getVersao(context: Context): Int {
            val sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            return sharedPreferences.getInt(PREFS_VERSAO, 1)
        }

        /**
         * Metodo que cria o banco de dados
         *
         * @param context
         * @param versao
         * @return
         */
        @Throws(Exception::class)
        fun criaDB(context: Context, nome: String, versao: Int) {
            val sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor

            editor = sharedPreferences.edit()
            editor.putInt(PREFS_VERSAO, versao)
            editor.apply()

            val db = openDB(context, nome)
            closeDB(db)
        }


        /**
         * Abre banco de dados
         *
         * @param context
         * @return
         */
        @Throws(Exception::class)
        fun openDB(context: Context): SQLiteDatabase {
            return openDB(context, mNomeDB)
        }

        /**
         * Abre banco de dados
         *
         * @param context
         * @return
         */
        @Throws(Exception::class)
        fun openDB(context: Context, nome: String): SQLiteDatabase {
            val versao = getVersao(context)
            val db: SqliteDataBaseHelper
            db = if(nome.isEmpty()) SqliteDataBaseHelper(context, context.getString(R.string.db_name), versao)
            else SqliteDataBaseHelper(context, nome, versao)
            return db.writableDatabase
        }

        /**
         * Fecha banco de dados
         *
         * @param db
         */
        fun closeDB(db: SQLiteDatabase?) {
            if (db != null && db.isOpen) {

                if (db.inTransaction())
                    db.endTransaction()

                db.close()
            }
        }


        fun retornaDB(): File {
            return File(mNomeDB)
        }

        fun retornaDB_2(): File {
            return File("$FILE_DIRETORY/$mNomeDB")
        }
    }

}
