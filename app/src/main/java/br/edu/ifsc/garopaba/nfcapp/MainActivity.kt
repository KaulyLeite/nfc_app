package br.edu.ifsc.garopaba.nfcapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var botaoLer: Button
    private lateinit var botaoEscrever: Button
    private lateinit var botaoLimpar: Button
    private lateinit var textoVersao: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        botaoLer = findViewById(R.id.botaoLer)
        botaoEscrever = findViewById(R.id.botaoEscrever)
        botaoLimpar = findViewById(R.id.botaoLimpar)
        textoVersao = findViewById(R.id.textoVersao)

        textoVersao.text = String.format(getString(R.string.texto_versao), BuildConfig.VERSION_NAME)

        botaoLer.setOnClickListener {
            if (Utils.verificarSuporteNfc(this)) {
                startActivity(Intent(this, LerActivity::class.java))
            } else {
                mostrarErroSuporteNfc()
            }
        }

        botaoEscrever.setOnClickListener {
            if (Utils.verificarSuporteNfc(this)) {
                startActivity(Intent(this, EscreverActivity::class.java))
            } else {
                mostrarErroSuporteNfc()
            }
        }

        botaoLimpar.setOnClickListener {
            if (Utils.verificarSuporteNfc(this)) {
                startActivity(Intent(this, LimparActivity::class.java))
            } else {
                mostrarErroSuporteNfc()
            }
        }
    }

    private fun mostrarErroSuporteNfc() {
        Toast.makeText(this, getString(R.string.sem_suporte), Toast.LENGTH_SHORT).show()
    }
}
