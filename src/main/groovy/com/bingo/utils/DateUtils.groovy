package com.bingo.utils

import groovy.transform.CompileStatic

import java.text.DateFormat
import java.text.SimpleDateFormat

final class DateUtils {

    @CompileStatic
    final public static Date unixTimeToDate(final long unixTime) {
        return new Date(unixTime * 1000L)
    }

    @CompileStatic
    final public static Date getCurrentStartDate() {
        def currentDate = new Date().toCalendar()
        return new GregorianCalendar(
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0).time
    }

    @CompileStatic
    final public static String getDateString(final String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern)
        Date date = new Date()
        return dateFormat.format(date)
    }
}
