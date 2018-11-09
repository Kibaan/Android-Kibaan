package jp.co.altonotes.android.kibaan.extension

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class DateExtTest {

    private val calendar = Calendar.getInstance()
    private val dayMinute: Long = 60 * 60 * 24

    @Test
    fun testCreateAndString_yyyy() {
        val testData = "2018"
        val testFormat = "yyyy"
        val date = create(string = testData, format = testFormat) ?: return
        calendar.time = date
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(testData, date.string(format = testFormat))
    }

    @Test
    fun testCreateAndString_yyyyMM() {
        val testData = "201809"
        val testFormat = "yyyyMM"
        val date = create(string = testData, format = testFormat) ?: return
        calendar.time = date
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(9 - 1, calendar.get(Calendar.MONTH))
        assertEquals(testData, date.string(format = testFormat))
    }

    @Test
    fun testCreateAndString_yyyyMMdd() {
        val testData = "20180912"
        val testFormat = "yyyyMMdd"
        val date = create(string = testData, format = testFormat) ?: return
        calendar.time = date
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(9 - 1, calendar.get(Calendar.MONTH))
        assertEquals(12, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(testData, date.string(format = testFormat))
    }

    @Test
    fun testCreateAndString_yyyyMMddHH() {
        val testData = "2018091214"
        val testFormat = "yyyyMMddHH"
        val date = create(string = testData, format = testFormat) ?: return
        calendar.time = date
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(9 - 1, calendar.get(Calendar.MONTH))
        assertEquals(12, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(14, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calendar.get(Calendar.MINUTE))
        assertEquals(testData, date.string(format = testFormat))
    }

    @Test
    fun testCreateAndString_yyyyMMddHHmm() {
        val testData = "201808061200"
        val testFormat = "yyyyMMddHHmm"
        val date = create(string = testData, format = testFormat) ?: return
        calendar.time = date
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(8 - 1, calendar.get(Calendar.MONTH))
        assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(12, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calendar.get(Calendar.MINUTE))
        assertEquals(testData, date.string(format = testFormat))
    }

    @Test
    fun testCreateAndString_yyyy_MM_ddHH_mm_セパレータあり() {
        val testData = "2018/09/12 14:00"
        val testFormat = "yyyy/MM/dd HH:mm"
        val date = create(string = testData, format = testFormat) ?: return
        calendar.time = date
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(9 - 1, calendar.get(Calendar.MONTH))
        assertEquals(12, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(14, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calendar.get(Calendar.MINUTE))
        assertEquals(testData, date.string(format = testFormat))
    }

    @Test
    fun testCreateAndString_yyyy_MM_ddHH_mm_日本語含む() {
        val testData = "2018年09月12日 14時00分"
        val testFormat = "yyyy年MM月dd日 HH時mm分"
        val date = create(string = testData, format = testFormat) ?: return
        calendar.time = date
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(9 - 1, calendar.get(Calendar.MONTH))
        assertEquals(12, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(14, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calendar.get(Calendar.MINUTE))
        assertEquals(testData, date.string(format = testFormat))
    }

    @Test
    fun testYearAdded_プラス１() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.yearAdded(1)
        assertEquals(2019, calendar.get(Calendar.YEAR))
    }

    @Test
    fun testYearAdded_プラス１００() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.yearAdded(100)
        assertEquals(2118, calendar.get(Calendar.YEAR))
    }

    @Test
    fun testYearAdded_マイナス１() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.yearAdded(-1)
        assertEquals(2017, calendar.get(Calendar.YEAR))
    }

    @Test
    fun testYearAdded_マイナス１００() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.yearAdded(-100)
        assertEquals(1918, calendar.get(Calendar.YEAR))
    }

    @Test
    fun testYearAdded_プラス０() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.yearAdded(0)
        assertEquals(2018, calendar.get(Calendar.YEAR))
    }

    @Test
    fun testMonthAdded_プラス１() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.monthAdded(1)
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(9 - 1, calendar.get(Calendar.MONTH))
        assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun testMonthAdded_プラス１２() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.monthAdded(12)
        assertEquals(2019, calendar.get(Calendar.YEAR))
        assertEquals(8 - 1, calendar.get(Calendar.MONTH))
        assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun testMonthAdded_マイナス１() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.monthAdded(-1)
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(7 - 1, calendar.get(Calendar.MONTH))
        assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun testMonthAdded_マイナス１２() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.monthAdded(-12)
        assertEquals(2017, calendar.get(Calendar.YEAR))
        assertEquals(8 - 1, calendar.get(Calendar.MONTH))
        assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun testDayAdded_プラス１() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.dayAdded(1)
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(8 - 1, calendar.get(Calendar.MONTH))
        assertEquals(7, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun testDayAdded_プラス月加算() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.dayAdded(26)
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(9 - 1, calendar.get(Calendar.MONTH))
        assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun testDayAdded_マイナス１() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.dayAdded(-1)
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(8 - 1, calendar.get(Calendar.MONTH))
        assertEquals(5, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun testDayAdded_マイナス月減算() {
        val date = create(string = "201808061200", format = "yyyyMMddHHmm") ?: return
        calendar.time = date.dayAdded(-6)
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(7 - 1, calendar.get(Calendar.MONTH))
        assertEquals(31, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun testCountMilliSeconds_normal() {
        val fromDate = Dates.create(string = "201810101200", format = "yyyyMMddHHmm") ?: return
        val toDate = fromDate.secondAdded(value = 1)
        val second = toDate.countMilliSeconds(from = fromDate)
        assertEquals(1000, second)
    }

    @Test
    fun testCountMilliSeconds_nextDay() {
        val fromDate = Dates.create(string = "201810101200", format = "yyyyMMddHHmm") ?: return
        val toDate = fromDate.dayAdded(1)
        val second = toDate.countMilliSeconds(from = fromDate)
        assertEquals(dayMinute * 1000, second)
    }

    @Test
    fun testCountSeconds_normal() {
        val fromDate = Dates.create(string = "201810101200", format = "yyyyMMddHHmm") ?: return
        val toDate = fromDate.secondAdded(value = 10)
        val second = toDate.countSeconds(from = fromDate)
        assertEquals(10, second)
    }

    fun testCountSeconds_nextDay() {
        val fromDate = Dates.create(string = "201810101200", format = "yyyyMMddHHmm") ?: return
        val toDate = fromDate.dayAdded(1)
        val second = toDate.countSeconds(from = fromDate)
        assertEquals(dayMinute, second)
    }

    @Test
    fun testDateCreate() {
        val src = "2018-01-01"
        val format = "yyyy-MM-dd"
        val formatter = SimpleDateFormat(format, Locale.US)

        assertEquals(formatter.parse(src), Dates.create(src, format))

        val date = Dates.create("じゅげむ", "でたらめ")
        assertNull(date)
    }

    private fun create(string: String, format: String): Date? {
        return Dates.create(string, format)
    }
}
