package com.agrogi.cargagi

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.FileOutputStream

class AgregarValeActivity : AppCompatActivity() {

    private lateinit var datePickerButton: Button
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var spinnerChofer: Spinner
    private lateinit var spinnerEstacion: Spinner
    private lateinit var radioGroupTipo: RadioGroup
    private lateinit var editTextNumero: EditText

    private val choferes: MutableList<Chofer> = mutableListOf()
    private val choferesFile by lazy { File(filesDir, "choferes.txt") }

    private val estaciones : MutableList<Estacion> = mutableListOf()
    private val estacionesFile by lazy { File(filesDir, "estacions.txt") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_vale)

        datePickerButton = findViewById(R.id.inputDate)
        spinnerChofer = findViewById(R.id.spinnerChofer)
        spinnerEstacion = findViewById(R.id.spinnerEstacion)
        radioGroupTipo = findViewById(R.id.opciones_vale)
        editTextNumero = findViewById(R.id.editTextNumber)

        choferes.add(Chofer("1", "1", "Chofer", "20345678901", "Empresa 1", "20345678901", "ABC123"))
        estaciones.add(Estacion("1", "1", "Estación", "Calle 123", "20345678901"))


        loadChoferes()
        setupChoferSpinner()

        loadEstaciones()
        setupEstacionSpinner()

        initDatePicker()

        val acceptButton = findViewById<Button>(R.id.acceptButton)
        acceptButton.setOnClickListener{
            acceptButton()
        }

