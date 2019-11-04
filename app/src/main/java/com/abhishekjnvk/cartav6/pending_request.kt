package com.abhishekjnvk.cartav6

import android.content.Intent.getIntent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import com.abhishekjnvk.cartav6.ui.friends.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_pending_request.*
import kotlinx.android.synthetic.main.pending_friend_bucket.view.*

class  pending_request : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_request)
        fetchusers()
    }


    fun fetchusers() {
        val uid = FirebaseAuth.getInstance().uid!!
        val ref = FirebaseDatabase.getInstance().getReference("/friend_request/$uid")
        ref.keepSynced(true)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    val pendingrequest = it.getValue(pendingRequestClass::class.java)
                    if (pendingrequest != null) {
                        adapter.add(pendingRequestItem(pendingrequest))                                                       //hiding please wait
                        Log.d("NewMessage", "data sent to adapter")
                        no_friend_request_info.visibility= View.INVISIBLE
                    }
                    else{

                    }
                }
                pending_friend_recyclerview?.adapter = adapter

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("NewMessage","no data found")
            }
        })

    }

}


class pendingRequestItem(val pendingRequest: pendingRequestClass): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_pending.text = pendingRequest.name
        viewHolder.itemView.accept_pending.setOnClickListener {
            val friendID = pendingRequest.uid
            accept_request(friendID)
        }
    }

    private fun accept_request(friendID: String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/All/$friendID")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val data= p0.getValue(User::class.java)
                val ref = FirebaseDatabase.getInstance().getReference("/friend/$uid/$friendID")
                ref.setValue(data)
            }
            override fun onCancelled(p0: DatabaseError) {
//                Log.d("profile","no data found")
            }
        }

        )




        val ref3 = FirebaseDatabase.getInstance().getReference("/users/All/$uid")
        ref3.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val data= p0.getValue(User::class.java)
                val ref4 = FirebaseDatabase.getInstance().getReference("/friend/$friendID/$uid")
                ref4.setValue(data)
            }
            override fun onCancelled(p0: DatabaseError) {
//                Log.d("profile","no data found")
            }
        }

        )



        //deleting accepted friend
        val ref2 = FirebaseDatabase.getInstance().getReference("/friend_request/$uid/$friendID")
        ref2.removeValue()

        //reloading page
//        pending_request.fetchusers()
    }

    override fun getLayout(): Int {
        return R.layout.pending_friend_bucket
    }
}



@Parcelize
class pendingRequestClass(val uid: String, val name: String):
    Parcelable
{
    constructor() : this("","")
}