package com.cralos.cleanarchitecture.business.domain.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUtil
@Inject
constructor(private val dateFormat: SimpleDateFormat) {

    //date format: "2019-05-17 HH:mm:ss"
    //2019-05-17

    fun removeTimeFromDateString(sd: String): String {
        // quitamos el esapcio -> "2019-05-17 HH:mm:ss"
        return sd.substring(0, sd.indexOf(" "))
    }

    fun convertFirebaseTimestampToStringData(timestamp: Timestamp): String {
        return dateFormat.format(timestamp.toDate())
    }

    fun convertStringDateToFirebaseTimestamp(date: String): Timestamp {
        return Timestamp(dateFormat.parse(date))
    }

    fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

}