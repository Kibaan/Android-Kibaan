package kibaan.android.ios

import kibaan.android.extension.Dates
import org.junit.Assert
import org.junit.Test
import java.util.*

class DateExtTest {

    @Test
    fun testCompareDate() {
        val format = "yyyymmdd"
        Assert.assertTrue(create("20180102", format)?.compare(create("20180101", format)!!) == ComparisonResult.orderedDescending)
        Assert.assertTrue(create("20180101", format)?.compare(create("20180101", format)!!) == ComparisonResult.orderedSame)
        Assert.assertTrue(create("20180101", format)?.compare(create("20180102", format)!!) == ComparisonResult.orderedAscending)
    }

    @Test
    fun testCompareMonth() {
        val format = "mm"
        Assert.assertTrue(create("02", format)?.compare(create("01", format)!!) == ComparisonResult.orderedDescending)
        Assert.assertTrue(create("01", format)?.compare(create("01", format)!!) == ComparisonResult.orderedSame)
        Assert.assertTrue(create("01", format)?.compare(create("02", format)!!) == ComparisonResult.orderedAscending)
    }

    @Test
    fun testCompareDay() {
        val format = "dd"
        Assert.assertTrue(create("11", format)?.compare(create("10", format)!!) == ComparisonResult.orderedDescending)
        Assert.assertTrue(create("10", format)?.compare(create("10", format)!!) == ComparisonResult.orderedSame)
        Assert.assertTrue(create("10", format)?.compare(create("11", format)!!) == ComparisonResult.orderedAscending)
    }

    @Test
    fun testCompareHour() {
        val format = "HH"
        Assert.assertTrue(create("12", format)?.compare(create("11", format)!!) == ComparisonResult.orderedDescending)
        Assert.assertTrue(create("11", format)?.compare(create("11", format)!!) == ComparisonResult.orderedSame)
        Assert.assertTrue(create("11", format)?.compare(create("12", format)!!) == ComparisonResult.orderedAscending)
    }

    @Test
    fun testCompareMinute() {
        val format = "HH:mm"
        Assert.assertTrue(create("12:30", format)?.compare(create("12:20", format)!!) == ComparisonResult.orderedDescending)
        Assert.assertTrue(create("12:20", format)?.compare(create("12:20", format)!!) == ComparisonResult.orderedSame)
        Assert.assertTrue(create("12:20", format)?.compare(create("12:30", format)!!) == ComparisonResult.orderedAscending)
    }

    @Test
    fun testCompareSecond() {
        val format = "HH:mm:ss"
        Assert.assertTrue(create("12:30:45", format)?.compare(create("12:30:35", format)!!) == ComparisonResult.orderedDescending)
        Assert.assertTrue(create("12:30:35", format)?.compare(create("12:30:35", format)!!) == ComparisonResult.orderedSame)
        Assert.assertTrue(create("12:30:35", format)?.compare(create("12:30:45", format)!!) == ComparisonResult.orderedAscending)
    }

    private fun create(string: String, format: String): Date? {
        return Dates.create(string, format)
    }
}