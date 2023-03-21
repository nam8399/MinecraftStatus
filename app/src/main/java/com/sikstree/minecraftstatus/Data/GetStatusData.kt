package com.sikstree.minecraftstatus.Data

import com.google.gson.annotations.SerializedName

data class GetStatusData(
    @SerializedName("online")
    val online: String,

    @SerializedName("version")
    val version: Object,

    @SerializedName("players")
    val players: Object
//    val version: List<Version>,
//    val players: List<Players>

// @SerializedName으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함
// @SerializedName()로 변수명을 입치시켜주면 클래스 변수명이 달라도 알아서 매핑
)

//data class Version(
//    @SerializedName("name_raw")
//    val name_raw: String
//)
//
//data class Players(
//    @SerializedName("online")
//    val online: String,
//
//    @SerializedName("max")
//    val max:String
//)