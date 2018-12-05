package studios.aestheticapps.linker.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeHelper
{
    private const val DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss"
    private val formatter: SimpleDateFormat = SimpleDateFormat(DATE_TIME_FORMAT)

    fun getCurrentDateTimeStamp(): String = formatter.format(Date())
}