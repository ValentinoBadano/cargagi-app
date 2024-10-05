package com.agrogi.cargagi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ValeAdapter(private val vales: List<Vale>) : RecyclerView.Adapter<ValeAdapter.ValeViewHolder>() {

    class ValeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val tvFecha: TextView = view.findViewById(R.id.fecha)
        val tvChofer: TextView = view.findViewById(R.id.chofer)
        val tvDescripcion: TextView = view.findViewById(R.id.descripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vale, parent, false)
        return ValeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ValeViewHolder, position: Int) {
        val vale = vales[position]
        // holder.tvFecha.text = vale.fecha
        holder.tvChofer.text = vale.chofer
        holder.tvDescripcion.text = if (vale.importe != 0f) {
            "Monto: ${vale.importe} Efectivo"
        } else {
            "Litros: ${vale.litros} Combustible"
        }
    }

    override fun getItemCount(): Int = vales.size
}
