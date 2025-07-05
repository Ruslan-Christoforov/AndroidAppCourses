package com.example.test

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface CoinApiService {
    @GET("https://drive.usercontent.google.com/u/0/uc?id=15arTK7XT2b7Yv4BJsmDctA4Hg-BbS8-q&export=download ") // Замените на актуальный путь
    fun getRawJson(): Call<ResponseBody>
}