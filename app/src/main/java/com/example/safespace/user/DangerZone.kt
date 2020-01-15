package com.example.safespace.user

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.safespace.R
import com.example.safespace.entity.dangerArea
import com.example.safespace.user.HomeActivity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_maps.*

class DangerZone : AppCompatActivity(), OnMapReadyCallback {

    lateinit var areaList: MutableList<dangerArea>

    val ref = FirebaseDatabase.getInstance().getReference("DangerArea")

    private lateinit var mMap: GoogleMap
    private lateinit var lalongTude: LatLng

    private var TTarea : dangerArea? = null

    private var locationListener: LocationListener? = null
    private var locationManager: LocationManager? = null

    private val MIN_TIME: Long = 1000
    private val MIN_DIST: Long = 5
    private var mark: Marker? = null
    private var i= 1

    private var latLng: LatLng? = null

    lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        areaList = mutableListOf()
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        var mapFragment = SupportMapFragment.newInstance()
        ft.replace(R.id.dangerMap,mapFragment).commit()
        mapFragment.getMapAsync(this)

        returnFRMmap.setOnClickListener{
            val test = Intent(this, HomeActivity::class.java)
            startActivity(test)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        ref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){

                    for(h in p0.children){
                        val tarea = h.getValue(dangerArea::class.java)
                        areaList.add(tarea!!)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })



        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                mMap.clear()
                if (mark != null) {
                    mark!!.remove()
                }

                for(Tarea in areaList) {
                    val target = Location("")
                    target.latitude = Tarea.centerLa
                    target.longitude = Tarea.centerLo
                    val distanceInMeters = target.distanceTo(location)
                    val isWithin = distanceInMeters < Tarea.radius
                    val isWithin5km = distanceInMeters < 5000

                    if(isWithin5km) {
                        lalongTude = LatLng(Tarea.centerLa, Tarea.centerLo)

                        mMap.addCircle(
                            CircleOptions().center(lalongTude).radius(Tarea.radius).fillColor(
                                Color.argb(
                                    50,
                                    255,
                                    0,
                                    0
                                )
                            ).strokeColor(Color.BLACK)
                        )
                    }

                    if(TTarea != null) {
                        if (TTarea!!.centerLa == Tarea.centerLa && TTarea!!.centerLo == Tarea.centerLo && i != 1)
                        {
                            if(isWithin) {
                                break
                            }else{
                                TTarea = null
                                i = i - i + 1
                            }
                        }
                    }

                    if (isWithin) {
                        if(i == 1) {
                            warning(Tarea.desc)
                            TTarea = Tarea
                            i = i + 1
                            break
                        }
                    }
                }

                try {
                    latLng = LatLng(location.latitude, location.longitude)
                    mark = mMap.addMarker(MarkerOptions().position(latLng!!).title("Marker"))
                    val cameraPosition =
                        CameraPosition.Builder().target(latLng).zoom(10f).bearing(location.bearing)
                            .build()
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }

                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            location.latitude,
                            location.longitude
                        ), 13f
                    )
                )

                val cameraPosition = CameraPosition.Builder().target(latLng).zoom(17f).build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        try {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME,
                MIN_DIST.toFloat(),
                locationListener!!
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

    }

    fun warning(desc:String) {
        val builder:AlertDialog.Builder = AlertDialog.Builder(this);
        builder.setTitle("Beware").setMessage("There are "+desc+" happened around here!!").setPositiveButton("ok",null).show();
        vibrator.vibrate(500)
    }
}