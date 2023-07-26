package br.edu.ifsc.garopaba.nfcapp

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings

object Utils {

    fun verificarSuporteNfc(context: Context): Boolean {
        val adapter = NfcAdapter.getDefaultAdapter(context)
        return adapter != null
    }

    fun verificarAtivacaoNfc(context: Context): Boolean {
        val adapter = NfcAdapter.getDefaultAdapter(context)
        return adapter?.isEnabled == true
    }

    fun abrirConfiguracaoNfc(context: Context) {
        val intent = Intent(Settings.ACTION_NFC_SETTINGS)
        context.startActivity(intent)
    }
}
