package com.example.whatsappclone.Fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri

import android.os.Bundle

import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.Adapters.StatusViewAdapter
import com.example.whatsappclone.Database.Status
import com.example.whatsappclone.Database.User
import com.example.whatsappclone.R
import com.example.whatsappclone.StatusView
import com.example.whatsappclone.databinding.FragmentStatusBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class StatusFragment:Fragment(R.layout.fragment_status) {
    companion object{
        const val MY_DATA = "myTotalImages"
    }
    private var myview:FragmentStatusBinding?=null
    private var activityForTakingImages:ActivityResultLauncher<Intent>?=null
    private var totalImages: ArrayList<Uri> = ArrayList()
    private var position = 0
    private var mauth:FirebaseAuth?=null
    private var mdatabase:FirebaseDatabase?=null
    private var mStorage:FirebaseStorage?=null
    private var adapter:StatusViewAdapter?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myview = FragmentStatusBinding.bind(view)
        mauth = FirebaseAuth.getInstance()
        mdatabase = FirebaseDatabase.getInstance()
        mStorage = FirebaseStorage.getInstance()
        setUpActivityForTakingImages()
        getStatusFromFireBase()
        myview?.addbtn?.setOnClickListener {
            totalImages.clear()
            Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                activityForTakingImages?.launch(it)
            }
        }

    }

    private fun getStatusFromFireBase() {
        mdatabase?.reference?.child("Status")?.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val mstatus = ArrayList<Status>()
                for(data in snapshot.children){
                    val status  = data.children.elementAt(0).getValue(Status::class.java)
                    status?.statusid = data.key
                    mstatus.add(status!!)
                }
                searchForUsers(mstatus)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun searchForUsers( status: ArrayList<Status>) {
          mdatabase?.reference?.child("Users")?.addValueEventListener(object :ValueEventListener{
              override fun onDataChange(snapshot: DataSnapshot) {
                  val muser = ArrayList<User>()
                  for (data in status){
                      val user = snapshot.child(data.statusid!!).getValue(User::class.java)
                      user?.userId = data.statusid
                      user?.emailid = data.statusimage
                      user?.userStatus = data.timestamp
                      muser.add(user!!)
                  }
                setUpMyAdater(muser)
              }

              override fun onCancelled(error: DatabaseError) {
                  TODO("Not yet implemented")
              }

          })


    }

    private fun setUpMyAdater(users:ArrayList<User>) {
        adapter = StatusViewAdapter(users)
        myview?.myrlview?.adapter = adapter
        myview?.myrlview?.layoutManager = LinearLayoutManager(this.context)
    }


    private fun setUpActivityForTakingImages(){
        activityForTakingImages  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            if(result.resultCode==RESULT_OK && result.data!=null){
                val count = result.data?.clipData?.itemCount
                var i=0
                while(i<count!!){
                    val uri = result.data?.clipData?.getItemAt(i)?.uri
                    totalImages.add(uri!!)
                    i++
                }
                saveDataToCloud()

            }
            else Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDataToCloud() {
        val mreference = mStorage?.reference?.child("Status")?.child(mauth?.uid!!)
        for(items in totalImages){
            mreference?.child(Calendar.getInstance().time.time.toString())?.putFile(items)?.addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {
                    uri->
                    val status = Status(Calendar.getInstance().time.time.toString(),uri.toString())
                    mdatabase?.reference?.child("Status")?.child(mauth?.uid!!)?.push()?.setValue(status)
                }
            }

        }
    }


    override fun onDestroy() {
        myview?.myrlview?.adapter= null
        super.onDestroy()
    }
}