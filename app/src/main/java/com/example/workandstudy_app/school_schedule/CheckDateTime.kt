package com.example.workandstudy_app.school_schedule

class CheckDateTime {

    fun checktimeStartEnd(tgbatdau: String, tgketthuc: String): Boolean {
        val tgBegin = timetoMinutes(tgbatdau)
        val tgEnd = timetoMinutes(tgketthuc)
        if (tgBegin < tgEnd) {
            return true
        }
        return false
    }

    fun timetoMinutes(tg: String): Int {
        val (hour, minute) = tg.split(":").map { it.toInt() }
        return hour * 60 + minute
    }

    fun checkTime(hour: Int, minute: Int): String {
        if (hour < 10 && minute < 10) {
            return "0$hour:0$minute"
        } else {
            if (hour < 10) {
                return "0$hour:$minute"
            }
            if (minute < 10) {
                return "$hour:0$minute"
            }
        }
        return "$hour:$minute"
    }
}