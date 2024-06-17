package com.example.fastcampusbasic.Part2.chapter8

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fastcampusbasic.databinding.ActivityMapBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.play.integrity.internal.m
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.Tm128
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private var isMapInit = false
    private var markers = emptyList<Marker>()
    private var searchResultAdapter = SearchResultAdapter {
        moveCamera(it, 17.0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.bottomSheetL.searchResultRv.apply {
            layoutManager = LinearLayoutManager(this@MapActivity)
            adapter = searchResultAdapter
        }

        binding.searchView.setOnQueryTextListener(object: OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return if(query?.isNotEmpty() == true) {
                    SearchRepository.getGoodRestaurant(query).enqueue(object : Callback<SearchResult> {
                        override fun onResponse(
                            call: Call<SearchResult>, response: Response<SearchResult>
                        ) {
                            val searchItemList = response.body()?.items.orEmpty()

                            if(searchItemList.isEmpty()) {
                                Toast.makeText(this@MapActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                                return
                            } else if (isMapInit.not()) {
                                Toast.makeText(this@MapActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                return
                            }

                            markers.forEach {
                                it.map = null
                            }

                            markers = searchItemList.map {
                                Marker().apply {
                                    position = LatLng(it.mapy / 1e7, it.mapx / 1e7)
                                    captionText = it.title
                                    map = naverMap
                                }
                            }

                            searchResultAdapter.setDataSet(searchItemList)
                            collapseBottomSheet()
                            moveCamera(markers.first().position, 14.0)
                        }

                        override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                    false
                } else {
                    true
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun moveCamera(position: LatLng, zoomLevel: Double) {
        Log.e("MapActivity", "zoomLevel : $zoomLevel")
        if (isMapInit.not()) return
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(position, zoomLevel)
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun collapseBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetL.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(mapObject: NaverMap) {
        naverMap = mapObject
        isMapInit = true
    }
}