package com.example.weatherapp.models

import java.io.Serializable

data class Weater(
    val id:Int,
    val main:String,
    val description:String,
    val icon:String
): Serializable
/*
"weather": [
    {
      "id": 800,
      "main": "Clear",
      "description": "clear sky",
      "icon": "01n"
    }
  ],
 */
