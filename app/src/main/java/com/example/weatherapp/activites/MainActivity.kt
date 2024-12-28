package com.example.weatherapp.activites

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.StateSet
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.models.weather
import com.example.weatherapp.network.weatherService
import com.example.weatherapp.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var customDialog:Dialog
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mSharedPreferences:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)

        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        mSharedPreferences =
            getSharedPreferences(Constants.PREFERENCES,Context.MODE_PRIVATE)

        setUpUi()

        if (!isLocationEnable()){
            Toast.makeText(this@MainActivity,
                "Your location provider is turned off. Please turn it on",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }else{
            Dexter.withContext(this).withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()){
                        requestLocationData()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationaleDialogPermission()
                } } ).onSameThread().check()
        }
    }
    @SuppressLint("MissingPermission")
    private fun requestLocationData(){
        val mLocation = LocationRequest()
        mLocation.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationProviderClient.requestLocationUpdates(
            mLocation,mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val mLastLocation: Location? = locationResult.lastLocation

            val mLatitude = mLastLocation!!.latitude
            Log.e("Current Latitude","${mLatitude}")

            val mLongitude = mLastLocation.longitude

            Log.e("Current Latitude","${mLongitude}")

            getLocationWeatherDetails(mLatitude,mLongitude)
        }
    }


    private fun getLocationWeatherDetails(latitude:Double,longitude:Double){
        if (Constants.isNetworkAvailable(this)){

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(weatherService::class.java)
                val listCall = service.getWeather(
                    latitude,longitude, Constants.METRICS, Constants.APP_ID
                )
                 showDialog()
                listCall.enqueue(object :Callback<weather>{
                    @SuppressLint("CommitPrefEdits")
                    override fun onResponse(call: Call<weather>, response: Response<weather>) {
                        if (response.isSuccessful){
                            val weatherList = response.body()
                            cancelDailog()

                            val sharedPreferenceDataToString = Gson()
                                .toJson(weatherList)

                            val editor = mSharedPreferences.edit()
                            editor.putString(Constants.WEATHER_RESPONSE_DATA,sharedPreferenceDataToString)
                            editor.apply()
                            setUpUi()
                            Log.e("body","$weatherList")
                        }else{
                            val rc = response.code()
                            when(rc){
                                400 -> {
                                    Log.e("error","Bad Connection")
                                }
                                404 -> {
                                    Log.e("error","Time Out")
                                }
                                else -> {
                                    Log.e("error","Extremely")
                                }
                            }
                        }

                    }
                    override fun onFailure(p0: Call<weather>, t: Throwable) {
                        cancelDailog()
                        Log.e("errorr","${t.message}")
                    }
                })


        }else{
            Toast.makeText(this,
                "No Internet Connection.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUi() {
        val getSharedPreferencesData = mSharedPreferences.
        getString(Constants.WEATHER_RESPONSE_DATA,"")

        if (!getSharedPreferencesData.isNullOrEmpty()){
            val weather = Gson().fromJson(getSharedPreferencesData,weather::class.java)

            for (i in weather.weather.indices){
                Log.e("Weather Name",weather.weather.toString())
                binding.tvMain.text = weather.weather[i].main
                binding.tvMainDescription.text = weather.weather[i].description
                binding.tvTemp.text = weather.main.temp.toString() +
                        getUnits(application.resources.configuration.locales.toString())

                binding.tvSunriseTime.text = unixTime(weather.sys.sunrise)
                binding.tvSunsetTime.text = unixTime(weather.sys.sunset)
                binding.tvMax.text = weather.main.temp_max.toString()+"max"
                binding.tvMin.text = weather.main.temp_min.toString()+"min"
                binding.tvHumidity.text = weather.main.humidity.toString()+"per cent"
                binding.tvSpeed.text = weather.wind.speed.toString()
                binding.tvName.text = weather.name.toString()
                binding.tvCountry.text = weather.sys.country

                when(weather.weather[i].icon){
                    "01d" -> binding.ivMain.setImageResource(R.drawable.sunny)
                    "02d" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "03d" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "04d" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "04n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "10d" -> binding.ivMain.setImageResource(R.drawable.rain)
                    "11d" -> binding.ivMain.setImageResource(R.drawable.storm)
                    "13d" -> binding.ivMain.setImageResource(R.drawable.snowflake)
                    "01n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "02n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "03n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "10n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "11n" -> binding.ivMain.setImageResource(R.drawable.rain)
                    "13n" -> binding.ivMain.setImageResource(R.drawable.snowflake)
                }
            }
        }

    }

    private fun getUnits(value:String):String?{
        var value = "°C"
        if ("US" == value || "LR" == value || "MM" == value){
            value = "°F"
        }
        return value
    }
    @SuppressLint("SimpleDateFormat")
    private fun unixTime(time:Long):String?{
        val date = Date(time *1000L)
        val sdf = SimpleDateFormat("HH:mm",Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       return when(item.itemId){
            R.id.action_refresh ->{
                requestLocationData()
                true
            }else ->{
            super.onOptionsItemSelected(item)
            }
        }

    }

    private fun showDialog(){
        customDialog = Dialog(this)
        customDialog.setContentView(R.layout.custom_dialog)
        customDialog.create()
        customDialog.show()
    }
    private fun cancelDailog(){
        customDialog.dismiss()
    }


    private fun showRationaleDialogPermission() {
        AlertDialog.Builder(this)
            .setMessage("It's Look like you have turned off the permission." +
                    " It is required for this feature. " +
                    "It can be enabled under Application Settings")
            .setPositiveButton("GO SETTINGS"){_,_->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"){dialog,_->
                dialog.dismiss()
            }
            .create().show()
    }

    private fun isLocationEnable() : Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun checkPermission():Boolean{
       return ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}