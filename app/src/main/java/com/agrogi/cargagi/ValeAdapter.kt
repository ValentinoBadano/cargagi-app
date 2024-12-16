package com.agrogi.cargagi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale


class ValeAdapter(private val vales: List<Vale>, choferes: List<Chofer>) : RecyclerView.Adapter<ValeAdapter.ValeViewHolder>() {

    private val choferes = choferes

    class ValeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val tvFecha: TextView = view.findViewById(R.id.fecha)
        val tvChofer: TextView = view.findViewById(R.id.chofer)
        val tvDescripcion: TextView = view.findViewById(R.id.descripcion)
        val statusTextView : TextView = view.findViewById(R.id.status_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vale, parent, false)
        return ValeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ValeViewHolder, position: Int) {
        val vale = vales[position]
        val chofer = formatName(vale.chofer.nombre)
        val estacion = formatName(vale.estacion.nombre)

        val fecha = vale.fecha.substring(6, 8) + "/" + vale.fecha.substring(4, 6) + "/" + vale.fecha.substring(2, 4)

        holder.tvChofer.text = String.format(Locale.getDefault(), "%s", chofer)

        holder.tvDescripcion.text = if (vale.efectivo != 0f) {
            "Monto: $${"%.2f".format(vale.efectivo)} - $fecha"
        } else {
            "Litros: ${vale.litros} - $fecha"
        }

        if (vale.fechaSync.isNotEmpty()) {
            holder.statusTextView.text = "Sincronizado"
            holder.statusTextView.background = holder.itemView.context.getDrawable(R.drawable.rounded_green)
        } else {
            holder.statusTextView.text = "Pendiente"
            holder.statusTextView.background = holder.itemView.context.getDrawable(R.drawable.rounded_orange)
        }

        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Detalles del Vale")
            builder.setMessage("ID: ${vale.id}\nChofer: ${chofer}\nFecha: ${fecha}\nEstación: ${estacion}\n" + if (vale.efectivo != 0f) {
                "Monto: $${"%.2f".format(vale.efectivo)}"
            } else {
                "Litros: ${vale.litros}"
            })
            builder.setPositiveButton("OK", null)
            builder.show()
        }
    }

    private fun formatName(name: String): String {
        return name.lowercase().split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
    }

    override fun getItemCount(): Int = vales.size
}
