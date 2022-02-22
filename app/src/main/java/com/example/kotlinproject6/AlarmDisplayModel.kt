package com.example.kotlinproject6

data class AlarmDisplayModel (
    val hour : Int,
    val minute : Int,
    var onOff : Boolean
    ){

    val timeText : String
        get(){
            val h = "%02d".format( if( hour < 12 ) hour else hour - 12)
            val m = "%02d".format( minute )
            return "$h:$m"
        }

    val ampmText : String
        get(){
            if (hour > 12)
                return "PM"
            else
                return "AM"
        }
    fun makeDataForDB():String{
        return "$hour:$minute"
    }
}

