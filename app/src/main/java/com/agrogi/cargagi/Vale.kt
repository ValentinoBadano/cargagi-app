package com.agrogi.cargagi

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Vale(
    var id: Int,
    var chofer: Chofer,
    var estacion: Estacion,
    var fecha: String,
    var tipo: String,
    var litros: Float,
    var efectivo: Float,
    var nro1: Int,
    var nro2: Int,
    var dominio: String,
    var fechaSync : String
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
    p0.writeInt(id)
    p0.writeParcelable(chofer, p1)
    p0.writeParcelable(estacion, p1)
    p0.writeString(fecha)
    p0.writeString(tipo)
    p0.writeFloat(litros)
    p0.writeFloat(efectivo)
    p0.writeInt(nro1)
    p0.writeInt(nro2)
    p0.writeString(dominio)
    p0.writeString(fechaSync)
}

    companion object CREATOR : Parcelable.Creator<Vale> {
    override fun createFromParcel(parcel: Parcel): Vale {
        return Vale(
            id = parcel.readInt(),
            chofer = parcel.readParcelable(Chofer::class.java.classLoader)!!,
            estacion = parcel.readParcelable(Estacion::class.java.classLoader)!!,
            fecha = parcel.readString() ?: "",
            tipo = parcel.readString() ?: "",
            litros = parcel.readFloat(),
            efectivo = parcel.readFloat(),
            nro1 = parcel.readInt(),
            nro2 = parcel.readInt(),
            dominio = parcel.readString() ?: "",
            fechaSync = parcel.readString() ?: ""
        )
    }

    override fun newArray(size: Int): Array<Vale?> {
        return arrayOfNulls(size)
    }
}
}