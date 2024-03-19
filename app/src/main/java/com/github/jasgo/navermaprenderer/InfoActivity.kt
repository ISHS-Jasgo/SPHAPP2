package com.github.jasgo.navermaprenderer

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.floor

class InfoActivity : AppCompatActivity() {

    lateinit var placeName_view : TextView
    lateinit var peopleCount_view : TextView
    lateinit var riskConstant_view : TextView
    lateinit var confirmButton: Button
    lateinit var predictListView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        placeName_view = findViewById(R.id.PlaceName)
        peopleCount_view = findViewById(R.id.text_2)
        riskConstant_view = findViewById(R.id.text_1)
        confirmButton = findViewById(R.id.endBtn)
        predictListView = findViewById(R.id.predictList)

        confirmButton.setOnClickListener {
            finish()
        }

        val intent = intent
        val placeName: String? = intent.getStringExtra("placeName")
        val placeSize: Int = intent.getIntExtra("placeSize", 0)
        val peopleCount: Pair<Int, Int>? = intent.getSerializableExtra("peopleCount") as Pair<Int, Int>?
        val sph: List<Double>? = intent.getDoubleArrayExtra("sph")?.toList()
        val predict: HashMap<String, Int>? = intent.getSerializableExtra("predict") as HashMap<String, Int>?
        if (!sph.isNullOrEmpty() && !predict.isNullOrEmpty() && peopleCount != null) {
            placeName_view.text = placeName
            "현재 인구수: ${peopleCount.first} ~ ${peopleCount.second}명".also { peopleCount_view.text = it }
            "현재 위험도: ${floor(sph[0] / 10).toInt()}%".also { riskConstant_view.text = it }
            val keyList = predict.keys.sortedBy {
                val dateFormat = SimpleDateFormat("hh시mm분", Locale.KOREA)
                dateFormat.parse(it)?.time
            }
            val dataList = ArrayList<String>()
            for (key in keyList) {
                dataList.add("$key 인구수: ${(floor(predict[key]?.div(100.0) ?: 0.0) * 100).toInt()}명 위험도: ${floor(sph[predict.keys.indexOf(key)] / 10).toInt()}%")
            }
            predictListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        }
    }
}