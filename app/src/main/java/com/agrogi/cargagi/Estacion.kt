package com.agrogi.cargagi

import android.os.Parcel
import android.os.Parcelable

data class Estacion(
    val id: String,
    val codigo: String,
    val nombre: String,
    val direccion: String,
    val cuit: String,
    val telefono: String,
    val email: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(codigo)
        parcel.writeString(nombre)
        parcel.writeString(direccion)
        parcel.writeString(cuit)
        parcel.writeString(telefono)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Estacion> {
        override fun createFromParcel(parcel: Parcel): Estacion {
            return Estacion(parcel)
        }

        override fun newArray(size: Int): Array<Estacion?> {
            return arrayOfNulls(size)
        }
    }
}
