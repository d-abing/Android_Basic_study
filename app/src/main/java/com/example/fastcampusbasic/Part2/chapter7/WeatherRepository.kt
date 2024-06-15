package com.example.fastcampusbasic.Part2.chapter7

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://apis.data.go.kr/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(WeatherService::class.java)

    fun getVillageForecast(
        longitude: Double,
        latitude: Double,
        successCallback: (List<Forecast>) -> Unit,
        failureCallback: (Throwable) -> Unit
    ) {
        val baseDateTime = BaseDateTime.getBaseDateTime()
        val converter = GeoPointConverter()
        val point = converter.convert(lon = longitude, lat = latitude)

        service.getVillageForecast(
            serviceKey = SERVICE_KEY,
            baseDate = baseDateTime.baseDate,
            baseTime = baseDateTime.baseTime,
            nx = point.nx,
            ny = point.ny
        ).enqueue(object : Callback<WeatherEntity> {

            override fun onResponse(call: Call<WeatherEntity>, response: Response<WeatherEntity>) {
                val forecastDateTimeMap = mutableMapOf<String, Forecast>()
                val forecastList = response.body()?.response?.body?.items?.forecastEntities.orEmpty()

                for (forecast in forecastList) {

                    val key = "${forecast.fcstDate}/${forecast.fcstTime}"
                    if (forecastDateTimeMap[key] == null) {
                        forecastDateTimeMap[key] =
                            Forecast(forecast.fcstDate, forecast.fcstTime)
                    }

                    forecastDateTimeMap[key]?.apply {
                        when(forecast.category) {
                            Category.POP -> precipitation = forecast.fcstValue.toInt()
                            Category.PTY -> precipitationType = transformRainType(forecast)
                            Category.SKY -> sky = transformSky(forecast)
                            Category.TMP -> temperature = forecast.fcstValue.toDouble()
                            else -> {}
                        }
                    }
                }

                val list = forecastDateTimeMap.values.toMutableList()
                list.sortWith { f1, f2 ->
                    val f1DateTime = "${f1.fcstDate}${f1.fcstTime}"
                    val f2DateTime = "${f2.fcstDate}${f2.fcstTime}"

                    return@sortWith f1DateTime.compareTo(f2DateTime)
                }

                if (list.isEmpty()) {
                    Log.e("WeatherRepository", "getVillageForecast: list is empty")
                    failureCallback(NullPointerException())
                } else {
                    Log.e("WeatherRepository", "getVillageForecast: $list")
                    successCallback(list)
                }
            }

            override fun onFailure(call: Call<WeatherEntity>, t: Throwable) {
                Log.e("WeatherRepository", "getVillageForecast: $t")
                failureCallback(t)
            }
        })
    }

    private fun transformRainType(forecast: ForecastEntity): String {
        return when (forecast.fcstValue.toInt()) {
            0 -> "없음"
            1 -> "비"
            2 -> "비/눈"
            3 -> "눈"
            4 -> "소나기"
            else -> ""
        }
    }

    private fun transformSky(forecast: ForecastEntity): String {
        return when (forecast.fcstValue.toInt()) {
            1 -> "맑음"
            3 -> "구름많음"
            4 -> "흐림"
            else -> ""
        }
    }
}