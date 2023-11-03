package com.example.notefire_cloud.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class NoteData(
    val noteId: String? = null,
    val noteTitle: String? = null,
    val noteDescription: String? = null,
    val userId: String? = null,
    val time: Timestamp = Timestamp.now()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(noteId)
        parcel.writeString(noteTitle)
        parcel.writeString(noteDescription)
        parcel.writeString(userId)
        parcel.writeParcelable(time, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NoteData> {
        override fun createFromParcel(parcel: Parcel): NoteData {
            return NoteData(parcel)
        }

        override fun newArray(size: Int): Array<NoteData?> {
            return arrayOfNulls(size)
        }
    }
}
