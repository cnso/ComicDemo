package com.jash.comicdemo.entities

import com.google.gson.annotations.SerializedName

data class Response<out T> (
        @SerializedName("code")
        val code:Int,
        @SerializedName("message")
        val message:String?,
        @SerializedName("data")
        val data:T
)