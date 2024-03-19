package com.github.jasgo.navermaprenderer.datareceiver

import android.util.Log
import com.github.jasgo.navermaprenderer.placedata.PlaceList
import com.naver.maps.geometry.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class PlaceDataReceiver {

    var positionData: HashMap<String, LatLng>

    init {
        val client = OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build()
        positionData = HashMap()
        val placeSizeDataReceiver = PlaceSizeDataReceiver()
        thread(start = true) {
            positionData = getLocation(client).filter { place ->
                placeSizeDataReceiver.placeSizeData[place.key] != 0
            } as HashMap<String, LatLng>
        }.join()
    }

    private fun getLocation(client: OkHttpClient): HashMap<String, LatLng> {
        val location = HashMap<String, LatLng>()
        val request1 = getLatLngfromAddressRequest()
        val response1 = client.newCall(request1).execute()
        val responseString1 = response1.body?.string()
        Log.d("response", responseString1!!)
        PlaceList().getPlaceList().forEach { p ->
            run {
                val place = p.split("/")[0].trim()
                location[place] = parseLatLng(responseString1, place)
            }
        }
        return location
    }

    private fun getLatLngfromAddressRequest(): Request {
        val url = URL("http://app.ishs.co.kr:5000/places")
        return Request.Builder()
            .url(url)
            .build()
    }

    private fun parseLatLng(responseString: String, place: String): LatLng {
        val jsonObject = JSONObject(responseString)
        val placeData = jsonObject.getJSONObject(place)
        val longitude = placeData.getDouble("lng")
        val latitude = placeData.getDouble("lat")
        return LatLng(latitude, longitude)
    }

}