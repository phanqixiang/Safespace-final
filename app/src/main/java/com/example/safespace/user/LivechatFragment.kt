package com.example.safespace.user


import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.safespace.R
import java.util.*
import android.os.CountDownTimer
import cn.pedant.SweetAlert.SweetAlertDialog

import android.graphics.Color
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_livechat.view.*


class LivechatFragment : Fragment() {

    val TAG = "LivechatFragment"
    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setupActionBar("Livechat")
    }


    private fun setupActionBar(title: String) {
        val mractionbar =
            activity!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.mr_toolbar)
        (activity as AppCompatActivity).setSupportActionBar(mractionbar)

        setHasOptionsMenu(true)


        activity!!.setTitle(title)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_livechat, container, false)





        view.findViewById<Button>(R.id.chatBtn).setOnClickListener {
            val nickname = view.findViewById<EditText>(R.id.editTextNickname).text.toString().trim()
            if (nickname.length < 3 || nickname.length > 14)
                showError("Chat Now", "Nickname must consist of 3 - 14 characters")
            else {


                val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
                pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                pDialog.titleText = "Loading"
                pDialog.setCancelable(false)
                pDialog.show()

                val ref = FirebaseDatabase.getInstance().getReference("/admin")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    var adminFound = false
                    override fun onDataChange(p0: DataSnapshot) {

                        p0.children.forEach {
                            val adminName = it.child("name").value.toString()
                            val adminId = it.child("adminId").value.toString()
                            val status = it.child("status").value.toString()

                            if (status.equals("ready")) {
                                val userId = FirebaseAuth.getInstance().uid?:""
                                val ref1 = FirebaseDatabase.getInstance().getReference("users/").child(userId).child("status")
                                ref1.setValue(adminId)

                                Log.d(TAG, "Status set:${adminId}")
                                val ref2 = FirebaseDatabase.getInstance().getReference("users/").child(userId).child("nickname")
                                ref2.setValue(nickname)


                                adminFound = true

                                Log.d(TAG, "Online admin name:${adminName}")
                                Log.d(TAG, "Nickname:${nickname}")
                                // Preparing admin details to be transferred to chatroom
                                val bundle = Bundle()
                                bundle.putString(
                                    "preferredNickname",
                                    nickname
                                )
                                bundle.putString("adminId", adminId)
                                bundle.putString("adminName", adminName)


                                //Stop loading animation
                                pDialog.dismissWithAnimation()


                                // Entering to chatroom
                                val fragment = ChatroomFragment()
                                fragment.arguments = bundle
                                activity!!.supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_host, fragment)

                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit()

                                return@forEach
                            }

                        }

                        //Stop loading animation
                        if(!adminFound){
                            pDialog.dismissWithAnimation()
                            SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Livechat")
                                .setContentText("Chat Operator is not available at the moment. Please try again later")
                                .show()
                        }



                    }

                    override fun onCancelled(p0: DatabaseError) {
                        Log.d(TAG, "Admin cannot be found")
                    }
                })


            }


        }





        return view
    }


    private fun countDownSearch() {
        val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Searching"
        pDialog.setCancelable(false)
        pDialog.show()

        object : CountDownTimer(5000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG, "Seconds until finished:" + millisUntilFinished / 1000)
                Log.d(TAG, "Hello")

            }

            override fun onFinish() {
                Log.d(TAG, "Done")
                pDialog.dismissWithAnimation()
            }
        }.start()

    }

    private fun searchTimer() {
        val timer = Timer()

        Log.d(TAG, "clicked")
        //Set the schedule function
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    Log.d(TAG, "Hello")
                }
            },
            0, 1000
        )

        Log.d(TAG, "done")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    private fun showError(title: String, msg: String) {
        SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(msg)
            .show()
    }


}


