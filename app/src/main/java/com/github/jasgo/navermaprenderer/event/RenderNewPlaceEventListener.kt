package com.github.jasgo.navermaprenderer.event

import com.naver.maps.map.overlay.Marker

interface RenderNewPlaceEventListener {
    fun onRenderNewPlace(marker: Marker)
}