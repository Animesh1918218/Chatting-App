package com.example.whatsappclone

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.whatsappclone.Database.User
import com.example.whatsappclone.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SignUp : AppCompatActivity() {
    private var mdatabase: FirebaseDatabase?=null
    private var mauth:FirebaseAuth?=null
    private var mprogressDialogue:Dialog?=null
    private var myview :ActivitySignUpBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        setUpMyDialogue()
       mauth = FirebaseAuth.getInstance()
        mdatabase = FirebaseDatabase.getInstance()
        myview?.signintxt?.setOnClickListener {
            Intent(this,SignIn::class.java).also {
                startActivity(it)
            }
        }

        myview?.signupbtn?.setOnClickListener {
            if(myview?.emailtxt?.text!!.isNotEmpty()&&myview?.passtxt?.text!!.isNotEmpty()&&myview?.nametxt?.text!!.isNotEmpty()) {
                mprogressDialogue?.show()
                lifecycleScope.launch {
                authprocess()
                }
            }
            else Toast.makeText(this, "Please Fill All Details", Toast.LENGTH_SHORT).show()
                }
    }


    private suspend fun  authprocess(){
        mauth?.createUserWithEmailAndPassword(myview?.emailtxt?.text.toString(),myview?.passtxt?.text.toString())?.addOnCompleteListener {
                task->
            mprogressDialogue?.cancel()
            if(task.isSuccessful){

                val user = User(myview?.nametxt?.text.toString(),myview?.emailtxt?.text.toString(),myview?.passtxt?.text.toString())
                val id = task.result.user!!.uid
                mdatabase?.reference?.child("Users")?.child(id)?.setValue(user)
                Toast.makeText(this, "User Created Successfully", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun setUpMyDialogue(){
        mprogressDialogue = Dialog(this)
        mprogressDialogue?.setContentView(R.layout.progressbar)
        mprogressDialogue?.setCancelable(false)
    }
}