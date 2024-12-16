package com.agrogi.cargagi

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.File
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val vales: MutableList<Vale> = mutableListOf() // Aquí irán los datos
    private lateinit var recyclerView: RecyclerView
    private lateinit var valeAdapter: ValeAdapter
    private val file by lazy { File(filesDir, "vales.txt") }
    private var serverIp = "http://10.0.2.2:5000"  // IP local para el emulador de Android Studio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        // obtenemos la ip de shared preferences
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        serverIp = sharedPreferences.getString("server_ip", "http://10.0.2.2:5000") ?: "http://10.0.2.2:5000"

        // if serverIp is null, set default value
        if (serverIp.isEmpty()) {
            serverIp = "http://10.0.2.2:5000"
            }

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val choferes = loadChoferesFromFile(File(filesDir, "choferes.txt"))

        valeAdapter = ValeAdapter(vales, choferes)
        recyclerView.adapter = valeAdapter

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { navigateToCrearVale() }

        downloadFile("estacions.txt")
        Thread.sleep(300)
        downloadFile("choferes.txt")
        Thread.sleep(300)
        sincronizar()

        loadVales()
    }

    // Definir el ActivityResultLauncher
    private val agregarValeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val nuevoVale = data?.getParcelableExtra<Vale>("nuevoVale")
            // Asignar un id al nuevo vale
            nuevoVale?.id = getNextId()
            nuevoVale?.let {
                vales.add(it)
                valeAdapter.notifyItemInserted(vales.size - 1)
                saveVales()
                sincronizar()
            }
        }
    }


    private fun loadVales() {
        loadValesFromJson()
    }

    private fun saveVales() {
        saveValesToJson()
    }

    private fun navigateToCrearVale() {
        val intent = Intent(this, AgregarValeActivity::class.java)
        agregarValeLauncher.launch(intent)
    }

    fun loadChoferesFromFile(file: File): List<Chofer> {
        if (!file.exists()) return emptyList()
        val choferes = mutableListOf<Chofer>()
        BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.ISO_8859_1)).use { reader ->
            reader.lineSequence().forEach { line ->
                val parts = line.split(";")
                val chofer = Chofer(
                    id = parts[0].trim(),
                    codigo = parts[1].trim(),
                    codigoEmpresa = parts[2].trim(),
                    nombre = parts[3].trim(),
                    cuit = parts[4].trim(),
                    empresa = parts[5].trim(),
                    empresaCuit = parts[6].trim(),
                    patente = parts[7].trim(),
                    telefono = parts[8].trim(),
                    email = parts[9].trim()
                )
                choferes.add(chofer)
            }
        }
        return choferes
    }

    private fun saveValesToJson() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(vales)
        val file = File(filesDir, "vales.json")
        FileWriter(file).use { it.write(jsonString) }
    }

    private fun loadValesFromJson() {
        val file = File(filesDir, "vales.json")
        if (file.exists()) {
            val gson = Gson()
            val type = object : TypeToken<List<Vale>>() {}.type
            vales.clear()
            vales.addAll(gson.fromJson(FileReader(file), type))
            valeAdapter.notifyDataSetChanged()
        }
    }


    private fun downloadFile(fileName: String) {
        thread {
            try {
                // Crear la conexión HTTP
                val url = URL("$serverIp/$fileName")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000  // 10 segundos
                connection.readTimeout = 10000
                connection.connect()

                // Verificar si la respuesta es exitosa
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    // Leer el contenido de la respuesta
                    val inputStream: InputStream = connection.inputStream
                    val file = File(filesDir, fileName)

                    FileOutputStream(file).use { outputStream ->
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        while (true) {
                            bytesRead = inputStream.read(buffer)
                            if (bytesRead == -1) break
                            outputStream.write(buffer, 0, bytesRead)
                        }
                    }

                    runOnUiThread {
                        Toast.makeText(this, "$fileName sincronizado.", Toast.LENGTH_SHORT).show()
                    }
                    Thread.sleep(1000)
                } else {
                    Log.e("DownloadActivity", "Error en la respuesta: ${connection.responseCode}")
                    runOnUiThread {
                        Toast.makeText(this, "Error al descargar $fileName", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("DownloadActivity", "Error descargando archivo $fileName", e)
                runOnUiThread {
                    Toast.makeText(this, "Error descargando $fileName", Toast.LENGTH_SHORT).show()
                }
            }
            Log.d("DownloadActivity", "Fin de la descarga de $fileName")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_desplegable, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.action_sync -> {
                sincronizar()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getNextId() : Int {
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val nextId = sharedPreferences.getInt("next_vale_id", 1)
        with(sharedPreferences.edit()) {
            putInt("next_vale_id", nextId + 1)
            apply()
        }
        return nextId
    }

    private fun sincronizar() {
        /* Sincronizar los vales con el servidor */
        thread {
            try {
                val url = URL("$serverIp/upload_vales/50")
                val boundary = "----WebKitFormBoundary" + System.currentTimeMillis()
                val lineEnd = "\r\n"
                val twoHyphens = "--"

                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.doInput = true
                connection.useCaches = false
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")



                // Usar DataOutputStream para escribir bytes
                val outputStream = DataOutputStream(connection.outputStream)

                // Escribir la parte del archivo
                outputStream.writeBytes("$twoHyphens$boundary$lineEnd")
                outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"vales.txt\"$lineEnd")
                outputStream.writeBytes("Content-Type: text/plain$lineEnd")
                outputStream.writeBytes(lineEnd)

                // Escribir el contenido del archivo
                val file = File(filesDir, "vales.json")
                if (!file.exists()) {
                    runOnUiThread {
                        Toast.makeText(this, "El archivo vales.json no existe", Toast.LENGTH_SHORT).show()
                    }
                    return@thread
                }
                FileInputStream(file).use { fileInputStream ->
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }

                // Escribir el final del multipart
                outputStream.writeBytes(lineEnd)
                outputStream.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")

                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread {
                        Toast.makeText(this, "Vales sincronizados", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorStream = connection.errorStream
                    val errorResponse = errorStream?.bufferedReader()?.use { it.readText() }
                    runOnUiThread {
                        Log.e("Sincronizar", "Error sincronizando vales: $responseCode - $errorResponse")
                        Toast.makeText(this, "Error al sincronizar vales", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("Sincronizar", "Error sincronizando vales", e)
                runOnUiThread {
                    Toast.makeText(this, "Error al sincronizar vales", Toast.LENGTH_SHORT).show()
                }
            }

            fetchValesFromServer()?.let { serverVales ->
                mergeVales(serverVales, vales)
                saveVales()
            }
        }
    }

    private fun fetchValesFromServer(): List<Vale>? {
    return try {
        val url = URL("$serverIp/vales.json/50")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = reader.readText()
            reader.close()

            val gson = Gson()
            val type = object : TypeToken<List<Vale>>() {}.type
            gson.fromJson<List<Vale>>(response, type)
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("FetchVales", "Error fetching vales from server", e)
        null
    }
}

    private fun mergeVales(serverVales: List<Vale>, localVales: MutableList<Vale>) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        serverVales.forEach { serverVale ->
            val localVale = localVales.find { it.id == serverVale.id }
            if (localVale == null) {
                localVales.add(serverVale)
            } else if (localVale.fechaSync.isEmpty()) {
                localVale.fechaSync = currentDate
                runOnUiThread {
                    valeAdapter.notifyItemChanged(localVales.indexOf(localVale))
                }
            }
        }
    }


}

