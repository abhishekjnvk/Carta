package com.abhishekjnvk.cartav6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.abhishekjnvk.cartav6.ui.friends.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_search_users.*
import kotlinx.android.synthetic.main.searched_friend_bucket.view.*

class SearchUsers : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_users)
        supportActionBar?.title = "Search"

        info_filter.setOnClickListener {
            Toast.makeText(this, "Filter your result by name, username or email", Toast.LENGTH_LONG).show()
            val uid = FirebaseAuth.getInstance().uid!!
            val uid2 = FirebaseAuth.getInstance().uid!!
            val test = functions.isAlreadyFriend(uid,uid2)
            Log.d("TestingFunction","Value of test in SearchUser is $test || Final")
        }
        go_search.setOnClickListener {
            search_friend()
            Log.d("NewMessage","Done")
        }

    }

    private fun search_friend() {
        val search_term = name_search.text.toString()
        if (search_term.isEmpty())
            return
        val ref = FirebaseDatabase.getInstance().getReference("/users/All")
        ref
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val adapter = GroupAdapter<ViewHolder>()
                    p0.children.forEach {
                        Log.d("NewMessage","$p0")
                        val user = it.getValue(User::class.java)
                        if (user != null && user.name.toLowerCase().contains(search_term.toLowerCase())) {
                            adapter.add(SearchedfriendItem(user))                                                       //hiding please wait
                            Log.d("NewMessage", "data sent to adapter")
                        }
                    }
                    searched_user?.adapter = adapter

                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("NewMessage","no data found")
                }
            })

    }
}



class SearchedfriendItem(val user: User): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_all_users.text = user.name
        viewHolder.itemView.bio_all_users.text = user.bio
        val searcheduid = user.uid
        val uid = FirebaseAuth.getInstance().uid


        if (uid==searcheduid)                                       //hiding addfriend button for own account
            viewHolder.itemView.add_friend.visibility= View.INVISIBLE


        viewHolder.itemView.add_friend.setOnClickListener {
            send_request(searcheduid)
            viewHolder.itemView.add_friend.visibility= View.INVISIBLE
        }
//        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.profile_image_all_user)
    }

    private fun send_request(friendId: String) {
        val uid = FirebaseAuth.getInstance().uid
        if (uid==friendId)
            return
        val ref2 = FirebaseDatabase.getInstance().getReference("/users/All/$uid")
        ref2.keepSynced(true)
        ref2.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Log.d("NewMessage",p0.toString())
                val name = p0.child("name").getValue(String::class.java)
                val bio = p0.child("bio").getValue(String::class.java)
                val ref = FirebaseDatabase.getInstance().getReference("/friend_request/$friendId/$uid")
                ref.keepSynced(true)
                ref.child("uid").setValue(uid)
                ref.child("name").setValue(name)
                ref.child("bio").setValue(bio)
                Log.d("NewMessage","Request Sent")
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d("profile","no data found")
            }
        }
        )
    }

    override fun getLayout(): Int {
        return R.layout.searched_friend_bucket
    }
}