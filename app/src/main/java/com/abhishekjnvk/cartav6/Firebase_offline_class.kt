package com.abhishekjnvk.cartav6



import android.app.Application
import android.content.Context
import com.google.firebase.database.FirebaseDatabase

class Firebase_offline_class : Application() {
    override fun onCreate() {
        super.onCreate()
        //Firebase Offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    }

}