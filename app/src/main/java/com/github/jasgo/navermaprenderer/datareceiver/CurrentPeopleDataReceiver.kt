package com.github.jasgo.navermaprenderer.datareceiver

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.concurrent.thread

class CurrentPeopleDataReceiver {

    var currentPeopleData: HashMap<String, Pair<Int, Int>> = HashMap()

    init {
        val client = OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build()
        thread(start = true) {
            currentPeopleData = parseCurrentPeopleData(getCurrentPeopleData(client))
        }.join()
    }

    fun update() {
        val client = OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build()
        thread(start = true) {
            currentPeopleData = parseCurrentPeopleData(getCurrentPeopleData(client))
        }
    }

    private fun getCurrentPeopleData(client: OkHttpClient): String {
        val request = getCurrentPeopleDataRequest()
        val response = client.newCall(request).execute()
        return response.body?.string()!!
    }

    private fun getCurrentPeopleDataRequest(): Request {
        return Request.Builder()
            .url("http://app.ishs.co.kr:5000/people")
            .build()
    }

    private fun parseCurrentPeopleData(responseString: String): HashMap<String, Pair<Int, Int>> {
        val jsonObject = JSONObject(responseString)
        val result = HashMap<String, Pair<Int, Int>>()
        jsonObject.keys().forEach { key ->
            run {
                val value = jsonObject[key] as JSONObject
                val minValue = value["ppl_min"] as Int
                val maxValue = value["ppl_max"] as Int
                result[key] = Pair(minValue, maxValue)
            }
        }
        return result
    }
}