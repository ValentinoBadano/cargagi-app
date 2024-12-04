package com.agrogi.cargagi

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HintAdapter(context: Context, resource: Int, objects: List<String>) : ArrayAdapter<String>(context, resource, objects) {
    override fun isEnabled(position: Int): Boolean {
        // Disable the first item from Spinner
        return position != 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view as TextView
        // Set the hint text color for the first item
        if (position == 0) {
            textView.setTextColor(Color.GRAY)
        } else {
            textView.setTextColor(Color.BLACK)
        }
        return view
    }
}