package com.agrogi.cargagi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.text.toLowerCase
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Locale

class ValeAdapter(private val vales: List<Vale>, choferes: List<Chofer>) : RecyclerView.Adapter<ValeAdapter.ValeViewHolder>() {

    private val choferes = choferes

    class ValeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val tvFecha: TextView = view.findViewById(R.id.fecha)
        val tvChofer: TextView = view.findViewById(R.id.chofer)
        val tvDescripcion: TextView = view.findViewById(R.id.descripcion)
        val tvImage : ImageView = view.findViewById(R.id.featured_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vale, parent, false)
        return ValeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ValeViewHolder, position: Int) {
        val vale = vales[position]
        val chofer = vale.chofer.nombre.lowercase().split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }

        val fecha = vale.fecha.substring(6, 8) + "/" + vale.fecha.substring(4, 6) + "/" + vale.fecha.substring(0, 4)

        holder.tvChofer.text = String.format(Locale.getDefault(), "%s - %s", chofer, fecha)

        holder.tvDescripcion.text = if (vale.efectivo != 0f) {
            "Monto: ${vale.efectivo} Efectivo"
        } else {
            "Litros: ${vale.litros} Combustible"
        }

        if (vale.fechaSync.isNotEmpty()) {
            holder.tvImage.setImageResource(R.drawable.ic_sync_success)
        } else {
            holder.tvImage.setImageResource(R.drawable.ic_sync_failed)
        }

        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Detalles del Vale")
            builder.setMessage("ID: ${vale.id}\nChofer: ${chofer}\nFecha: ${fecha}\nDescripción: ${holder.tvDescripcion.text}")
            builder.setPositiveButton("OK", null)
            builder.show()
        }
    }

    override fun getItemCount(): Int = vales.size
}
