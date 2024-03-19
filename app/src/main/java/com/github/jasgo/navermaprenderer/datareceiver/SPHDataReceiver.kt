package com.github.jasgo.navermaprenderer.datareceiver

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class SPHDataReceiver {

    var sphData: HashMap<String, List<Double>> = HashMap()

    init {
        val client = OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build()
        thread(start = true) {
            sphData = parseSPHData(getSPHResult(client))
        }.join()
    }

    fun update() {
        val client = OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build()
        thread(start = true) {
            sphData = parseSPHData(getSPHResult(client))
        }
    }

    private fun getSPHResult(client: OkHttpClient): String {
        val request4 = getPlaceDangerRequest()
        val response4 = client.newCall(request4).execute()
        val responseString4 = response4.body?.string()
        return responseString4!!
    }

    private fun getPlaceDangerRequest(): Request {
        val url = URL("http://jrh.ishs.co.kr/sphResult")
        return Request.Builder()
            .url(url)
            .build()
    }

    private fun parseSPHData(responseString: String): HashMap<String, List<Double>> {
        val jsonObject = JSONObject(responseString)
        val data = jsonObject["result"] as JSONObject
        val result = HashMap<String, List<Double>>()
        for (key in data.keys()) {
            val value = data[key] as JSONArray
            val list = ArrayList<Double>()
            for (i in 0 until value.length()) {
                if (value[i] is Double) {
                    list.add(value[i] as Double)
                } else {
                    list.add(0.0)
                }
            }
            result[key] = list
        }
        return result
    }
}