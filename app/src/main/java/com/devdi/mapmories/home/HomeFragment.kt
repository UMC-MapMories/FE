package com.devdi.mapmories.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devdi.mapmories.R
import com.devdi.mapmories.community.PeopleFragment
import com.devdi.mapmories.databinding.FragmentHomeBinding
import com.devdi.mapmories.diary.DiaryFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locations = mutableListOf<LatLng>()
    private val markers = mutableMapOf<String, Marker>()

    private val PREFS_NAME = "MapPreferences"
    private val LOCATIONS_KEY = "saved_locations"

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            enableMyLocation()
        } else {
            // 권한 거부 시 기본 위치로 이동
            moveToDefaultLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)

        // SharedPreferences에 저장된 위치 로드
        loadLocationsFromPrefs()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // 위치 권한 확인
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            // 권한 요청
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // 저장된 마커 추가
        for (latLng in locations) {
            addMarkerOnMap(latLng)
        }

        map.setOnMapLongClickListener { latLng ->
            locations.add(latLng) // 클릭한 위치 저장
            addMarkerOnMap(latLng) // 마커 추가
            saveLocationsToPrefs() // 위치 정보 저장
            Log.d("locations", "long click : ${latLng.latitude}, ${latLng.longitude}")

            Handler(Looper.getMainLooper()).postDelayed({
                val diaryFragment = DiaryFragment().apply {
                    arguments = Bundle().apply {
                        putDouble("latitude", latLng.latitude)
                        putDouble("longitude", latLng.longitude)
                    }
                }

                // Diary Fragment 전환
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, diaryFragment)
                    .addToBackStack(null) // 뒤로 가기
                    .commit()
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId =
                    R.id.diary
            }, 1000) // 1초
        }

//        map.setOnMarkerClickListener { marker ->
//            val peopleFragment = PeopleFragment().apply {
//                arguments = Bundle().apply {
//                    putDouble("latitude", marker.position.latitude)
//                    putDouble("longitude", marker.position.longitude)
//                }
//            }
//
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.frame_layout, peopleFragment)
//                .addToBackStack(null)
//                .commit()
//            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId = R.id.people
//            true
//        }

        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                val peopleFragment = PeopleFragment().apply {
                    arguments = Bundle().apply {
                        putDouble("latitude", marker.position.latitude)
                        putDouble("longitude", marker.position.longitude)
                    }
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, peopleFragment)
                    .addToBackStack(null)
                    .commit()
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId =
                    R.id.people
            }
        })

        map.setOnMarkerClickListener { marker ->
            showMarkerOptionsDialog(marker)
            true
        }
    }

    private fun showMarkerOptionsDialog(marker: Marker) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("마커 옵션")
            .setMessage("이 마커를 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                deleteMarker(marker)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deleteMarker(marker: Marker) {
        val position = marker.position
        locations.removeIf { it.latitude == position.latitude && it.longitude == position.longitude }
        markers.remove(getMarkerKey(position))
        marker.remove()
        saveLocationsToPrefs()

        Toast.makeText(context, "마커가 삭제되었습니다", Toast.LENGTH_SHORT).show()
    }

    private fun addMarkerOnMap(latLng: LatLng) {
        val marker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_airplane))
                .draggable(true)
        )
        marker?.let {
            markers[getMarkerKey(latLng)] = it
        }
    }

    private fun getMarkerKey(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }

    // SharedPreferences에 위치 저장
    private fun saveLocationsToPrefs() {
        val sharedPrefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(locations)

        Log.d("HomeFragment", "saveLocationsToPrefs : $json")

        editor.putString(LOCATIONS_KEY, json)
        editor.apply()
    }

    // SharedPreferences에서 위치 로드
    private fun loadLocationsFromPrefs() {
        val sharedPrefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPrefs.getString(LOCATIONS_KEY, null)

        Log.d("HomeFragment", "loadLocationsFromPrefs : $json")

        if (json != null) {
            val type = object : TypeToken<List<LatLng>>() {}.type
            val savedLocations: List<LatLng> = gson.fromJson(json, type)
            locations.clear()
            locations.addAll(savedLocations)
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    moveCameraToLocation(currentLatLng)
                } else {
                    // 위치 정보를 가져올 수 없는 경우 기본 위치로 이동
                    moveToDefaultLocation()
                }
            }
        }
    }

    private fun moveCameraToLocation(latLng: LatLng) {
        val position = CameraPosition.Builder()
            .target(latLng)
            .zoom(5f) // 국가 단위 확대
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    private fun moveToDefaultLocation() {
        // 기본 위치: 서울
        val defaultLatLng = LatLng(37.566610, 126.978403)
        moveCameraToLocation(defaultLatLng)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        loadLocationsFromPrefs()

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if (bottomNavigationView != null) {
            if (bottomNavigationView.selectedItemId != R.id.home) {
                bottomNavigationView.menu.findItem(R.id.home).isChecked = true
            }
        }
    }
}