package com.example.fastcampusbasic.Part2.chapter7

data class Forecast (
    val fcstDate: String,
    val fcstTime: String,

    var temperature: Double = 0.0,
    var sky: String = "",
    var precipitation: Int = 0,
    var precipitationType: String = "",
) {

    val weather: String
        get() {
            return if(precipitationType == "" || precipitationType == "없음") {
                sky
            } else {
                precipitationType
            }
        }
}