        radioGroupTipo.setOnCheckedChangeListener { group, checkedId ->
            onOptionSelected()
        }
    }

    private fun acceptButton() {
        // crea el nuevo vale con los datos ingresados
        // fecha en formato YYYYMMDD
        val fecha = datePickerButton.text.toString()
        val chofer = spinnerChofer.selectedItem.toString()
        val estacion = spinnerEstacion.selectedItem.toString()
        val numero = editTextNumero.text.toString().toFloatOrNull() ?: 0f

        val tipo = when (radioGroupTipo.checkedRadioButtonId) {
            R.id.radio_efectivo -> "E"
            R.id.radio_combustible -> "C"
            else -> ""
        }

        // verifica que el vale tenga todos los datos necesarios
        if (fecha.isEmpty() || chofer.isEmpty() || estacion.isEmpty() || numero == 0f || tipo.isEmpty()) {
            // muestra un mensaje Snackbar con un error
            showSnackbar("Por favor, complete todos los campos")
            return
        }

        // Fecha: Debe estar dentro de un rango de dos días antes o después del día actual.
        val fechaActual = LocalDate.now()
        val fechaVale = LocalDate.parse(fecha)
        if (fechaVale.isBefore(fechaActual.minusDays(2)) || fechaVale.isAfter(fechaActual.plusDays(2))) {
            showSnackbar("La fecha debe estar dentro de un rango de dos días antes o después del día actual.")
            return
        }


        // get Chofer with name chofer
        val choferObj = choferes.find { it.nombre == chofer }
        // get Estacion with name estacion
        val estacionObj = estaciones.find { it.nombre == estacion }

        val nuevoVale = Vale(
            id = 0,
            chofer = choferObj ?: Chofer("", "", "", "", "", "", ""),
            estacion = estacionObj ?: Estacion("", "", "", "", ""),
            fecha = fecha.replace("-", ""),
            tipo = tipo,
            litros = if (tipo == "C") numero else 0f,
            efectivo = if (tipo == "E") numero else 0f,
            nro1 = 0,
            nro2 = 0,
            dominio = choferObj?.patente ?: "",
            fechaSync = ""
        )

        showConfirmationDialog(nuevoVale)
    }

    private fun addVale(nuevoVale: Vale) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("nuevoVale", nuevoVale)
        setResult(RESULT_OK, intent)
        val pdfFile = createPdf(this, "vale_${nuevoVale.id}.pdf", generatePdfContent(nuevoVale))
        if (pdfFile != null) {
            sendWhatsAppMessage(this,"542355556925", pdfFile, "Nuevo vale")
            Log.d("PDF", "PDF creado correctamente en ${pdfFile.absolutePath}")
        } else {
            Toast.makeText(this, "Error al crear el PDF", Toast.LENGTH_SHORT).show()
            Log.e("PDF", "Error al crear el PDF")
        }
        finish() // Close the current activity
    }

    private fun showConfirmationDialog(nuevoVale: Vale) {
        val fecha = nuevoVale.fecha.substring(6, 8) + "/" + nuevoVale.fecha.substring(4, 6) + "/" + nuevoVale.fecha.substring(0, 4)

        val message = """
        Chofer: ${nuevoVale.chofer.nombre}
        Estación: ${nuevoVale.estacion.nombre}
        Fecha: $fecha
        Tipo: ${if (nuevoVale.tipo == "E") "Efectivo" else "Combustible"}
        ${if (nuevoVale.tipo == "E") "Efectivo: " + nuevoVale.efectivo else "Combustible: " + nuevoVale.litros}
    """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Confirmar Vale")
            .setMessage(message)
            .setPositiveButton("Confirmar") { dialog, which ->
                // Proceed with adding the vale
                addVale(nuevoVale)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun initDatePicker() {
        // inicializa el date picker en la fecha de hoy
        val date = LocalDate.now()

        datePickerDialog = DatePickerDialog(this, { view, year, month, dayOfMonth ->
            val date = LocalDate.of(year, month, dayOfMonth)
            datePickerButton.text = date.toString()
        }, date.year, date.monthValue - 1, date.dayOfMonth)

        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

        datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
            val date = LocalDate.of(year, month + 1, dayOfMonth)
            datePickerButton.text = date.toString()
        }
    }


    private fun showDatePickerDialog() {
        // change date picker title color
        datePickerDialog.show()
    }

    private fun onOptionSelected() {
        val radioId = radioGroupTipo.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(radioId)

        // cambia la hint del editText según la opción seleccionada
        editTextNumero.hint = when (radioButton.text) {
            "Efectivo" -> "Monto"
            "Combustible" -> "Litros"
            else -> ""
        }
    }

    private fun showSnackbar(message: String) {
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }

    fun loadChoferesFromFile(file: File): List<Chofer> {
        Log.d("CargaChoferes", "Cargando choferes desde archivo")
        if (!file.exists()) return emptyList()
        val choferes = mutableListOf<Chofer>()
        BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.ISO_8859_1)).use { reader ->
            reader.lineSequence().forEach { line ->
                val parts = line.split(";")
                val chofer = Chofer(
                    id = parts[0].trim(),
                    codigo = parts[1].trim(),
                    nombre = parts[3].trim(),
                    cuit = parts[4].trim(),
                    empresa = parts[5].trim(),
                    empresaCuit = parts[6].trim(),
                    patente = parts[7].trim()
                )
                choferes.add(chofer)
            }
        }
        Log.d("CargaChoferes", "Choferes cargados.")
        return choferes
    }

    private fun loadChoferes() {
        choferes.addAll(loadChoferesFromFile(choferesFile))
    }

    private fun setupChoferSpinner() {
        Log.d("CargaChoferes", "Configurando spinner de choferes")
        val adapter = HintAdapter(this, android.R.layout.simple_spinner_item, choferes.map { it.nombre })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // prompt
        spinnerChofer.prompt = "Seleccione un chofer"
        spinnerChofer.adapter = adapter
        Log.d("CargaChoferes", "Spinner de choferes configurado")
    }

    private fun loadEstacionesFromFile(file: File): List<Estacion> {
        Log.d("CargaEstaciones", "Cargando estaciones desde archivo")
        if (!file.exists()) return emptyList()
        val estaciones = mutableListOf<Estacion>()
        BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.ISO_8859_1)).use { reader ->
            reader.lineSequence().forEach { line ->
                val parts = line.split(";")
                val estacion = Estacion(
                    id = parts[0].trim(),
                    codigo = parts[1].trim(),
                    nombre = parts[2].trim(),
                    direccion = parts[3].trim(),
                    cuit = parts[4].trim()
                )
                estaciones.add(estacion)
            }
        }
        Log.d("CargaEstaciones", "Estaciones cargadas.")
        return estaciones
    }

    private fun loadEstaciones() {
        estaciones.addAll(loadEstacionesFromFile(estacionesFile))
    }

    private fun setupEstacionSpinner() {
        Log.d("CargaEstaciones", "Configurando spinner de estaciones")
        val adapter = HintAdapter(this, android.R.layout.simple_spinner_item, estaciones.map { it.nombre })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerEstacion.prompt = "Seleccione una estación"
        spinnerEstacion.adapter = adapter
        Log.d("CargaEstaciones", "Spinner de estaciones configurado")
    }


    fun sendWhatsAppMessage(context: Context, phoneNumber: String, pdfFile: File, message: String) {
        try {
            // Generar la URI usando FileProvider
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                pdfFile
            )



            // Crear el Intent para WhatsApp
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                setPackage("com.whatsapp") // Asegura que use WhatsApp
                setData(uri)
                putExtra(Intent.EXTRA_STREAM, uri) // Archivo PDF
                putExtra(Intent.EXTRA_TEXT, message) // Mensaje de texto
                putExtra("jid", "$phoneNumber@s.whatsapp.net") // JID del destinatario
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }



            // Iniciar el Intent
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("WhatsApp", "Error al enviar mensaje por WhatsApp", e)
            Toast.makeText(context, "WhatsApp no está instalado o hubo un error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createPdf(context: Context, fileName: String, content: String): File? {
        /**
         * Crea un archivo PDF con el contenido especificado.
         * @param context Contexto de la aplicación.
         * @param fileName Nombre del archivo PDF.
         * @param content Contenido del archivo PDF.
         * @return Archivo PDF creado o null si ocurre un error.
         */
        return try {
            // Crear el documento PDF
            val pdfDocument = PdfDocument()
            val paint = Paint()
            val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            // Dibujar contenido en el PDF
            val canvas: Canvas = page.canvas
            canvas.drawText("Comprobante", 100f, 50f, paint) // Título
            canvas.drawText(content, 50f, 100f, paint)       // Contenido dinámico

            pdfDocument.finishPage(page)

            // guarda el archivo
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)

            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
            null
        }
    }

    private fun generatePdfContent(vale: Vale): String {
        val fecha = vale.fecha.substring(6, 8) + "/" + vale.fecha.substring(4, 6) + "/" + vale.fecha.substring(0, 4)
        return """
        ID: ${vale.id}
        Chofer: ${vale.chofer.nombre}
        Estación: ${vale.estacion.nombre}
        Fecha: $fecha
        Tipo: ${if (vale.tipo == "E") "Efectivo" else "Combustible"}
        ${if (vale.tipo == "E") "Efectivo: " + vale.efectivo else "Combustible: " + vale.litros}
        """.trimIndent().replace("\n", "\n")
    }

}

