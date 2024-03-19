package com.github.jasgo.navermaprenderer.datareceiver

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class PredictDataReceiver {

    var predictData: HashMap<String, HashMap<String, Int>>

    init {
        predictData = HashMap()
        val client = OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build()
        thread(start = true) {
            predictData = parsePredictData(getPredictData(client))
        }.join()
    }

    fun update() {
        val client = OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build()
        thread(start = true) {
            predictData = parsePredictData(getPredictData(client))
        }
    }

    private fun parsePredictData(responseString: String): HashMap<String, HashMap<String, Int>> {
        val data = JSONObject(responseString)
        val result = HashMap<String, HashMap<String, Int>>()
        for (key in data.keys()) {
            val value = data[key] as JSONObject
            val place = HashMap<String, Int>()
            val ds = value["ds"] as JSONObject
            val yhat = value["yhat"] as JSONObject
            for (key2 in ds.keys()) {
                val time = ds[key2].toString().split("T")[1].split(":")
                place["${time[0]}시 ${time[1]}분"] = (yhat[key2] as Double).toInt()
            }
            result[key] = place
        }
        return result
    }

    private fun getPredictData(client: OkHttpClient): String {
        val request = getPredictPeopleCountRequest()
        val response = client.newCall(request).execute()
        val responseString = response.body?.string()
        return responseString!!
    }

    private fun getPredictPeopleCountRequest(): Request {
        val url = URL("http://app.ishs.co.kr:3000/predict/all")
        return Request.Builder()
            .url(url)
            .build()
    }
}