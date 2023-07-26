package br.edu.ifsc.garopaba.nfcapp

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast

class LerActivity : AppCompatActivity(), NfcAdapter.ReaderCallback, IModoLeitor {

    private lateinit var textoTitulo: TextView
    private lateinit var textoTag: TextView
    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ler)

        textoTitulo = findViewById(R.id.textoTitulo)
        textoTag = findViewById(R.id.textoTag)

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
                val payload = ndefMessage.records[0].payload
                val conteudoTag = String(payload)

                if (conteudoTag.isNotBlank() || conteudoTag.isNotEmpty()) {
                    textoTitulo.text = getString(R.string.titulo_resultado)
                    textoTag.text = conteudoTag
                    textoTag.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this, getString(R.string.sem_conteudo), Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, getString(R.string.erro_ler), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
