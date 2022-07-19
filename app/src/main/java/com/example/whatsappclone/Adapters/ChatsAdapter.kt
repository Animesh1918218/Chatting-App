package com.example.whatsappclone.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.ChatDetail
import com.example.whatsappclone.Database.User
import com.example.whatsappclone.R
import com.example.whatsappclone.databinding.ChatsAdpaterLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ChatsAdapter(private val user:ArrayList<User>):RecyclerView.Adapter<ChatsAdapter.MainViewHolder>() {
    companion object{
        const val MY_DATA="myalldata"
    }
    private var context:Context?=null
    inner class MainViewHolder(item:ChatsAdpaterLayoutBinding):RecyclerView.ViewHolder(item.root) {
            val messagetxt = item.messagetxt
            val nametxt = item.nametxt
            val image = item.imageview
            val layput = item.mylayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        context = parent.context
        return MainViewHolder(ChatsAdpaterLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.nametxt.text = user[position].username

        FirebaseDatabase.getInstance().reference.child("Chats").child(FirebaseAuth.getInstance().currentUser?.uid+user[position].userId).addValueEventListener(
            object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChildren()){
                        var text:String?=null
                        for(datasnapshot in snapshot.children){
                             text = datasnapshot.child("message").getValue(String::class.java)
                        }
                        if(text!!.length>=17){
                            holder.messagetxt.text = text.substring(0,17)+".."
                        }
                        else holder.messagetxt.text = text
                    }
                    else holder.messagetxt.text = "No Message"
                }

                override fun onCancelled(error: DatabaseError) {
                      Toast.makeText(context,"$error",Toast.LENGTH_SHORT).show()
                }

            }
        )
        Picasso.get().load(user[position].profilepic).placeholder(R.drawable.ic_baseline_image_24).into(holder.image)
        holder.layput.setOnClickListener {
            Intent(context,ChatDetail::class.java).also {
                it.putExtra(MY_DATA,user[position])
                context?.startActivity(it)
            }
        }

    }

    override fun getItemCount(): Int {
       return user.size
    }

}