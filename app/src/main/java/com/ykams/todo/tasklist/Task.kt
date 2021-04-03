package com.ykams.todo.tasklist

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String? = ""
) :  Parcelable {
    constructor(parcel: Parcel): this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString()
    )

     companion object : Parceler<Task> {
        override fun Task.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(title)
            parcel.writeString(description)
        }

        override fun create(parcel: Parcel): Task = Task(parcel)
    }
}
