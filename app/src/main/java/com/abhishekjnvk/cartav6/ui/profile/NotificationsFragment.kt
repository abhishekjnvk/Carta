package com.abhishekjnvk.cartav6.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.abhishekjnvk.cartav6.R
import com.abhishekjnvk.cartav6.SignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                          savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_profile, container, false)
    view.apply {
        FetchCurrentUserData()
        sign_out_profile.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(view.getContext())
            alertDialogBuilder.setMessage("Really Want To Logout?")
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setPositiveButton("Ok",
                DialogInterface.OnClickListener { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(view.getContext(), SignIn::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
//            functions.myFun(view.getContext(), "")


        }
    }

    return view
}


    public fun FetchCurrentUserData() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/All/$uid")
        ref.keepSynced(true)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var NameOfUser = "   "
                var email = "    "
                var bio = "    "
                NameOfUser = p0.child("name").getValue(String::class.java)!!
                email = p0.child("email").getValue(String::class.java)!!
                bio = p0.child("bio").getValue(String::class.java)!!
                my_name.text=NameOfUser
                my_bio.text=bio
                my_email.text=email
                show_hide_profile.isChecked
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d("profile","no data found")
            }
        }
        )
    }
}