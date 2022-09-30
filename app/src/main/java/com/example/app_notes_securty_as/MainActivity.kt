package com.example.app_notes_securty_as

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.app_notes_securty_as.databinding.ActivityMainBinding
import com.example.app_notes_securty_as.ui.home.HomeActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, OnSuccessListener<Location> {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var REQUEST_CODE: Int = 100
    private var latitude: String? = null
    private var longitude: String? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            Log.d(TAG, "Current USER => ${currentUser.uid}")
        }
    }
    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE){
            if (grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation()
            }else{
                Toast.makeText(this, "Need Permission", Toast.LENGTH_LONG)
            }
        }

    }
    override fun onSuccess(location: Location?) {
        if (location != null){
            var geocoder = Geocoder(this, Locale.getDefault())
            var address : List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            latitude = "Latitude: ${address.get(0).latitude}"
            longitude = "Longitude: ${address.get(0).longitude}"

            Log.d(TAG, "GPS => $latitude LONGITUDE: $longitude" )

        }
    }
    private fun getLastLocation(){
        val accessLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(accessLocation == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this)
        }else{
            askPermition()
        }
    }
    private fun askPermition() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
    }
    private fun saveSharedPreference() {
        val sp = this.getSharedPreferences("mainPrefs", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("latitude", latitude)
        editor.putString("longitude", longitude)
        editor.commit()
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
    }
    /*

    private fun getSharedPreference(){
        val sp = this.getSharedPreferences("mainPrefs", MODE_PRIVATE)
        spLatitude.text = "${sp.getString("latitude", "")}"
        spLongitude.text = "${sp.getString("longitude", "")}"

    }*/




}