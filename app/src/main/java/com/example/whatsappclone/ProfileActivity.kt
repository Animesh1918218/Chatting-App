package com.example.whatsappclone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.whatsappclone.Database.User
import com.example.whatsappclone.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class ProfileActivity : AppCompatActivity() {
    private var myview:ActivityProfileBinding?=null
    private var mdatabase:FirebaseDatabase? = null
    private var mStorage:FirebaseStorage?=null
    private var activitylauncherForImages:ActivityResultLauncher<Intent>?=null
    private var activityLauncherForPemission:ActivityResultLauncher<String>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        setSupportActionBar(myview?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        myview?.toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        setUpResultLauncherForPermissions()
        setUpActivityResultLuncherForImages()
        mdatabase = FirebaseDatabase.getInstance()
        mStorage = FirebaseStorage.getInstance()
        updateProfilePicture()
        myview?.flbtn?.setOnClickListener {
            if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                    activitylauncherForImages?.launch(it)
                }
            }
            else activityLauncherForPemission?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun setUpResultLauncherForPermissions() {
        activityLauncherForPemission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isgranted->
            if(isgranted){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(this, "Grant Permission from settings", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpActivityResultLuncherForImages() {
        activitylauncherForImages = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            if(result.resultCode== RESULT_OK && result.data!=null){
                myview?.myimagebtn?.setImageURI(result?.data?.data)
                val mreference=mStorage?.reference?.child("ProfilePictures")?.child(FirebaseAuth.getInstance().currentUser?.uid!!)

                  mreference?.putFile(result?.data?.data!!)?.addOnSuccessListener {
                      _->
                     mreference.downloadUrl.addOnSuccessListener {
                         uri->
                         mdatabase?.reference?.child("Users")?.child(FirebaseAuth.getInstance().currentUser?.uid!!)?.child("profilepic")?.setValue(uri.toString())
                     }
                  }

            }
            else Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun checkPermission(permission:String):Boolean{
        return ContextCompat.checkSelfPermission(this,permission) ==PackageManager.PERMISSION_GRANTED
    }



    private fun updateProfilePicture(){
        mdatabase?.reference?.child("Users")?.child(FirebaseAuth.getInstance().uid!!)?.addValueEventListener(
            object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                          val user = snapshot.getValue(User::class.java)
                          if(user?.profilepic!=null)
                              Picasso.get().load(Uri.parse(user.profilepic)).into(myview?.myimagebtn)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
        )
    }
}