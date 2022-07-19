package com.example.whatsappclone.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

import android.view.View

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.Adapters.ChatsAdapter
import com.example.whatsappclone.Database.User
import com.example.whatsappclone.R
import com.example.whatsappclone.databinding.FragmentChatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatsFragment : Fragment(R.layout.fragment_chats) {
      private var myview:FragmentChatsBinding?=null
      private var myitems = ArrayList<User>()
      private var database:FirebaseDatabase?=null
      private var myadapter:ChatsAdapter?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myview = FragmentChatsBinding.bind(view)
        database = FirebaseDatabase.getInstance()
        getValuesFromDatabase()
        setuUpMyAdpter()

        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid!!).child("userStatus").setValue("Online")

    }


    private fun setuUpMyAdpter(){
        myadapter = ChatsAdapter(myitems)

        myview?.myrlview?.adapter = myadapter
        myview?.myrlview?.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
    }
    private fun getValuesFromDatabase(){
        database?.reference?.child("Users")?.addValueEventListener(
          object : ValueEventListener{
              override fun onDataChange(snapshot: DataSnapshot) {
                  myitems.clear()
                 for(datasnaphot in snapshot.children){
                     val user = datasnaphot.getValue(User::class.java)
                     user?.userId = datasnaphot.key
                     Log.i("Picture","${user?.profilepic}")
                     if(user?.userId==FirebaseAuth.getInstance().currentUser?.uid)
                         continue
                     else
                         myitems.add(user!!)

                 }
                  myview?.myrlview?.adapter?.notifyDataSetChanged()

              }

              override fun onCancelled(error: DatabaseError) {
                  Toast.makeText(requireContext(),"$error",Toast.LENGTH_SHORT).show()
              }

          }
        )
    }
}