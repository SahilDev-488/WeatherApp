package com.example.weatherapp.models

import java.io.Serializable

data class Coord(
    val lon:Double,
    val lat:Double
): Serializable
/*
"coord": {
    "lon": 76.7179,
    "lat": 30.7046
  },
 */