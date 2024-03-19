package com.github.jasgo.navermaprenderer.event

import com.naver.maps.map.overlay.Marker

class Renderer {
    private var onRenderNewPlaceEventListener: RenderNewPlaceEventListener

    init {
        onRenderNewPlaceEventListener = object : RenderNewPlaceEventListener {
            override fun onRenderNewPlace(marker: Marker) {
                // Do nothing
            }
        }
    }

    fun setOnRenderNewPlace(onRendererNewPlaceEventListener: RenderNewPlaceEventListener) {
        this.onRenderNewPlaceEventListener = onRendererNewPlaceEventListener
    }

    fun renderNewPlace(marker: Marker) {
        onRenderNewPlaceEventListener.onRenderNewPlace(marker)
    }

}