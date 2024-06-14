package com.example.fastcampusbasic.Part2.chapter7

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ActivityWeatherBinding
import com.example.fastcampusbasic.databinding.ItemForecastBinding
import com.google.android.gms.location.LocationServices
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                updateLocation()
            } else -> {
                Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
                finish()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION))

    }

    private fun updateLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if ( ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            Thread {
                try {
                    val addressList = Geocoder(this, Locale.KOREA).getFromLocation(
                        it.latitude,
                        it.longitude,
                        1
                    )
                    runOnUiThread {
                        binding.locationTv.text = addressList?.get(0)?.thoroughfare.orEmpty()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }.start()

            WeatherRepository.getVillageForecast(
                longitude = it.longitude,
                latitude = it.latitude,
                successCallback = { list ->
                    val currentForecast = list.first()

                    binding.temperatureTv.text = getString(R.string.temperature_text, currentForecast.temperature)
                    binding.skyTv.text = currentForecast.weather
                    binding.precipitationTv.text = getString(R.string.precipitation_text, currentForecast.precipitation)

                    binding.childForecastL.apply {

                        list.forEachIndexed { index, forecast ->
                            if (index == 0) { return@forEachIndexed }

                            val itemView = ItemForecastBinding.inflate(layoutInflater)

                            val hours = forecast.fcstTime.substring(0, 2)
                            val minutes = forecast.fcstTime.substring(2, 4)

                            itemView.timeTv.text = "$hours:$minutes"
                            itemView.weatherTv.text = forecast.weather
                            itemView.temperatureTv.text = getString(R.string.temperature_text, forecast.temperature)

                            addView(itemView.root)
                        }

                    }
                },
                failureCallback = { t ->
                    t.printStackTrace()
                }
            )
        }
    }
}