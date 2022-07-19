package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.Adapters.ChatDetailsAdapter
import com.example.whatsappclone.Database.Messages
import com.example.whatsappclone.databinding.ActivityGroupChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class GroupChat : AppCompatActivity() {
    private var myview:ActivityGroupChatBinding?=null
    private var mauth:FirebaseAuth?=null
    private var mdatabase:FirebaseDatabase?=null
    private var madapter:ChatDetailsAdapter?=null
    private var allItems = ArrayList<Messages>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        mauth = FirebaseAuth.getInstance()
        mdatabase = FirebaseDatabase.getInstance()
        setUpMyAdapter()
        getAllMessage()
        myview?.flbtn?.setOnClickListener {
            val message  = myview?.textInputEditText?.text.toString()
            if(message.isNotEmpty())
                setAllMessages(message)
            else Toast.makeText(this, "Please Enter Message", Toast.LENGTH_SHORT).show()
        }
    }
  private  fun setUpMyAdapter(){
        madapter = ChatDetailsAdapter(allItems)
        myview?.myrlview?.adapter = madapter
        myview?.myrlview?.layoutManager = LinearLayoutManager(this)
    }

    private fun getAllMessage(){
        mdatabase?.reference?.child("GroupChats")?.addValueEventListener(
            object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    allItems.clear()
                    for(datasnapshot in snapshot.children){
                        val message = datasnapshot.getValue(Messages::class.java)
                        allItems.add(message!!)

                    }
                    myview?.myrlview?.adapter?.notifyDataSetChanged()
                    myview?.myrlview?.smoothScrollToPosition(myview?.myrlview?.adapter?.itemCount!!)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
        )
    }


    private fun setAllMessages(message:String){
        val messages = Messages(message,mauth?.currentUser?.uid!!,Calendar.getInstance().time.time)
        mdatabase?.reference?.child("GroupChats")?.push()?.setValue(messages)
    }


}