package com.example.safespace.user

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.safespace.R

import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.safespace.entity.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_message_from.view.*
import kotlinx.android.synthetic.main.chat_message_to.view.*
import kotlinx.android.synthetic.main.chat_user_offline.view.*
import kotlinx.android.synthetic.main.chat_user_online.view.*
import kotlinx.android.synthetic.main.fragment_chatroom.view.*
import java.text.SimpleDateFormat
import java.util.*


class ChatroomFragment : Fragment() {

    val TAG = "ChatroomFragment"
    val adapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var receiverId: String
    private lateinit var preferredNickname: String
    private lateinit var receiverName: String
    private lateinit var senderId: String
    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatroom, container, false)


        //Getting adminId, adminName, preferred nickname by user for chat session
        val bundle = arguments
        preferredNickname = bundle?.getString("preferredNickname") ?: ""
        receiverId = bundle?.getString("adminId") ?: ""
        receiverName = bundle?.getString("adminName") ?: ""
        senderId = FirebaseAuth.getInstance().uid?:""


        //Send message button
        view.sendBtn.setOnClickListener {
            Log.d(TAG, "Trying to send message")
            sendMessage(view, receiverId)
        }

        //Setup message listener(to update the chat log)
        setupMessageListener(view)

        //Setup status listener
        setupStatusListener(view)

        // Setup back button
        setupBackButton(view)


        // Display chat person
        adapter.add(UserOnline(receiverName, getCurrentTime()))


        // Setup recycler for chat logs
        view.findViewById<RecyclerView>(R.id.recyclerViewChat).adapter = adapter

        // Must be defined for recycler view to work(can be defined in xml too)
        view.findViewById<RecyclerView>(R.id.recyclerViewChat).layoutManager =
            LinearLayoutManager(activity)

        return view
    }

    //Get current time in 24hour format
    private fun getCurrentTime(): String{
        val sdf = SimpleDateFormat("HH:mm")
        val time24Format = sdf.format(Date())


        return time24Format
    }



    private fun setupMessageListener(view: View) {
        val ref = FirebaseDatabase.getInstance().getReference("/message").orderByChild("senderId")
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val msg = p0.getValue(Message::class.java)
                if(msg!=null){
                    Log.d(TAG,"onchildAdded")
                    if(receiverId.equals(msg.receiverId) && senderId.equals(msg.senderId)){
                        adapter.add(ChatToItem(preferredNickname, msg.messageTxt, msg.time))
                    }
                    else if(senderId.equals(msg.receiverId)){
                        adapter.add(ChatFromItem(receiverName,msg.messageTxt))
                    }
                    view.findViewById<RecyclerView>(R.id.recyclerViewChat).scrollToPosition(view.findViewById<RecyclerView>(R.id.recyclerViewChat).adapter!!.itemCount -1 )
                }



            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.d(TAG, "Changed")
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }



    private fun sendMessage(view: View, receiverId: String) {
        //Getting input chat message
        val senderId = FirebaseAuth.getInstance().uid
        val messageTxt = view.editTextChat.text.toString().trim()


7
        if (messageTxt.isNotEmpty() && senderId != null) {
            // Get current time in 24hour format
            val sdf = SimpleDateFormat("HH:mm")
            val time24Format = sdf.format(Date())



            val ref = FirebaseDatabase.getInstance().getReference("/message").push()
            val chat =Message(ref.key!!, messageTxt, senderId, receiverId, System.currentTimeMillis(),time24Format)

            ref.setValue(chat).addOnSuccessListener {
                Log.d(TAG, "Saved our chat message:${ref.key}")

            }.addOnFailureListener {
                showError("Livechat", "Message cannot be sent. Check your network connection and try again")
            }

            //Clear input chat box
            view.editTextChat.setText("")

            view.editTextChat.requestFocus()
        }


    }

    private fun setupStatusListener(view: View){
        val ref = FirebaseDatabase.getInstance().getReference("/admin").child(receiverId)
        Log.d(TAG, "status check")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val status = p0.value.toString()

            if(status.equals("offline")) {
                adapter.add(UserOffline(receiverName, getCurrentTime()))


                //Disable text
                view.editTextChat.setFocusable(false)
                view.editTextChat.setEnabled(false)
                view.editTextChat.setCursorVisible(false)
                view.editTextChat.setKeyListener(null)

                //Disable send button
                view.sendBtn.setEnabled(false)
                view.sendBtn.background =
                    ContextCompat.getDrawable(view.context, R.drawable.offline_send_button)
            }
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }
    class ChatFromItem(val receiverName: String, val msg: String) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.message_from_content.text = "From Message"
            viewHolder.itemView.message_from_name.text = receiverName
            viewHolder.itemView.message_from_content.text = msg
        }

        override fun getLayout(): Int {
            return R.layout.chat_message_from
        }
    }

    class ChatToItem(val nickname: String, val msg: String, val time: String) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.message_to_content.text = "To Message"
            viewHolder.itemView.message_to_name.text = nickname
            viewHolder.itemView.message_to_content.text = msg
            viewHolder.itemView.message_to_time.text = time
        }

        override fun getLayout(): Int {
            return R.layout.chat_message_to
        }
    }

    class UserOffline(val nickname: String, val time: String) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.offlineMessage.text = "${nickname} has left the chat"
            viewHolder.itemView.offlineTime.text = time
        }

        override fun getLayout(): Int {
            return R.layout.chat_user_offline
        }
    }


    class UserOnline(val nickname: String, val time: String) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.onlineMessage.text = "${nickname} has joined the chat"
            viewHolder.itemView.onlineTime.text = time
        }

        override fun getLayout(): Int {
            return R.layout.chat_user_online
        }
    }


    private fun setupBackButton(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.livechat_toolbar)
        if (SDK_INT >= 21) {
            toolbar.setNavigationOnClickListener(View.OnClickListener {
                resetStatus()
                val fragment = LivechatFragment()
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_host, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            })
        }
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")

    }

    private fun resetStatus(){
        val userId = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users").child(userId).child("status")
        ref.setValue("offline")
    }

    private fun removeChat(){
        val ref = FirebaseDatabase.getInstance().getReference("/message").orderByChild("senderId").equalTo(senderId)
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    it.ref.removeValue()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onStop() {
        Log.d(TAG, "onStop")

        super.onStop()
    }
    override fun onDestroy() {
        Log.d(TAG, "onDestroy")

        removeChat()
        super.onDestroy()

    }
    private fun showError(title:String, msg: String){

        SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(msg)
            .show()
    }


}