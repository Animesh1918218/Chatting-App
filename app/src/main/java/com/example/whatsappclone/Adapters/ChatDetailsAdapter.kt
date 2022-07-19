package com.example.whatsappclone.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.Database.Messages
import com.example.whatsappclone.databinding.RecieverMsgLayoutBinding
import com.example.whatsappclone.databinding.SenderLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import java.sql.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatDetailsAdapter(private val allitems:ArrayList<Messages>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val RECIEVER_CODE = 2
    private val SENDER_CODE = 1
    inner class SenderViewHolder(item:SenderLayoutBinding):RecyclerView.ViewHolder(item.root){
        val message = item.chattxt
        val time = item.timetxt
    }

    inner class RecieverViewHolder(item:RecieverMsgLayoutBinding):RecyclerView.ViewHolder(item.root){
        val message = item.chattxt
        val timestamp = item.timetxt
    }

    override fun getItemViewType(position: Int): Int {
        return if(FirebaseAuth.getInstance().currentUser?.uid==allitems[position].uid)
            SENDER_CODE
        else RECIEVER_CODE
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if(viewType==SENDER_CODE)
            SenderViewHolder(SenderLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        else
            RecieverViewHolder(RecieverMsgLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       if(holder.javaClass==SenderViewHolder::class.java){
           (holder as SenderViewHolder).message.text = allitems[position].message
           val sdf = SimpleDateFormat("hh:mm aa", Locale.UK)
          val time= sdf.format(allitems[position].timestamp)


           holder.time.text = time.toString()
       }
        else{
           (holder as RecieverViewHolder).message.text = allitems[position].message
           val sdf = SimpleDateFormat("hh:mm aa", Locale.UK)
           val time= sdf.format(allitems[position].timestamp)


           holder.timestamp.text = time.toString()
       }
    }

    override fun getItemCount(): Int {
         return allitems.size
    }
}