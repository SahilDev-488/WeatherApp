package com.example.weatherapp.models

import java.io.Serializable

data class Wind(
    val speed:Double,
    val deg:Int,
    val gust:Double
): Serializable
/*
"wind": {
    "speed": 2.73,
    "deg": 332,
    "gust": 2.81
  },
 */
