package com.example.weatherapp.models

import java.io.Serializable

data class Main(
    val temp:Double,
    val feels_like:Double,
    val temp_min:Double,
    val temp_max:Double,
    val pressure:Int,
    val humidity:Int,
    val sea_level:Int,
    val grnd_level:Int
): Serializable
/*
 "main": {
    "temp": 283.82,
    "feels_like": 281.83,
    "temp_min": 283.82,
    "temp_max": 283.82,
    "pressure": 1015,
    "humidity": 34,
    "sea_level": 1015,
    "grnd_level": 979
  },
 */
