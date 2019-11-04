package com.abhishekjnvk.cartav6

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.abhishekjnvk.cartav6.ui.friends.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide()
        Login_Authenticate.setVisibility(View.GONE)
        Login_Authenticate.setOnClickListener {

            signup_layout.setVisibility(View.GONE)
            Login_layout.setVisibility(View.VISIBLE)

            SignUP_Authenticate.setVisibility(View.VISIBLE)
            Login_Authenticate.setVisibility(View.GONE)
        }
        SignUP_Authenticate.setOnClickListener {
            Login_layout.setVisibility(View.GONE)
            signup_layout.setVisibility(View.VISIBLE)

            SignUP_Authenticate.setVisibility(View.GONE)
            Login_Authenticate.setVisibility(View.VISIBLE)

        }
        Login_button_Authenticate.setOnClickListener {
            performlogin()
        }
        Sign_up_button_Authenticate.setOnClickListener {
            registeruser()
        }
    }

    private fun performlogin(){
// start of alert box
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Please Wait")
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        //end of alert box
        val email = email_login.text.toString()
        val password = password_login.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()
            return
        }
        alertDialog.show() // showing alert box
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                alertDialog.hide()     // hiding alert box
                Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                alertDialog.hide()     // hiding alert box
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun registeruser(){
        val email = email_signup.text.toString()
        val password = password_signup.text.toString()
        val name = name_signUP.text.toString()
        if (email.isEmpty() || password.isEmpty()||name.isEmpty()) {
            Toast.makeText(this, "Please fill All Fields.", Toast.LENGTH_SHORT).show()
            return
        }
        // Firebase Authentication to create a pendingRequest with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                // else if successful
                val uid = it.result?.user?.uid
                Log.d("Main", "Successfully created pendingRequest with uid: $uid")
//                uploadImageToFirebaseStorage(uid)
                saveUserToFirebaseDatabase(it.toString())
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to create pendingRequest: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val name = name_signUP.text.toString()
        val email= email_signup.text.toString()
        val bio= "Hello World!"
        val ref = FirebaseDatabase.getInstance().getReference("/users/All/$uid")
        ref.keepSynced(true)
        val user = User(
            uid,name,email,bio,""
        )
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Main", "Finally we saved the pendingRequest to Firebase Database")
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to set value to database: ${it.message}")
            }
    }
}
