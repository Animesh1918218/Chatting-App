package com.example.whatsappclone


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.Adapters.ChatDetailsAdapter

import com.example.whatsappclone.Adapters.ChatsAdapter
import com.example.whatsappclone.Database.Messages
import com.example.whatsappclone.Database.User
import com.example.whatsappclone.databinding.ActivityChatDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList


class ChatDetail : AppCompatActivity() {
    private var myview:ActivityChatDetailBinding?=null
    private var myauth:FirebaseAuth?=null
    private var mdatabase:FirebaseDatabase?=null
    private var myadapter:ChatDetailsAdapter?=null
    private var mlist = ArrayList<Messages>()
    private var senderCode:String?=null
    private var recieverCode:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        myauth = FirebaseAuth.getInstance()
        mdatabase = FirebaseDatabase.getInstance()
        myview?.mytoolbar?.title = ""
        setSupportActionBar(myview?.mytoolbar)
        val user = intent.getParcelableExtra<User>(ChatsAdapter.MY_DATA)
        myview?.nametxt?.text = user?.username

        changeStatusOfUser(user?.userId!!)

        senderCode =  myauth?.currentUser?.uid+user?.userId
        recieverCode = user?.userId + myauth?.currentUser?.uid
        setUpMyAdapter()
        setUpMyData()
        myview?.flbtn?.setOnClickListener {
            val chat = myview?.textInputEditText?.text.toString()
            if(chat.isNotEmpty())
                storeMyDataMessage(chat)
            else Toast.makeText(this, "Please Type A Message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeStatusOfUser(userId:String) {
          mdatabase?.reference?.child("Users")?.child(userId)?.addValueEventListener(object :ValueEventListener{
              override fun onDataChange(snapshot: DataSnapshot) {
                  val user = snapshot.getValue(User::class.java)
                  myview?.statustxt?.text = user?.userStatus
              }

              override fun onCancelled(error: DatabaseError) {
                  TODO("Not yet implemented")
              }

          })
    }


    private fun setUpMyAdapter(){
    myadapter = ChatDetailsAdapter(mlist)
    myview?.myrlview?.adapter = myadapter
    myview?.myrlview?.layoutManager = LinearLayoutManager(this)
    }


    private fun setUpMyData(){

         mdatabase?.reference?.child("Chats")?.addValueEventListener(object :ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 mlist.clear()
                 for(datasnapshot in snapshot.child(senderCode!!).children){
                     val message = datasnapshot.getValue(Messages::class.java)
                     mlist.add(message!!)
                 }
                 myview?.myrlview?.adapter?.notifyDataSetChanged()
                 myview?.myrlview?.smoothScrollToPosition(myview?.myrlview?.adapter?.itemCount!!)
             }

             override fun onCancelled(error: DatabaseError) {
                 Toast.makeText(this@ChatDetail,"$error",Toast.LENGTH_SHORT).show()
             }

         })

    }




   private fun storeMyDataMessage(chat:String){
       val message = Messages(chat,myauth?.currentUser?.uid!!,Calendar.getInstance().time.time)
       mdatabase?.reference?.child("Chats")?.child(senderCode!!)?.push()?.setValue(message)?.addOnSuccessListener {
           mdatabase?.reference?.child("Chats")?.child(recieverCode!!)?.push()?.setValue(message)
       }
       myview?.myrlview?.smoothScrollToPosition(myview?.myrlview?.adapter?.itemCount!!)
       myview?.textInputEditText?.setText("")
   }

}