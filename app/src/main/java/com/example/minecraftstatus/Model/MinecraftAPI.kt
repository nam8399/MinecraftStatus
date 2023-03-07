package com.example.minecraftstatus.Model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MinecraftAPI {
    @GET("v2/status/java/{address}")
    fun getJavaServerStatus(@Path("address") page: String): Call<String>

    @GET("v2/status/java/{address}")
    suspend fun getSuspendJava(@Path("address") page: String): String
}