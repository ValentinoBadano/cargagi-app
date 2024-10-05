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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agrogi.cargagi.ui.theme.CargaGITheme
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val vales: MutableList<Vale> = mutableListOf() // Aquí irán los datos
    private lateinit var recyclerView: RecyclerView
    private lateinit var valeAdapter: ValeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadVales()

        valeAdapter = ValeAdapter(vales)
        recyclerView.adapter = valeAdapter

        // botón flotante
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{navigateToCrearVale()}
    }


    private fun loadVales() {
        // Simulación de carga de datos
        vales.add(Vale(1, "José", estacion = "Ejemplo", importe = 0f, fecha = "2024-01-02", litros = 25f, sync=false))
        vales.add(Vale(2, "Carlos", estacion = "Ejemplo", importe = 10000f, fecha = "2024-01-02", litros = 0f, sync=true))
        vales.add(Vale(1, "José", estacion = "Ejemplo", importe = 0f, fecha = "2024-01-02", litros = 25f, sync=false))
        vales.add(Vale(2, "Carlos", estacion = "Ejemplo", importe = 10000f, fecha = "2024-01-02", litros = 0f, sync=true))
        vales.add(Vale(1, "José", estacion = "Ejemplo", importe = 0f, fecha = "2024-01-02", litros = 25f, sync=false))
        vales.add(Vale(2, "Carlos", estacion = "Ejemplo", importe = 10000f, fecha = "2024-01-02", litros = 0f, sync=true))
        vales.add(Vale(1, "José", estacion = "Ejemplo", importe = 0f, fecha = "2024-01-02", litros = 25f, sync=false))
        vales.add(Vale(2, "Carlos", estacion = "Ejemplo", importe = 10000f, fecha = "2024-01-02", litros = 0f, sync=true))
        vales.add(Vale(1, "José", estacion = "Ejemplo", importe = 0f, fecha = "2024-01-02", litros = 25f, sync=false))
        vales.add(Vale(2, "Carlos", estacion = "Ejemplo", importe = 10000f, fecha = "2024-01-02", litros = 0f, sync=true))
        vales.add(Vale(1, "José", estacion = "Ejemplo", importe = 0f, fecha = "2024-01-02", litros = 25f, sync=false))
        vales.add(Vale(2, "Carlos", estacion = "Ejemplo", importe = 10000f, fecha = "2024-01-02", litros = 0f, sync=true))
        // Agrega más datos según sea necesario
    }

    private fun navigateToCrearVale () {
        val intent = Intent(this, AgregarValeActivity::class.java)
        startActivity(intent)
    }
}

