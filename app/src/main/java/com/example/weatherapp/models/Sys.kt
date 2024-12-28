package com.example.weatherapp.models

import java.io.Serializable

data class Sys(
    val country:String,
    val sunrise:Long,
    val sunset:Long
): Serializable
/*
 "sys": {
    "country": "IN",
    "sunrise": 1734745595,
    "sunset": 1734782155
  },
 */
