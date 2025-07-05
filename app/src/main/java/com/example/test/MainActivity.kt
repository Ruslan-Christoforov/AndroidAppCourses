package com.example.test

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject

import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter



class MainActivity : AppCompatActivity() {


    private lateinit var coinApiService: CorseApiService

    private lateinit var myTextView: TextView

    private lateinit var recyclerView: RecyclerView

    private var coursesList: List<Course>? = null


    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    var hashLikeCourses: List<User>? = null


    var items: MutableList<Item> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        //deleteDatabase("user_database")

        db = AppDatabase.getDatabase(this)
        userDao = db.userDao()


        lifecycleScope.launch {
            //userDao.insert(newUser)
            hashLikeCourses = userDao.getAllUsers()
            // Обработка списка пользователей

            // Убедитесь, что users не равен null и не пуст
            hashLikeCourses?.let { userList ->
                if (userList.isNotEmpty()) {
                } else {
                    println("Список пользователей пуст.")
                }
            } ?: run {
                println("Не удалось получить список пользователей.")
            }


        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://drive.usercontent.google.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        coinApiService = retrofit.create(CorseApiService::class.java)

        myTextView = findViewById(R.id.tvT)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchCoins()

        val btSort: Button = findViewById(R.id.sort)

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

        val btFavorites: Button = findViewById(R.id.btFav)

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

                    Item(item.id, item.title, item.text, item.price, item.rate, item.startDate, item.hasLike, item.publishDate)
                }
                recyclerView.adapter = adapter
            }

            for (item in visibleItems) {
                addInDatbase(item)
            }

        }

        val btMain: Button = findViewById(R.id.btMain)

        btMain.setOnClickListener {
            changeAtributes("main")

            fetchCoins()
        }

        val btAc: Button = findViewById(R.id.btAc)
        btAc.setOnClickListener {
            changeAtributes("accaunt")

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

                            val exists = hashLikeCourses!!.any { it.title == course.title }
                            if (exists) {
                                course.hasLike = true
                                //Toast.makeText(this@MainActivity, "Yse", Toast.LENGTH_SHORT).show()
                            }

                            items.add(Item(course.id, course.title, course.text, course.price, course.rate, course.startDate, course.hasLike, course.publishDate))
                        }

                        items = items.distinctBy { it.title }.toMutableList()

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
        var t: TextView
        t = findViewById(R.id.label1)
        t.setTextColor(Color.parseColor("#F2F2F3"))

        t = findViewById(R.id.label2)
        t.setTextColor(Color.parseColor("#F2F2F3"))

        t = findViewById(R.id.label3)
        t.setTextColor(Color.parseColor("#F2F2F3"))

        var bt: Button
        bt = findViewById(R.id.btMain)
        bt.foreground = ContextCompat.getDrawable(this, R.drawable.sl2)

        bt = findViewById(R.id.btFav)
        bt.foreground = ContextCompat.getDrawable(this, R.drawable.sl1)

        bt = findViewById(R.id.btAc)
        bt.foreground = ContextCompat.getDrawable(this, R.drawable.sl6)

        if (choose == "favorites") {
            var t: TextView = findViewById(R.id.label2)
            t.setTextColor(Color.parseColor("#12B956"))
            bt = findViewById(R.id.btFav)
            bt.foreground = ContextCompat.getDrawable(this, R.drawable.sl3)
        }

        if (choose == "main") {
            var t: TextView = findViewById(R.id.label1)
            t.setTextColor(Color.parseColor("#12B956"))
            bt = findViewById(R.id.btMain)
            bt.foreground = ContextCompat.getDrawable(this, R.drawable.sl4)
        }

        if (choose == "accaunt") {
            var t: TextView = findViewById(R.id.label3)
            t.setTextColor(Color.parseColor("#12B956"))
            bt = findViewById(R.id.btAc)
            bt.foreground = ContextCompat.getDrawable(this, R.drawable.sl7)
        }


    }

    private fun addInDatbase(item: Item) {
        // Добавление нового пользователя
        val newUser = User(title = item.title,
            text = item.text,
            price = item.price,
            rate = item.rate,
            startDate = item.startDate,
            hasLike = item.hasLike,
            publishDate = item.publishDate
        )


        lifecycleScope.launch {
            userDao.insert(newUser)
            //Toast.makeText(this@MainActivity, "Данные загружены", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addInDatbase(item: User) {
        // Добавление нового пользователя
        val newUser = User(title = item.title,
            text = item.text,
            price = item.price,
            rate = item.rate,
            startDate = item.startDate,
            hasLike = item.hasLike,
            publishDate = item.publishDate
        )


        lifecycleScope.launch {
            userDao.insert(newUser)
            //Toast.makeText(this@MainActivity, "Данные загружены", Toast.LENGTH_SHORT).show()
        }
    }
}