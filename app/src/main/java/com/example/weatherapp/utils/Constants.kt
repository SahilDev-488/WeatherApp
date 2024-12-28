package com.example.weatherapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object Constants {

    const val APP_ID:String = "72ceb3b9386b5f5d75695959874d594b"
    const val BASE_URL:String = "https://api.openweathermap.org/data/"
    const val METRICS:String = "metric"
    const val PREFERENCES:String = "weatherSharedPreference"
    const val WEATHER_RESPONSE_DATA = "weather_response_data"

    fun isNetworkAvailable(context: Context):Boolean{
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val network = connectivity.activeNetwork ?: return false
            val activeNetwork = connectivity.getNetworkCapabilities(network) ?: return false
            when{
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                else -> return false
            }
        }
        val network = connectivity.activeNetworkInfo
        return network != null &&
                network.isConnectedOrConnecting
    }
}