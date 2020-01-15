package com.example.safespace.user

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.safespace.R
import com.example.safespace.entity.SosLocation
import com.google.android.gms.location.*
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_sos.view.*
import java.time.LocalTime
import kotlin.properties.Delegates

/**
 * A simple [Fragment] subclass.
 */
class SosFragment : Fragment() {

    private lateinit var fused: FusedLocationProviderClient
    private var la :Double = 0.000
    private var lo :Double = 0.000
    private var i:Int = 0
    val TAG = "SosFragment"

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {


        val view: View = inflater.inflate(R.layout.fragment_sos, container, false)

        fused = LocationServices.getFusedLocationProviderClient(activity!!)
        getLocation()

        view.SOSbutton.setOnClickListener {

            if (i == 0) {
                fused = LocationServices.getFusedLocationProviderClient(activity!!)
                getLocation();
                val buton = view.findViewById(R.id.SOSbutton) as Button

                val ref = FirebaseDatabase.getInstance().getReference("SOSLocation")
                val LID = ref.push().key

                val sos = SosLocation(la, lo)

                ref.child(LID.toString()).setValue(sos).addOnSuccessListener {
                    val build = AlertDialog.Builder(context)
                    build.setTitle("Plese be Strong")
                    build.setMessage("We sent your sos signal out already. Please stay in somewhere safe and wait for help")
                    build.setPositiveButton("yes", null);
                    build.show()
                }

                buton.setBackgroundColor(Color.GREEN)
                buton.setText("We are coming")
                i++
            }else{
                val build = AlertDialog.Builder(context)
                build.setTitle("Please be patient")
                build.setMessage("We sent your sos signal out already. Please stay in somewhere safe and wait for help")
                build.setPositiveButton("ok", null);
                build.show()
            }
        }

        view.dangerZoneBTN.setOnClickListener{
            val test = Intent(activity, DangerZone::class.java)
            startActivity(test)
        }
        // Return the fragment view/layout
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")

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

    private fun getLocation() {
        fused.lastLocation.addOnSuccessListener { location: Location ->
            la = location.latitude
            lo = location.longitude
        }
    }

}
