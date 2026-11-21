package com.zaurh.notefirecloud.presentation.util

import android.content.Context
import android.text.format.DateUtils

/** "14:05" for today, "21 Nov" this year, "21 Nov 2023" otherwise — localized by the system. */
fun formatNoteDate(context: Context, millis: Long): String {
    val flags = when {
        DateUtils.isToday(millis) -> DateUtils.FORMAT_SHOW_TIME
        isThisYear(millis) -> DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH or DateUtils.FORMAT_NO_YEAR
        else -> DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH or DateUtils.FORMAT_SHOW_YEAR
    }
    return DateUtils.formatDateTime(context, millis, flags)
}

/** Full date and time, used in the editor footer. */
fun formatNoteDateTime(context: Context, millis: Long): String =
    DateUtils.formatDateTime(
        context, millis,
        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH or DateUtils.FORMAT_SHOW_TIME,
    )

private fun isThisYear(millis: Long): Boolean {
    val cal = java.util.Calendar.getInstance()
    val year = cal.get(java.util.Calendar.YEAR)
    cal.timeInMillis = millis
    return cal.get(java.util.Calendar.YEAR) == year
}
