package com.example.test

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {


    private lateinit var coinApiService: CoinApiService

    private lateinit var myTextView: TextView

    private lateinit var recyclerView: RecyclerView

    private var coursesList: List<Course>? = null




    var items: MutableList<Item> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://drive.usercontent.google.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        coinApiService = retrofit.create(CoinApiService::class.java)

        myTextView = findViewById(R.id.tvT)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchCoins()

        var btSort: Button = findViewById(R.id.sort)

        btSort.setOnClickListener {
            var sortedItems = items.sortedByDescending { LocalDate.parse(it.publishDate, DateTimeFormatter.ISO_LOCAL_DATE) }

            if (sortedItems != null) {
                val adapter = ItemAdapter(sortedItems!!) { item ->
                    // Действие при нажатии на кнопку
                    //println("Clicked on: ${item.title}")
                }
                recyclerView.adapter = adapter
            }
        }

        var btFavorites: Button = findViewById(R.id.btFav)

        btFavorites.setOnClickListener {
            changeAtributes("favorites")
            val hushLike = true

            val visibleItems = if (hushLike) {
                items.filter { it.hasLike }
            } else {
                items.filter { !it.hasLike }
            }

            if (visibleItems != null) {
                val adapter = ItemAdapter(visibleItems!!) { item ->
                }
                recyclerView.adapter = adapter
            }
        }

        var btMain: Button = findViewById(R.id.btMain)

        btMain.setOnClickListener {
            changeAtributes("main")
            items = mutableListOf()
            fetchCoins()
        }

    }

    private fun fetchCoins() {

        val gson = Gson()

        coinApiService.getRawJson().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    var jsonResponse = response.body()?.string() // Получаем JSON в виде строки
                    // Обрабатываем полученные данные
                    if (jsonResponse != null) {
                        // Например, выводим в лог
                        println(jsonResponse)
                        //myTextView.text = jsonResponse.toString()

                        val jsonObject: JsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)

                        // Получаем все ключи
                        for (key in jsonObject.keySet()) {
                            val value: JsonElement = jsonObject[key]
                            println("Key: $key, Value: $value")
                        }

                        // Если вы хотите получить конкретный массив
                        coursesList = gson.fromJson(jsonObject.get("courses"), Array<Course>::class.java).toList()

                        // Теперь вы можете работать с курсами
                        for (course in coursesList!!) {
                            myTextView.append('\n' + course.title)
                            items.add(Item(course.title, course.text, course.price, course.rate, course.startDate, course.hasLike, course.publishDate))
                        }

                    }
                } else {
                    // Обработать ошибку
                    println("Ошибка: ${response.code()}")
                    System.out.println("e")
                }

                val adapter = ItemAdapter(items!!) { item ->
                    // Действие при нажатии на кнопку
                    //println("Clicked on: ${item.title}")
                }
                recyclerView.adapter = adapter
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Обработка ошибки
                t.printStackTrace()
            }
        })
    }

    private fun changeAtributes(choose:String) {
        if (choose == "favorites") {
            var t: TextView = findViewById(R.id.label2)
            t.setTextColor(Color.parseColor("#12B956"))

            t = findViewById(R.id.label1)
            t.setTextColor(Color.parseColor("#F2F2F3"))

            var bt: Button = findViewById(R.id.btMain)
            bt.foreground = ContextCompat.getDrawable(this, R.drawable.sl2)

            bt = findViewById(R.id.btFav)
            bt.foreground = ContextCompat.getDrawable(this, R.drawable.sl3)
        }

        if (choose == "main") {
            var t: TextView = findViewById(R.id.label1)
            t.setTextColor(Color.parseColor("#12B956"))

            t = findViewById(R.id.label2)
            t.setTextColor(Color.parseColor("#F2F2F3"))

            var bt: Button = findViewById(R.id.btFav)
            bt.foreground = ContextCompat.getDrawable(this, R.drawable.sl1)

            bt = findViewById(R.id.btMain)
            bt.foreground = ContextCompat.getDrawable(this, R.drawable.sl4)

        }

    }
}