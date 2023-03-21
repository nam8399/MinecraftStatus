package com.sikstree.minecraftstatus.Model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MinecraftAPI {
    @GET("v2/status/java/{address}")
    fun getJavaServerStatus(@Path("address") page: String): Call<String>

    @GET("v2/status/java/{address}")
    suspend fun getSuspendJava(@Path("address") page: String): String

    @GET("v2/status/bedrock/{address}")
    suspend fun getSuspendBE(@Path("address") page: String): String

    @GET("users/profiles/minecraft/{uid}")
    suspend fun getSuspendUUID(@Path("uid") page: String): String

    @GET("avatars/{uuid}")
    suspend fun getSuspendSkin(@Path("uuid") page: String): String
}