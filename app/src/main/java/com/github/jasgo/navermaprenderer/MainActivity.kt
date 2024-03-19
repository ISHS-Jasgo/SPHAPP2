package com.github.jasgo.navermaprenderer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.github.jasgo.navermaprenderer.datareceiver.DataManager
import com.github.jasgo.navermaprenderer.event.Renderer
import com.github.jasgo.navermaprenderer.event.RenderNewPlaceEventListener
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
        findViewById<Button>(R.id.search).setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    @UiThread
    override fun onMapReady(map: NaverMap) {
        val positionData = DataManager.placeDataReceiver.positionData
        val placeSizeData = DataManager.placeSizeDataReceiver.placeSizeData
        val markerList = ArrayList<Marker>()
        runOnUiThread {
            positionData.forEach { (place, position) ->
                run {
                    val marker = Marker()
                    marker.position = position
                    marker.captionText = place
                    marker.captionTextSize = 16f
                    marker.icon = MarkerIcons.BLACK
                    marker.iconTintColor = resources.getColor(R.color.black, null)
                    marker.isHideCollidedMarkers = true
                    marker.isHideCollidedCaptions = true
                    marker.isForceShowIcon = true
                    marker.isForceShowCaption = true
                    marker.setOnClickListener { _ ->
                        val intent = Intent(this, InfoActivity::class.java)
                        intent.putExtra("placeName", place)
                        intent.putExtra("placeSize", placeSizeData[place])
                        intent.putExtra("peopleCount", DataManager.currentPeopleDataReceiver.currentPeopleData[place])
                        intent.putExtra("sph", DataManager.sphDataReceiver.sphData[place]?.toDoubleArray())
                        intent.putExtra("predict", DataManager.predictDataReceiver.predictData[place])
                        startActivity(intent)
                        true
                    }
                    markerList.add(marker)
                }
            }
        }
        val markerInScreenList = ArrayList<Marker>()
        val renderer = Renderer()
        map.addOnCameraChangeListener { reason, _ ->
            if (reason == CameraUpdate.REASON_GESTURE) {
                val projection = map.projection
                markerList.forEach { marker ->
                    run {
                        val position = marker.position
                        val x = projection.toScreenLocation(position).x
                        val y = projection.toScreenLocation(position).y
                        if (x < 0 || x > map.width || y < 0 || y > map.height) {
                            marker.map = null
                            if (markerInScreenList.contains(marker)) markerInScreenList.remove(marker)
                        } else {
                            if (marker.map == null) {
                                marker.map = map
                                markerInScreenList.add(marker)
                                renderer.renderNewPlace(marker)
                            }
                        }
                    }
                }
            }
        }
        renderer.setOnRenderNewPlace(object : RenderNewPlaceEventListener {
            override fun onRenderNewPlace(marker: Marker) {
                val predictData = DataManager.predictDataReceiver.predictData
                val sphData = DataManager.sphDataReceiver.sphData
                val currentPeopleData = DataManager.currentPeopleDataReceiver.currentPeopleData
                val place = marker.captionText
                if (!sphData.containsKey(place)) {
                    return
                }
                val risk = ((sphData[place]!![0] / 1000) * 100).toInt()
                if (risk <= 40) {
                    marker.icon = MarkerIcons.GREEN
                } else if (risk <= 70) {
                    marker.icon = MarkerIcons.YELLOW
                } else {
                    marker.icon = MarkerIcons.RED
                }
            }
        })
    }
}