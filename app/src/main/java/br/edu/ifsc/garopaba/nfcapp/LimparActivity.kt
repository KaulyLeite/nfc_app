package br.edu.ifsc.garopaba.nfcapp

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast

class LimparActivity : AppCompatActivity(), NfcAdapter.ReaderCallback, IModoLeitor {

    private lateinit var textoTitulo: TextView
    private lateinit var textoSubtitulo: TextView
    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_limpar)

        textoTitulo = findViewById(R.id.textoTitulo)
        textoSubtitulo = findViewById(R.id.textoSubtitulo)

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
        val ndef = Ndef.get(tag)
        val ndefMessage = ndef?.cachedNdefMessage

        runOnUiThread {
            if (ndefMessage != null && ndefMessage.records.isNotEmpty()) {
                limparConteudoTag(ndef)
            } else {
                mostrarErroLimpeza()
            }
        }
    }

    private fun limparConteudoTag(ndef: Ndef) {
        try {
            ndef.connect()

            val ndefMessage = ndef.ndefMessage
            val ndefRecords = ndefMessage?.records ?: emptyArray()

            if (ndefRecords.isEmpty() || (ndefRecords.size == 1 && ndefRecords[0].payload.isEmpty())) {
                runOnUiThread {
                    textoTitulo.text = getString(R.string.titulo_limpar)
                    textoSubtitulo.text = ""
                    Toast.makeText(this, getString(R.string.sem_conteudo), Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                val emptyPayload = ByteArray(0)
                val emptyRecord = NdefRecord(NdefRecord.TNF_EMPTY, null, null, emptyPayload)
                val emptyMessage = NdefMessage(arrayOf(emptyRecord))

                ndef.writeNdefMessage(emptyMessage)
                ndef.close()

                runOnUiThread {
                    textoTitulo.text = getString(R.string.titulo_resultado)
                    textoSubtitulo.text = getString(R.string.sucesso_limpar)
                    textoSubtitulo.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            runOnUiThread {
                mostrarErroLimpeza()
            }
        }
    }

    private fun mostrarErroLimpeza() {
        runOnUiThread {
            textoTitulo.text = getString(R.string.titulo_resultado)
            textoSubtitulo.text = getString(R.string.erro_limpar)
            textoSubtitulo.visibility = View.VISIBLE
        }
    }
}
