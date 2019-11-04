package com.abhishekjnvk.cartav6.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.abhishekjnvk.cartav6.Chat_page
import com.abhishekjnvk.cartav6.R
import com.abhishekjnvk.cartav6.ui.friends.User
import com.abhishekjnvk.cartav6.ui.friends.myfriendItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var notificationsViewModel: HomeViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.apply {
            fetch_friends()
        }

        return view
    }

    companion object {
        val user_key = "user_key"
    }
    private fun fetch_friends() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/friend/$uid")
        ref.keepSynced(true)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    val uid = FirebaseAuth.getInstance().uid ?: ""
                    if (user != null&& user.uid!=uid) {
                        adapter.add(myfriendItem(user))
                        Log.d("NewMessage", "data sent to adapter")
                    }
                }
                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as myfriendItem
                    val intent = Intent(view.context, Chat_page::class.java)
                    intent.putExtra(user_key, userItem.user)
                    startActivity(intent)
                }
                all_chats_home?.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d("NewMessage","no data found")
            }
        })
    }
}