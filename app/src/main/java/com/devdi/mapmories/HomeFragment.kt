package com.devdi.mapmories

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devdi.mapmories.databinding.FragmentHomeBinding
import com.devdi.mapmories.community.PeopleFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locations = mutableListOf<LatLng>()

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

//        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
//        mapFragment.getMapAsync(this)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.getMapAsync(this)

        // 이전 마커 복구
        savedInstanceState?.let {
            val savedLocations = it.getParcelableArrayList<LatLng>("locations")
            savedLocations?.let { list ->
                locations.addAll(list)
            }
        }
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
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId = R.id.diary
            }, 1000) // 1초
        }

        map.setOnMarkerClickListener { marker ->
            val peopleFragment = PeopleFragment().apply {
                arguments = Bundle().apply {
                    putDouble("latitude", marker.position.latitude)
                    putDouble("longitude", marker.position.longitude)
                }
            }

            // People Fragment 전환
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, peopleFragment)
                .addToBackStack(null)
                .commit()
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId = R.id.people
            true
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

    private fun addMarkerOnMap(latLng: LatLng) {
        map.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_airplane))
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("locations", ArrayList(locations))
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

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if (bottomNavigationView != null) {
            if (bottomNavigationView.selectedItemId != R.id.home) {
                bottomNavigationView.menu.findItem(R.id.home).isChecked = true
            }
        }
    }
}