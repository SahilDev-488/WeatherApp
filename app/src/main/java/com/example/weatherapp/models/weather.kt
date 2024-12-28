package com.example.weatherapp.models

import java.io.Serializable

data class weather(
    val coord:Coord,
    val weather:List<Weater>,
    val base:String,
    val main:Main,
    val visibility:Int,
    val wind:Wind,
    val clouds:Clouds,
    val dt:Long,
    val sys:Sys,
    val timezone:Int,
    val id:Int,
    val name:String,
    val cod:Int
):Serializable

/*
{
  "coord": {
    "lon": 76.7179,
    "lat": 30.7046
  },
  "weather": [
    {
      "id": 800,
      "main": "Clear",
      "description": "clear sky",
      "icon": "01n"
    }
  ],
  "base": "stations",
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
  "visibility": 10000,
  "wind": {
    "speed": 2.73,
    "deg": 332,
    "gust": 2.81
  },
  "clouds": {
    "all": 0
  },
  "dt": 1734722047,
  "sys": {
    "country": "IN",
    "sunrise": 1734745595,
    "sunset": 1734782155
  },
  "timezone": 19800,
  "id": 6992326,
  "name": "Mohali",
  "cod": 200
}
 */
