package br.edu.ifsc.garopaba.nfcapp

import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class EscreverActivity : AppCompatActivity(), NfcAdapter.ReaderCallback, IModoLeitor {

    private lateinit var textoTitulo: TextView
    private lateinit var textoSubtitulo: TextView
    private lateinit var campoTag: EditText
    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escrever)

        textoTitulo = findViewById(R.id.textoTitulo)
        textoSubtitulo = findViewById(R.id.textoSubtitulo)
        campoTag = findViewById(R.id.campoTag)

        if (!Utils.verificarAtivacaoNfc(this)) {
            Utils.abrirConfiguracaoNfc(this)
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onResume() {
        super.onResume()
        habilitarModoLeitor()
    }

    override fun onPause() {
        super.onPause()
        desabilitarModoLeitor()
    }

    override fun habilitarModoLeitor() {
        val flags = NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B
        val extras = Bundle()
        nfcAdapter.enableReaderMode(this, this, flags, extras)
    }

    override fun desabilitarModoLeitor() {
        nfcAdapter.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag) {
        val content = campoTag.text.toString()

        if (content.isNotEmpty()) {
            val ndefRecord = criarEscritaTexto(content)
            val ndefMessage = NdefMessage(arrayOf(ndefRecord))
            val ndef = Ndef.get(tag)
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            runOnUiThread {
                try {
                    if (ndef != null) {
                        ndef.connect()
                        ndef.writeNdefMessage(ndefMessage)
                        ndef.close()

                        textoTitulo.text = getString(R.string.titulo_resultado)
                        textoSubtitulo.text = getString(R.string.sucesso_escrever)
                        textoSubtitulo.visibility = View.VISIBLE
                        campoTag.visibility = View.INVISIBLE

                        inputMethodManager.hideSoftInputFromWindow(campoTag.windowToken, 0)
                    } else {
                        Toast.makeText(this, getString(R.string.erro_ler), Toast.LENGTH_SHORT)
                            .show()
                        inputMethodManager.hideSoftInputFromWindow(campoTag.windowToken, 0)
                    }
                } catch (e: Exception) {
                    mostrarErroEscrita()
                }
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, getString(R.string.erro_digitar), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun criarEscritaTexto(content: String): NdefRecord {
        val languageCode = "en"
        val textEncoding = Charsets.UTF_8
        val textPayload = content.toByteArray(textEncoding)
        val payload = ByteArray(1 + textPayload.size)

        payload[0] = languageCode.length.toByte()
        System.arraycopy(textPayload, 0, payload, 1, textPayload.size)

        return NdefRecord(
            NdefRecord.TNF_WELL_KNOWN,
            NdefRecord.RTD_TEXT,
            ByteArray(0),
            payload
        )
    }

    private fun mostrarErroEscrita() {
        runOnUiThread {
            textoTitulo.text = getString(R.string.titulo_resultado)
            textoSubtitulo.text = getString(R.string.erro_escrever)
            textoSubtitulo.visibility = View.VISIBLE
        }
    }
}
