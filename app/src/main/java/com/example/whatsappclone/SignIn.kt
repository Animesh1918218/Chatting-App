package com.example.whatsappclone

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.whatsappclone.Database.User
import com.example.whatsappclone.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class SignIn : AppCompatActivity() {
    private var myview:ActivitySignInBinding?=null
    private var mauth:FirebaseAuth?=null
    private var mprogressDialogue:Dialog?=null
    private var mdatabase:FirebaseDatabase?=null
    private var googlesigninclient:GoogleSignInClient?=null
    private var activityForGoogleSignIn:ActivityResultLauncher<Intent>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        mauth = FirebaseAuth.getInstance()
        mdatabase =  FirebaseDatabase.getInstance()
        setUpDialogue()
        myview?.cracctxt?.setOnClickListener {
            Intent(this,SignUp::class.java).also {
                startActivity(it)
            }
        }
        setUpMyGoogleSignInResultLauncher()
        setUpGoogleAuth()
        myview?.signupbtn?.setOnClickListener {
            val email = myview?.emailtxt?.text.toString()
            val pass = myview?.passtxt?.text.toString()
            if(email.isNotEmpty()&&pass.isNotEmpty()) {
                mprogressDialogue?.show()
                lifecycleScope.launch {
                    authProcess(email, pass)
                }
            }
            else Toast.makeText(this, "Please Fill All details", Toast.LENGTH_SHORT).show()
        }

        if(mauth?.currentUser!=null){
            Intent(this,MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        myview?.googlebtn?.setOnClickListener {
            mprogressDialogue?.show()
                   signIn()
        }
    }






    private  fun authProcess(email:String,pass:String){
        mauth?.signInWithEmailAndPassword(email,pass)?.addOnCompleteListener {
            task->
            mprogressDialogue?.cancel()
            if(task.isSuccessful){
                Intent(baseContext,MainActivity::class.java).also {
                    startActivity(intent)
                    finish()
                }

            }
            else Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setUpDialogue(){
        mprogressDialogue = Dialog(this)
        mprogressDialogue?.setContentView(R.layout.progressbar)

        mprogressDialogue?.setCancelable(false)
    }



    private fun setUpGoogleAuth(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()
        googlesigninclient = GoogleSignIn.getClient(this,gso)

    }

    private fun setUpMyGoogleSignInResultLauncher(){
        activityForGoogleSignIn = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            activity->
            if(activity.resultCode== RESULT_OK&&activity.data!=null){
                mprogressDialogue?.cancel()
                val task = GoogleSignIn.getSignedInAccountFromIntent(activity.data)
                try {

                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    mprogressDialogue?.show()
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Toast.makeText(this, "Sign in Failed", Toast.LENGTH_SHORT).show()
                }
            }
            else Toast.makeText(this, "Sign in Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
             val credential = GoogleAuthProvider.getCredential(idToken,null)

        mauth?.signInWithCredential(credential)?.addOnCompleteListener {
            task->
            mprogressDialogue?.cancel()
            if(task.isSuccessful){
                val user = mauth?.currentUser
                val muser = User()
                muser.emailid = user?.email
                muser.username = user?.displayName
                muser.profilepic = user?.photoUrl.toString()
                mdatabase?.reference?.child("Users")?.child(user?.uid!!)?.setValue(muser)
                Intent(this,MainActivity::class.java).also {
                    startActivity(it)
                    Toast.makeText(this, "Sign in with google", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun signIn() {
        val signInIntent = googlesigninclient?.signInIntent
        activityForGoogleSignIn?.launch(signInIntent)

    }



}