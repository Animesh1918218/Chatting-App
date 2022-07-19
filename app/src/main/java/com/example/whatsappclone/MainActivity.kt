package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.whatsappclone.Adapters.FragmentsPagersAdapter
import com.example.whatsappclone.Fragments.ChatsFragment
import com.example.whatsappclone.Fragments.StatusFragment
import com.example.whatsappclone.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private var myview:ActivityMainBinding?=null
    private var mauth:FirebaseAuth?=null
    private var myfragmentadapter:FragmentsPagersAdapter?=null
    private var ref:DatabaseReference?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivityMainBinding.inflate(layoutInflater)
        mauth = FirebaseAuth.getInstance()
        setContentView(myview?.root)
        setSupportActionBar(myview?.mytoolbar)
        ref =  FirebaseDatabase.getInstance().reference.child("Users").child(mauth?.uid!!).child("userStatus")
        setUpMytabs()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settingsbtn->{
                Intent(this,ProfileActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.logoutbtn->{
                mauth?.signOut()
                Intent(this,SignIn::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
            R.id.grpcht->{
                Intent(this,GroupChat::class.java).also {
                    startActivity(it)
                }
            }
        }

        return true
    }


    private fun setUpMytabs(){
        myfragmentadapter = FragmentsPagersAdapter(supportFragmentManager,lifecycle)
        myfragmentadapter?.addFragments(ChatsFragment())
        myfragmentadapter?.addFragments(StatusFragment())
        myview?.mviewpager?.adapter = myfragmentadapter
        myview?.mviewpager?.setPageTransformer(ZoomOutPageTransformer())
        TabLayoutMediator(myview?.mtblayout!!,myview?.mviewpager!!){
            tab,position->


        }.attach()

        myview?.mtblayout?.getTabAt(0)?.text = "CHATS"
        myview?.mtblayout?.getTabAt(1)?.text = "STATUS"

    }

    override fun onDestroy() {

        ref?.setValue("Offline")
        super.onDestroy()


    }
}