package com.example.whatsappclone.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.Database.User
import com.example.whatsappclone.R
import com.example.whatsappclone.StatusView
import com.example.whatsappclone.databinding.StatusAdaptorBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatusViewAdapter(val items:ArrayList<User>):RecyclerView.Adapter<StatusViewAdapter.MainViewHolder>() {

    companion object{
        const val MY_DATA ="mydata"
    }
     private var context:Context?=null
      inner class MainViewHolder(item:StatusAdaptorBinding):RecyclerView.ViewHolder(item.root){
          val myimage = item.shapeableImageView2
          val myname = item.nametxt
          val mtime = item.timetxt
          val mlayout = item.mylayout
      }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        context = parent.context
        return MainViewHolder(StatusAdaptorBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
          Picasso.get().load(items[position].emailid).placeholder(R.drawable.ic_baseline_image_24).into(holder.myimage)
          val sdf = SimpleDateFormat("dd/MM/yy  HH:MM aa", Locale.ENGLISH)
          val time = sdf.format(items[position].userStatus?.toLong())
          holder.mtime.text = time
          holder.myname.text = items[position].username
          holder.mlayout.setOnClickListener {
              Intent(context,StatusView::class.java).also {
                  it.putExtra(MY_DATA,items[position])
                  context?.startActivity(it)
              }
          }


    }

    override fun getItemCount(): Int {
       return items.size
    }
}