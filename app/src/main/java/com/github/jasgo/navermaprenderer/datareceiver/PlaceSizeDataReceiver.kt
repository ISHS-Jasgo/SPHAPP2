package com.github.jasgo.navermaprenderer.datareceiver

import com.github.jasgo.navermaprenderer.placedata.PlaceEnum
import kotlin.concurrent.thread

class PlaceSizeDataReceiver {

    var placeSizeData: HashMap<String, Int> = HashMap()

    init {
        thread(start = true) {
            PlaceEnum.PLACE_NM.list.forEach { p ->
                run {
                    placeSizeData[p] = getPlaceSize(p)
                }
            }
        }.join()
    }

    private fun getPlaceSize(place: String): Int {
        return PlaceEnum.PLACE_SIZE.list[PlaceEnum.PLACE_NM.list.indexOf(place.replace("+", " "))].toInt()
    }
}