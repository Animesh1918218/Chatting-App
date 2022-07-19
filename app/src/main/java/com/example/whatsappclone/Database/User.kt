package com.example.whatsappclone.Database

import android.os.Parcel
import android.os.Parcelable


class User :Parcelable {
    var username:String?=null
    var password:String?=null
    var emailid:String?=null
    var profilepic:String?=null
    var userId:String?=null
    var userStatus:String?="Offline"


    constructor(parcel: Parcel) {
        username = parcel.readString()
        password = parcel.readString()
        emailid = parcel.readString()
        profilepic = parcel.readString()
        userId = parcel.readString()

    }


    constructor(){}
    constructor(username:String,emailid:String,password:String){
        this.emailid = emailid
        this.password = password
        this.username = username
    }
    constructor(username:String,emailid:String,password:String,profilepic:String,userId:String){
        this.emailid = emailid
        this.password = password
        this.username = username
        this.profilepic = profilepic
        this.userId = userId

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
            dest?.writeString(this.username)
        dest?.writeString(this.password)
        dest?.writeString(this.emailid)
        dest?.writeString(this.profilepic)
        dest?.writeString(this.userId)


    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}