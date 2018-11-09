package jp.co.altonotes.android.kibaan.extension

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Dates {

    /**
     * フォーマットを指定してDateオブジェクトを作成する
     */
    fun create(string: String?, format: String): Date? {
        val string = string ?: return null

        return try {
            SimpleDateFormat(format, Locale.US).parse(string)
        } catch (e: ParseException) {
            null
        }
    }

}

/**
 * フォーマットを指定してDateを文字列にする
 */
fun Date.string(format: String): String {
    val result = SimpleDateFormat(format, Locale.US).format(this)
    return result ?: ""
}

/**
 * 年を加減したDateオブジェクトを作成する
 */
fun Date.yearAdded(value: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.YEAR, value)
    return calendar.time
}

/**
 * 月を加減したDateオブジェクトを作成する
 */
fun Date.monthAdded(value: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.MONTH, value)
    return calendar.time
}

/**
 * 日を加減したDateオブジェクトを作成する
 */
fun Date.dayAdded(value: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DAY_OF_MONTH, value)
    return calendar.time
}

/**
 * 秒を加減したDateオブジェクトを作成する
 */
fun Date.secondAdded(value: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.SECOND, value)
    return calendar.time
}

/**
 * 指定した日付時刻から何ミリ秒後かを取得する
 */
fun Date.countMilliSeconds(from: Date): Long {
    return time - from.time
}

/**
 * 指定した日付時刻から何秒後かを取得する
 */
fun Date.countSeconds(from: Date): Long {
    return (time - from.time) / 1000
}