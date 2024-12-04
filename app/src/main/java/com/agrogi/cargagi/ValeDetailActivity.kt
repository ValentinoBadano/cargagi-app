package com.agrogi.cargagi

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.agrogi.cargagi.ui.theme.CargaGITheme

class ValeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vale_detail)

        val chofer = intent.getStringExtra("CHOFER")
        val descripcion = intent.getStringExtra("DESCRIPCION")
        val estacion = intent.getStringExtra("ESTACION")
        val fecha = intent.getStringExtra("FECHA")
        val tipo = intent.getStringExtra("TIPO")
        val montoLitros = intent.getStringExtra("MONTO_LITROS")
        val numeroVale = intent.getIntExtra("NUMERO_VALE", 0)
        val fechaSync = intent.getStringExtra("FECHA_SYNC")

        val choferTextView: TextView = findViewById(R.id.chofer_detail)
        val descripcionTextView: TextView = findViewById(R.id.descripcion_detail)
        val estacionTextView: TextView = findViewById(R.id.estacion_detail)
        val fechaTextView: TextView = findViewById(R.id.fecha_detail)
        val tipoTextView: TextView = findViewById(R.id.tipo_detail)
        val montoLitrosTextView: TextView = findViewById(R.id.monto_litros_detail)
        val numeroValeTextView: TextView = findViewById(R.id.numero_vale_detail)
        val fechaSyncTextView: TextView = findViewById(R.id.fecha_sync_detail)

        choferTextView.text = chofer
        descripcionTextView.text = descripcion
        estacionTextView.text = estacion
        fechaTextView.text = "Fecha del vale: $fecha"
        tipoTextView.text = tipo
        montoLitrosTextView.text = montoLitros
        numeroValeTextView.text = "Número de vale: $numeroVale"
        fechaSyncTextView.text = if (fechaSync.isNullOrEmpty()) {
            "El vale no ha sido sincronizado"
        } else {
            "El vale ha sido sincronizado el $fechaSync"
        }
    }
}