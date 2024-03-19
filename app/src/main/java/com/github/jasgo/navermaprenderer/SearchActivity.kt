package com.github.jasgo.navermaprenderer

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.jasgo.navermaprenderer.placedata.PlaceList
import com.github.jasgo.sphapp.Place
import com.github.jasgo.sphapp.PlaceListAdapter

class SearchActivity : AppCompatActivity() {

    val TAG = "SearchActivity"

    lateinit var rv_place_book: RecyclerView
    lateinit var placeListAdapter: PlaceListAdapter
    lateinit var places: ArrayList<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        rv_place_book = findViewById(R.id.rv_place_list)

        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(searchViewTextListener)

        places = tempPlaces()
        setAdapter()
    }

    var searchViewTextListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            //검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            //텍스트 입력/수정시에 호출
            override fun onQueryTextChange(s: String): Boolean {
                placeListAdapter.filter.filter(s)
                Log.d(TAG, "SearchVies Text is changed : $s")
                return false
            }
        }

    private fun setAdapter() {
        //리사이클러뷰에 리사이클러뷰 어댑터 부착
        rv_place_book.layoutManager = LinearLayoutManager(this)
        placeListAdapter = PlaceListAdapter(places, this)
        rv_place_book.adapter = placeListAdapter
    }

    private fun tempPlaces(): ArrayList<Place> {
        val tempPersons = ArrayList<Place>()
        PlaceList().getPlaceList().forEach {
            val name = it.split("/")[0].trim()
            val address = it.split("/")[1].trim()
            tempPersons.add(Place(name, address))
        }
        return tempPersons
    }
}