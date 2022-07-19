package com.example.whatsappclone.Database

class Status {
    var timestamp:String?=null
    var statusimage:String?=null
    var statusid:String?=null
    constructor()
    constructor(timestamp:String,statusimage:String){
        this.statusimage = statusimage
        this.timestamp = timestamp
    }
    constructor(timestamp:String,statusimage:String,statusid:String){
        this.statusimage = statusimage
        this.timestamp = timestamp
        this.statusid = statusid
    }


    }

