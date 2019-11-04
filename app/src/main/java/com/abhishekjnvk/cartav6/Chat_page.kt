package com.abhishekjnvk.cartav6

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.abhishekjnvk.cartav6.ui.friends.User
import com.abhishekjnvk.cartav6.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.image_message_bucket.view.*
import kotlinx.android.synthetic.main.text_message_bucket.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

class Chat_page : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val User_detail = intent.getParcelableExtra<User>(HomeFragment.user_key)
        supportActionBar?.title = User_detail.name

        listenForMessages()
        send_message_chat.setOnClickListener {
            send_message()
            message_content_chat.text.clear()
        }
    }

    val adapter = GroupAdapter<ViewHolder>()

//    private fun listenForMessages2() {
//        val User_detail = intent.getParcelableExtra<User>(HomeFragment.user_key)
//        val uid = FirebaseAuth.getInstance().uid
//        val friendUid = User_detail.uid
//        val ref = FirebaseDatabase.getInstance().getReference("/Chat/$uid/$friendUid")
//        ref.keepSynced(true)
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                val adapter = GroupAdapter<ViewHolder>()
//                p0.children.forEach {
//                    val message = it.getValue(MessageClass::class.java)
//                    if (message != null) {
//                        adapter.add(MessageItem(message))                                                       //hiding please wait
//                        Log.d("NewMessage", "data sent to adapter")
//                        chat_chat_page.scrollToPosition(adapter.itemCount - 1)
//                    }
//                }
//                chat_chat_page?.adapter = adapter
//
//            }
//            override fun onCancelled(p0: DatabaseError) {
//                Log.d("NewMessage","no data found")
//            }
//        })
//    }

    val latestMessagesMap = HashMap<String, MessageClass>()
    private fun refreshRecyclerViewMessages() {
        adapter.clear()
//        latestMessagesMap.values.forEach {
//            adapter.add(MessageItem(it))
//        }
        listenForMessages()
    }

    private fun listenForMessages() {
        val User_detail = intent.getParcelableExtra<User>(HomeFragment.user_key)
        val uid = FirebaseAuth.getInstance().uid
        val friendUid = User_detail.uid
        val ref = FirebaseDatabase.getInstance().getReference("/Chat/$uid/$friendUid")
            ref.keepSynced(true)
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val message = p0.getValue(MessageClass::class.java) ?: return
                latestMessagesMap[p0.key!!] = message
//                refreshRecyclerViewMessages()
                    if (message != null) {
                        adapter.add(MessageItem(message))                                                       //hiding please wait
                        Log.d("NewMessage", "data sent to adapter")
                        chat_chat_page.scrollToPosition(adapter.itemCount - 1)
                }
                chat_chat_page?.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                val message = p0.getValue(MessageClass::class.java) ?: return
                latestMessagesMap[p0.key!!] = message
                refreshRecyclerViewMessages()
                if (message != null) {
                    adapter.add(MessageItem(message))                                                       //hiding please wait
                    Log.d("NewMessage", "data sent to adapter")
                    chat_chat_page.scrollToPosition(adapter.itemCount - 1)
                }
                chat_chat_page?.adapter = adapter

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })

    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun send_message(){
        val User_detail = intent.getParcelableExtra<User>(HomeFragment.user_key)
//        val uid = FirebaseAuth.getInstance().uid
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val friendUid = User_detail.uid
        val messagecontent = message_content_chat.text.toString()

        val current = LocalDateTime.now()
        val formatter = ofPattern("dd/mm/yyyy HH:MM")
        val time = current.format(formatter)

        if (messagecontent.isEmpty())
            return
        val Message = MessageClass(uid,friendUid,time,messagecontent)
        val ref = FirebaseDatabase.getInstance().getReference("/Chat/$friendUid/$uid").push()
        val ref2 = FirebaseDatabase.getInstance().getReference("/Chat/$uid/$friendUid").push()
        ref.setValue(Message)
        ref2.setValue(Message)
        chat_chat_page.scrollToPosition(adapter.itemCount - 1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profile_chat->{
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
            }
            R.id.delete_chat->{
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
            }
            R.id.block_chat->{
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
            }
            R.id.report_chat->{
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}




class MessageItem(val message: MessageClass): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_content.text=message.messageContent
        viewHolder.itemView.message_time.text=message.time

//        viewHolder.itemView.add_remove.setOnClickListener {
//            Log.d("NewMessage","$uid")
//        }

//        Picasso.get().load(pendingRequest.profileImageUrl).into(viewHolder.itemView.profile_image_all_user)
    }
    override fun getLayout(): Int {
        return R.layout.text_message_bucket
    }
}

@Parcelize
class MessageClass(val toId: String, val fromID: String, val time: String,val messageContent:String):
    Parcelable
{
    constructor() : this("","","","")
}


