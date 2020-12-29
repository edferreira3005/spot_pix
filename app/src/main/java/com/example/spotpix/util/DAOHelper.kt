package br.com.spotpromo.nestle_dpa_novo.regras.model

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import java.util.*

/**
 * Created by Edson Ferreira on 18/10/2018.
 */
open class DAOHelper @Throws(Exception::class)
constructor(val db: SQLiteDatabase) {

    interface Adapter<T : DAOHelper> {
        @Throws(Exception::class)
        fun onRead(dao: T, cursor: Cursor)
    }

    /**
     * Executa query e verifica se tem parametros. Query sem retorno
     * @param query
     * @param param
     */

    fun deletar(query: String, vararg param: Any): Int {

        val command: SQLiteStatement
        var position = 0

        command = db.compileStatement(query)

        if (param.isNotEmpty()) {
            command.clearBindings()
            for (`object` in param) {
                position++
                if (`object` is String) {
                    command.bindString(position, `object`)
                } else if (`object` is Int) {
                    command.bindLong(position, `object`.toLong())
                } else if (`object` is Boolean) {
                    command.bindLong(position, (if (`object`) 1 else 0).toLong())
                } else if (`object` is Double || `object` is Float) {
                    command.bindDouble(position, `object` as Double)
                } else if (`object` is ByteArray) {
                    command.bindBlob(position, `object`)
                } else if (`object` == null) {
                    command.bindNull(position)
                }
            }
        }

        try {
            return command.executeUpdateDelete()
        } finally {
            command.close()
        }
    }

    fun executaNoQuery(query: String, vararg param: Any?) {

        val command: SQLiteStatement
        var position = 0


        command = db.compileStatement(query)

        if (param.size > 0) {
            command.clearBindings()
            for (`object` in param) {
                position++

                if (`object` is String) {
                    command.bindString(position, `object`)
                } else if (`object` is Int) {
                    command.bindLong(position, `object`.toLong())
                } else if (`object` is Boolean) {
                    command.bindLong(position, (if (`object`) 1 else 0).toLong())
                } else if (`object` is Double || `object` is Float) {
                    command.bindDouble(position, `object` as Double)
                } else if (`object` is ByteArray) {
                    command.bindBlob(position, `object`)
                } else if (`object` == null) {
                    command.bindNull(position)
                }
            }
        }

        command.execute()
        command.close()
    }

    /**
     * Executa query e verifica se tem parametros. Query com retorno
     * @param query
     * @param param
     */
    fun executaQuery(query: String, vararg param: Any): Cursor {
        val arrString = ArrayList<String>()
        for (`object` in param) {
            arrString.add(`object`.toString())
        }
        val paramObj = Arrays.copyOf<String, Any>(
            arrString.toTypedArray(),
            arrString.size,
            Array<String>::class.java
        )
        val cursor = db.rawQuery(query, paramObj)
        cursor.moveToFirst()
        return cursor
    }

    fun getString(cursor: Cursor, campo: String): String {
        return cursor.getString(cursor.getColumnIndex(campo))
    }

    fun getInt(cursor: Cursor, campo: String): Int {
        return cursor.getInt(cursor.getColumnIndex(campo))
    }

    fun getDouble(cursor: Cursor, campo: String): Double {
        return cursor.getDouble(cursor.getColumnIndex(campo))
    }

    fun getBlob(cursor: Cursor, campo: String): ByteArray {
        return cursor.getBlob(cursor.getColumnIndex(campo))
    }

    @Throws(Exception::class)
    fun doRead(cursor: Cursor?, adapter: Adapter<DAOHelper>) {
        //Verifica se o cursor não esta nulo
        if (cursor != null) {
            try {
                //Verifica se é um cursor valido
                if (cursor.moveToFirst() && cursor.count > 0) {
                    do {
                        adapter.onRead(this, cursor)
                    } while (cursor.moveToNext()) //Loop
                }
            } finally {
                cursor.close()
            }
        }
    }


}
