package com.abhishekjnvk.cartav6.ui.friends

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.abhishekjnvk.cartav6.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_friend.*
import kotlinx.android.synthetic.main.my_friend_bucket.view.*

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friend, container, false)
        view.apply {
            fetchusers()
            val search_button: Button = view.run { findViewById(R.id.search_friends) }
            search_button.setOnClickListener {
                val intent = Intent(getActivity(),SearchUsers::class.java)
                startActivity(intent)
            }
            val pending_button: Button = view.findViewById(R.id.pending_friends)
            pending_button.setOnClickListener {
                val intent = Intent(getActivity(),pending_request::class.java)
                startActivity(intent)
            }

        }
        return view
    }

    private fun fetchusers() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/friend/$uid")
        val ref2 = FirebaseDatabase.getInstance().getReference("/friend/$uid")
        ref2.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    val uid = FirebaseAuth.getInstance().uid ?: ""
                    if (user != null&& user.uid!=uid) {
                        adapter.add(myfriendItem(user))                                                       //hiding please wait
                        Log.d("NewMessage", "data sent to adapter")
                    }
                }
                result_friend?.adapter = adapter

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("NewMessage","no data found")
            }
        })



        ref.keepSynced(true)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    val uid = FirebaseAuth.getInstance().uid ?: ""
                    if (user != null&& user.uid!=uid) {
                        adapter.add(myfriendItem(user))                                                       //hiding please wait
                        Log.d("NewMessage", "data sent to adapter")
                    }
                }
                result_friend?.adapter = adapter

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("NewMessage","no data found")
            }
        })

    }
}







class myfriendItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_my_friend.text = user.name
        viewHolder.itemView.bio_my_friend.text = user.bio
        val uid = user.uid
//        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.profile_image_all_user)
    }

    override fun getLayout(): Int {
        return R.layout.my_friend_bucket
    }
}

@Parcelize
class User(val uid: String, val name: String, val email: String,val bio:String,val  profileImageUrl:String):
Parcelable
{
    constructor() : this("","","","","")
}