package com.agrogi.cargagi

import android.os.Parcel
import android.os.Parcelable

data class Chofer(
    val id: String,
    val codigo: String,
    val codigo_empresa: String,
    val nombre: String,
    val cuit: String,
    val empresa: String,
    val empresaCuit: String,
    val patente: String,
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
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(codigo)
        parcel.writeString(nombre)
        parcel.writeString(cuit)
        parcel.writeString(empresa)
        parcel.writeString(empresaCuit)
        parcel.writeString(patente)
        parcel.writeString(telefono)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chofer> {
        override fun createFromParcel(parcel: Parcel): Chofer {
            return Chofer(parcel)
        }

        override fun newArray(size: Int): Array<Chofer?> {
            return arrayOfNulls(size)
        }
    }
}