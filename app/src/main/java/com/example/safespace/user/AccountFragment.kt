package com.example.safespace.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.safespace.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.safespace.entity.User
class AccountFragment: Fragment(){
    val TAG = "AccountFragment"

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {




       val view = inflater.inflate(R.layout.fragment_account, container,false)

        view.findViewById<Button>(R.id.buttonSignOut).setOnClickListener{
            Log.d(TAG, "button clicked")
            //Sign out current user
            FirebaseAuth.getInstance().signOut()


            //Clear stack
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // obtain logged user id
        val userId = FirebaseAuth.getInstance().currentUser?.uid?:""
        //Retrieve user details from firebase
        val ref = FirebaseDatabase.getInstance().getReference("/users").child(userId)
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                Toast.makeText(activity, "Username:"+user?.name, Toast.LENGTH_LONG).show()


            }


            override fun onCancelled(p0: DatabaseError) {
            }
        })
        return view



    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }
}