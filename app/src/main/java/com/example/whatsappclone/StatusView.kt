package com.example.whatsappclone


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import com.example.whatsappclone.Adapters.StatusViewAdapter

import com.example.whatsappclone.Database.Status
import com.example.whatsappclone.Database.User

import com.example.whatsappclone.databinding.ActivityStatusViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import java.lang.Exception


import kotlin.collections.ArrayList


class StatusView : AppCompatActivity(){
    private var myview:ActivityStatusViewBinding?=null
    private var counter = 0
    private var user:User?=null
    private var mStorage:FirebaseStorage?=null
    private var mauth:FirebaseAuth? = null
    private var mdatabase:FirebaseDatabase?=null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        myview = ActivityStatusViewBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        mStorage = FirebaseStorage.getInstance()
        mauth = FirebaseAuth.getInstance()
        mdatabase = FirebaseDatabase.getInstance()
        user = intent.getParcelableExtra(StatusViewAdapter.MY_DATA)
        Picasso.get().load(user?.profilepic).placeholder(R.drawable.ic_baseline_image_24).into(myview?.profile)
        myview?.nametxt?.text = user?.username
        updateArraylistItems()
        myview?.miview?.setOnTouchListener { _, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    myview?.progresview?.pause()
                    true
                }

                MotionEvent.ACTION_UP -> {
                    myview?.progresview?.resume()
                    true
                }
                else -> {
                    false
                }
            }
        }
      myview?.backbtn?.setOnClickListener {
          onBackPressed()
      }

    }



    private fun updateArraylistItems() {
        mdatabase?.reference?.child("Status")?.child(user?.userId!!)?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val allitem:ArrayList<String> = ArrayList()
                for(items in snapshot.children){

                    val status = items.getValue(Status::class.java)
                    allitem.add(status?.statusimage!!)

                }
                Log.i("newimages","$allitem")
                Picasso.get().load(allitem[counter]).into(myview?.miview,object :Callback{
                    override fun onSuccess() {
                        setUpMyStatusProgressView(allitem)
                    }

                    override fun onError(e: Exception?) {
                        TODO("Not yet implemented")
                    }

                })

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setUpMyStatusProgressView(allitem: ArrayList<String>) {
          myview?.progresview?.setStoriesListener(setUpMyStatusListner(allitem))
          myview?.progresview?.setStoriesCount(allitem.size)
         myview?.progresview?.setStoryDuration(3000)
        myview?.progresview?.startStories()
    }

    private fun setUpMyStatusListner(allitem: ArrayList<String>):StoriesProgressView.StoriesListener{
        return object :StoriesProgressView.StoriesListener{
            override fun onNext() {

                counter++
                Picasso.get().load(allitem[counter]).into(myview?.miview,object :Callback{
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        TODO("Not yet implemented")
                    }

                })
            }

            override fun onPrev() {
                if(counter>0) {

                    counter--
                    Picasso.get().load(allitem[counter]).into(myview?.miview,object : Callback{
                        override fun onSuccess() {

                            return
                        }

                        override fun onError(e: Exception?) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }

            override fun onComplete() {
              onBackPressed()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        myview?.progresview?.destroy()
    }
}


