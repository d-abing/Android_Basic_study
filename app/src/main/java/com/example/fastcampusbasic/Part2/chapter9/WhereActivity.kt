package com.example.fastcampusbasic.Part2.chapter9

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ActivityWhereBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.play.integrity.internal.m
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.naver.maps.map.NaverMap

class WhereActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener {
    private lateinit var binding: ActivityWhereBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentUid: String

    private var trackingPersonId: String = ""
    private val markerMap = hashMapOf<String, Marker>()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                getCurrentLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getCurrentLocation()
            }
            else -> {
                Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            // 새로 요청된 위치 정보
            for (location in locationResult.locations) {

                Log.d("WhereActivity", "${location.latitude}, ${location.longitude}")
                val locationMap = mutableMapOf<String, Any>()
                locationMap["latitude"] = location.latitude
                locationMap["longitude"] = location.longitude
                Firebase.database.reference.child("Person").child(currentUid).updateChildren(locationMap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhereBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUid = Firebase.auth.currentUser?.uid.orEmpty()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_f) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()
        setupEmojiAnimationView()
        setupCurrentLocationView()
        setupFirebaseDatabase()
    }

    override fun onResume() {
        super.onResume()

        getCurrentLocation()
    }

    override fun onPause() {
        super.onPause()

        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun getCurrentLocation() {
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 5 * 1000)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        // 위치 권한이 있는 상태
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        moveLastLocation()
    }
    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }


    private fun setupEmojiAnimationView() {
        binding.emojiLav.setOnClickListener {
            if (trackingPersonId != "" && trackingPersonId != currentUid) {
                val lastEmoji = mutableMapOf<String, Any>()
                lastEmoji["type"] = "love"
                lastEmoji["lastModifier"] = System.currentTimeMillis()
                Firebase.database.reference.child("Emoji").child(trackingPersonId).updateChildren(lastEmoji)
                binding.emojiLav.playAnimation()

                binding.dummyLav.animate()
                    .scaleX(5f)
                    .scaleY(5f)
                    .alpha(0f)
                    .withStartAction {
                        binding.dummyLav.scaleX = 1f
                        binding.dummyLav.scaleY = 1f
                        binding.dummyLav.alpha = 1f
                    }
                    .withEndAction {
                        binding.dummyLav.scaleX = 1f
                        binding.dummyLav.scaleY = 1f
                        binding.dummyLav.alpha = 1f
                    }
                    .start()
            }

            binding.emojiLav.speed = 2f
            binding.centerLav.speed = 2f
        }
    }


    private fun setupCurrentLocationView() {
        binding.currentLocationFab.setOnClickListener {
            trackingPersonId = ""
            moveLastLocation()
        }
    }

    private fun moveLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 17f)
            )
        }
    }



    private fun setupFirebaseDatabase() {
        Firebase.database.reference.child("Person")
            .addChildEventListener(object: ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val person = snapshot.getValue(Person::class.java) ?: return
                    val uid = person.uid ?: return

                    if (markerMap[uid] == null) {
                        markerMap[uid] = makeNewMarker(person, uid) ?: return
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val person = snapshot.getValue(Person::class.java) ?: return
                    val uid = person.uid ?: return

                    if (markerMap[uid] == null) {
                        markerMap[uid] = makeNewMarker(person, uid) ?: return
                    } else {
                        markerMap[uid]?.position = LatLng(person.latitude ?: 0.0, person.longitude ?: 0.0)
                    }

                    if (uid == trackingPersonId) {
                        googleMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder()
                                    .target(LatLng(person.latitude ?: 0.0, person.longitude ?: 0.0))
                                    .zoom(17f)
                                    .build()
                            )
                        )
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}

            })

        Firebase.database.reference.child("Emoji").child(Firebase.auth.currentUser?.uid ?: "")
            .addChildEventListener(object: ChildEventListener {

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                    binding.centerLav.playAnimation()
                    binding.centerLav.animate()
                        .scaleX(3f)
                        .scaleY(3f)
                        .alpha(0.3f)
                        .setDuration(binding.centerLav.duration / 2)
                        .withEndAction {
                            binding.centerLav.scaleX = 0f
                            binding.centerLav.scaleY = 0f
                            binding.centerLav.alpha = 1f
                        }
                        .start()
                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun makeNewMarker(person: Person, uid: String): Marker? {

        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(person.latitude ?: 0.0, person.longitude ?: 0.0))
                .title(person.name.orEmpty())
        ) ?: return null

        marker.tag = uid

        Glide.with(this).asBitmap()
            .load(person.profilePhoto)
            .transform(RoundedCorners(60))
            .override(180)
            .listener( object: RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.let {
                        runOnUiThread {
                            marker.setIcon(
                                BitmapDescriptorFactory.fromBitmap(it)
                            )
                        }
                    }

                    return true
                }
            }).submit()


        return marker
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.setMaxZoomPreference(20.0f)
        googleMap.setMinZoomPreference(10.0f)

        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnMapClickListener {
            trackingPersonId = ""
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        trackingPersonId = marker.tag as? String ?: ""

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.emojiBsl)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        return false
    }
}