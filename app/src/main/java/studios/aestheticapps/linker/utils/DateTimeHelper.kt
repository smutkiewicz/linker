package studios.aestheticapps.linker.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeHelper
{
    const val DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss"

    private const val DATE_NEVER = "1996/01/26 12:00:00"
    private const val MONTH_DAY_FORMAT = "MMM dd"
    private val simpleDateFormatter: SimpleDateFormat = SimpleDateFormat(DATE_TIME_FORMAT)
    private val monthDayFormatter: SimpleDateFormat = SimpleDateFormat(MONTH_DAY_FORMAT)

    fun getCurrentDateTimeStamp()= simpleDateFormatter.format(Date())

    fun getDateTimeNever() = DATE_NEVER

    fun getMonthAndDay(dateTime: String): String
    {
        val dateValue = simpleDateFormatter.parse(dateTime)
        return monthDayFormatter.format(dateValue)
    }
}