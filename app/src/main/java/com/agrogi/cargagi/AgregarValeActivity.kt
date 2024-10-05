package com.agrogi.cargagi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

class AgregarValeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.agregar_vale)

        val acceptButton = findViewById<Button>(R.id.acceptButton)
        acceptButton.setOnClickListener{acceptButton()}
    }

    private fun acceptButton() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

