package com.example.whatsappclone.Database

import android.os.Parcel
import android.os.Parcelable

class Messages :Parcelable {
    var message:String ?=null
    var uid:String?=null
    var timestamp:Long?=null

    constructor(parcel: Parcel) {
        message = parcel.readString()
        uid = parcel.readString()
        timestamp = parcel.readLong()
    }

    constructor(){}
    constructor(messages: String,uid:String,timestamp:Long){
        this.message = messages
        this.uid = uid
        this.timestamp = timestamp
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeString(uid)
        parcel.writeLong(timestamp!!)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Messages> {
        override fun createFromParcel(parcel: Parcel): Messages {
            return Messages(parcel)
        }

        override fun newArray(size: Int): Array<Messages?> {
            return arrayOfNulls(size)
        }
    }

}