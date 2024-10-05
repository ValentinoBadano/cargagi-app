package com.agrogi.cargagi

data class Vale(
    val id: Int,
    val chofer: String,
    val estacion: String,
    val importe: Float,
    val litros: Float,
    val fecha: String,
    val sync: Boolean
)
