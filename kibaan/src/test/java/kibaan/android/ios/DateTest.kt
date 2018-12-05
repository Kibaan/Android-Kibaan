package kibaan.android.ios

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat

class DateTest {

    @Test
    fun testCompare() {
        val format = SimpleDateFormat("yyyy-MM-dd")

        val date1 = format.parse("2018-01-01")
        val date2 = format.parse("2018-02-01")
        val date3 = format.parse("2018-01-01")

        assertEquals(ComparisonResult.orderedSame, date1.compare(date3))
        assertEquals(ComparisonResult.orderedAscending, date1.compare(date2))
        assertEquals(ComparisonResult.orderedDescending, date2.compare(date1))
    }

}
