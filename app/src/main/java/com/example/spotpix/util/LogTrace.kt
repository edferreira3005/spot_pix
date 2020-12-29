package com.example.spotpix.util

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.spotpix.R
import java.io.FileNotFoundException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


object LogTrace {

    /**
     * Trata o erro, printa o erro no terminal e não exibe alerta
     * @param context
     * @param classe
     * @param err
     * @return
     */
    fun logCatch(context: Context, classe: Class<*>, err: Throwable): String {
        return LogTrace.logCatch(context, classe, err, false)
    }

    /**
     * Trata o erro, printa o erro no terminal e exibe alerta amigavel
     * @param context
     * @param classe
     * @param err
     * @param exibeMensagem
     * @return
     */
    fun logCatch(context: Context, classe: Class<*>, err: Throwable, exibeMensagem: Boolean): String {
        val sb = StringBuilder()
        var erro: String

        //Verifica qual é a extensão da Exception
        if (err is NetworkErrorException || err is UnknownHostException)
            erro = context.resources.getString(R.string.erro_conexao)
        else if (err is SocketTimeoutException || err is SocketException)
            erro = context.resources.getString(R.string.erro_servidor_expirado)
        else if (err is FileNotFoundException)
            erro = context.resources.getString(R.string.erro_arquivo_nao_encontrado)
        else if (err is SQLiteException)
            erro = context.resources.getString(R.string.erro_erro_banco)
        else if (err is NumberFormatException)
            erro = context.resources.getString(R.string.erro_campo_formatacao)
        else
            erro = if (err.message == null) context.resources.getString(R.string.erro_nao_identificado) else err.message!!


        //Monta mensagem de erro
        //sb.append(err.getClass().getSimpleName());
        //sb.append(" - ");
        sb.append(erro)
        sb.append("!!!")
        erro = sb.toString()

        err.printStackTrace()
        Log.e(classe.name, erro)

        if (exibeMensagem) {
            Alerta.show(context, "Erro", erro, "OK",
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }, false
            )
        }

        return erro
    }


    /**
     * Percorre a exception e retorna uma string com as informações de erro encontradas
     * @param err
     * @return
     */
    fun getAllException(err: Throwable): String {
        return LogTrace.getAllException(err, "")
    }

    fun getAllException(err: Throwable, moreMessage: String): String {
        try {
            var arr = err.stackTrace
            val report = StringBuilder()

            report.append(err.toString())
            report.append("<br/><br/>")

            report.append("--------- Stack trace ---------")
            report.append("<br/><br/>")
            for (elem in arr) {
                report.append(String.format("    %1\$s", elem.toString()))
                report.append("<br/>")
            }
            report.append("-------------------------------")
            report.append("<br/><br/>")

            // If the exception was thrown in a background thread inside
            // AsyncTask, then the actual exception can be found with getCause
            val cause = err.cause
            if (cause != null) {
                report.append("--------- Cause ---------")
                report.append("<br/><br/>")
                report.append(cause.toString())
                report.append("<br/><br/>")
                arr = cause.stackTrace
                for (elem in arr) {
                    report.append(String.format("    %1\$s", elem.toString()))
                    report.append("<br/>")
                }
                report.append("-------------------------------")
                report.append("<br/><br/>")
            }

            // Adiciona mais alguma mensagem
            if (!moreMessage.isEmpty()) {
                report.append("--------- More Mensage ---------")
                report.append("<br/><br/>")
                report.append(moreMessage)
                report.append("-------------------------------")
                report.append("<br/><br/>")
            }

            return report.toString()
        } catch (e: Exception) {
            return e.toString()
        }

    }
}