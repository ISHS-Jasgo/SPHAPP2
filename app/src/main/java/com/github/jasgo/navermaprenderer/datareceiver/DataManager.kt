package com.github.jasgo.navermaprenderer.datareceiver

import android.util.Log
import java.util.Timer
import java.util.TimerTask

class DataManager {
    companion object {
        val placeDataReceiver = PlaceDataReceiver()
        val placeSizeDataReceiver = PlaceSizeDataReceiver()
        val predictDataReceiver = PredictDataReceiver()
        val sphDataReceiver = SPHDataReceiver()
        val currentPeopleDataReceiver = CurrentPeopleDataReceiver()

        fun update() {
            predictDataReceiver.update()
            sphDataReceiver.update()
            currentPeopleDataReceiver.update()
        }

        val timer = Timer().schedule(object : TimerTask() {
            override fun run() {
                update()
                Log.d("DataManager", "update")
            }
        }, 0, 1000 * 60 * 1)
    }
}