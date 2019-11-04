package com.abhishekjnvk.cartav6

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener




object functions {

    private var flag :Int = 0

    fun myFun(context: Context, message: String) {
        context.toast(message)
    }

    fun Context.toast(message: CharSequence) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    fun isAlreadyFriend(myUid:String,friendUid:String) {
        val ref = FirebaseDatabase.getInstance().getReference("friend/$myUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var flag = 0
                if (snapshot.hasChild("$friendUid")) {
                    Log.d("TestingFunction","Value of flag is found to be $flag || Level 2")
                }
                else {
                    flag = 1
                    Log.d("TestingFunction","Value of flag is found to be $flag || Level 3")
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }

        })


//
        Log.d("TestingFunction","Value of flag is found to be $flag || Level 4")
//        if (flag==1)
//            return true
//
//        return false
    }

    fun isRequestSent(myUid:String,friendUid:String): Boolean {
        var flag = false
        val ref = FirebaseDatabase.getInstance().getReference("/friend_request/$friendUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("$myUid")) {
                    flag = true
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        });
        if (flag==true)
            return true

            return false
    }


    fun amIBlocked(myUid:String,friendUid:String): Boolean {
        var flag = false
        val ref = FirebaseDatabase.getInstance().getReference("/blocked/$friendUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("$myUid")) {
                    flag = true
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        });
        if (flag==true)
            return true

        return false
    }






    //function for testing network
    @Suppress("DEPRECATION")
    fun NetworkTest(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return !(networkInfo == null || !networkInfo.isConnected)
    }

}
