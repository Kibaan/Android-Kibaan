package kibaan.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtTest {

    // region -> SubScript

    @Test
    fun testSubscript_001() {
        assertEquals("12345"[0..0], "1")
    }

    @Test
    fun testSubscript_002() {
        assertEquals("12345"[0..3], "1234")
    }

    @Test
    fun testSubscript_003() {
        assertEquals("12345"[0..4], "12345")
    }

    @Test
    fun testSubscript_004() {
        assertEquals("12345"[0..5], null)
    }

    @Test
    fun testSubscript_005() {
        assertEquals("12345"[1..2], "23")
    }

    @Test
    fun testSubscript_011() {
        assertEquals("12345"[0 until 0], "")
    }

    @Test
    fun testSubscript_012() {
        assertEquals("12345"[0 until 3], "123")
    }

    @Test
    fun testSubscript_013() {
        assertEquals("12345"[0 until 4], "1234")
    }

    @Test
    fun testSubscript_014() {
        assertEquals("12345"[0 until 5], "12345")
    }

    @Test
    fun testSubscript_015() {
        assertEquals("12345"[0 until 6], null)
    }

    @Test
    fun testSubscript_016() {
        assertEquals("12345"[1 until 2], "2")
    }

    @Test
    fun testSubscript_017() {
        assertEquals("12345"[1 until 5], "2345")
    }

    // endregion

    // region -> Substring(from)

    @Test
    fun testSubstringFrom_001() {
        assertEquals("12345".substringFrom(from = 0), "12345")
    }

    @Test
    fun testSubstringFrom_002() {
        assertEquals("12345".substringFrom(from = 1), "2345")
    }

    @Test
    fun testSubstringFrom_003() {
        assertEquals("12345".substringFrom(from = 4), "5")
    }

    @Test
    fun testSubstringFrom_004() {
        assertEquals("12345".substringFrom(from = 5), "")
    }

    @Test
    fun testSubstringFrom_005() {
        assertEquals("12345".substringFrom(from = 6), null)
    }

    @Test
    fun testSubstringFrom_006() {
        assertEquals("12345".substringFrom(from = -1), null)
    }

    // endregion

    // region -> Substring(to)

    @Test
    fun testSubstringTo_001() {
        assertEquals("12345".substringTo(to = 0), "")
    }

    @Test
    fun testSubstringTo_002() {
        assertEquals("12345".substringTo(to = 2), "12")
    }

    @Test
    fun testSubstringTo_003() {
        assertEquals("12345".substringTo(to = 5), "12345")
    }

    @Test
    fun testSubstringTo_004() {
        assertEquals("12345".substringTo(to = 6), null)
    }

    @Test
    fun testSubstringTo_005() {
        assertEquals("12345".substringTo(to = -1), null)
    }

    // endregion

    // region -> Substring(from, length)

    @Test
    fun testSubstringFromLength_001() {
        assertEquals("12345".substring(from = 0, length = 0), "")
    }

    @Test
    fun testSubstringFromLength_002() {
        assertEquals("12345".substring(from = 0, length = 1), "1")
    }

    @Test
    fun testSubstringFromLength_003() {
        assertEquals("12345".substring(from = 0, length = 5), "12345")
    }

    @Test
    fun testSubstringFromLength_004() {
        assertEquals("12345".substring(from = 0, length = 6), null)
    }

    @Test
    fun testSubstringFromLength_005() {
        assertEquals("12345".substring(from = 1, length = 1), "2")
    }

    @Test
    fun testSubstringFromLength_006() {
        assertEquals("12345".substring(from = 4, length = 1), "5")
    }

    @Test
    fun testSubstringFromLength_007() {
        assertEquals("12345".substring(from = 4, length = 2), null)
    }

    @Test
    fun testSubstringFromLength_008() {
        assertEquals("12345".substring(from = 5, length = 2), null)
    }

    @Test
    fun testSubstringFromLength_009() {
        assertEquals("12345".substring(from = -1, length = 1), null)
    }

    @Test
    fun testSubstringFromLength_010() {
        assertEquals("12345".substring(from = 0, length = -1), null)
    }

    // endregion

    // region -> Format

    @Test
    fun testNumberFormat_001() {
        assertEquals("123".numberFormat, "123")
    }

    @Test
    fun testNumberFormat_002() {
        assertEquals("1234".numberFormat, "1,234")
    }

    @Test
    fun testNumberFormat_003() {
        assertEquals("1234567".numberFormat, "1,234,567")
    }

    @Test
    fun testNumberFormat_004() {
        assertEquals("AAA(1234)BBB(5678)".numberFormat, "AAA(1,234)BBB(5,678)")
    }

    @Test
    fun testNumberFormat_005() {
        assertEquals("+1234".numberFormat, "+1,234")
    }

    @Test
    fun testNumberFormat_006() {
        assertEquals("-1234".numberFormat, "-1,234")
    }

    @Test
    fun testNumberFormat_007() {
        assertEquals("+123456.789".signedNumberFormat, "+123,456.789")
    }

    @Test
    fun testNumberFormat_008() {
        assertEquals("-123456.789".signedNumberFormat, "-123,456.789")
    }

    @Test
    fun testNumberFormat_009() {
        assertEquals("-0.00".signedNumberFormat, "0.00")
    }

    @Test
    fun testNumberFormat_010() {
        assertEquals("+0.00".signedNumberFormat, "0.00")
    }

    @Test
    fun testNumberFormat_011() {
        assertEquals("-0".signedNumberFormat, "0")
    }

    @Test
    fun testNumberFormat_012() {
        assertEquals("+0".signedNumberFormat, "0")
    }

    @Test
    fun testNumberFormat_013() {
        assertEquals("123".signedNumberFormat, "+123")
    }

    @Test
    fun testNumberFormat_014() {
        assertEquals("1234".signedNumberFormat, "+1,234")
    }

    @Test
    fun testNumberFormat_015() {
        assertEquals("-123".signedNumberFormat, "-123")
    }

    @Test
    fun testNumberFormat_016() {
        assertEquals("-1234".signedNumberFormat, "-1,234")
    }

    @Test
    fun testPadLeft() {
        assertEquals("123".leftPadded(size = 6), "   123")
        assertEquals("123".leftPadded(size = 6, spacer = "0"), "000123")
        assertEquals("1234567".leftPadded(size = 6), "1234567")
    }

    @Test
    fun testPadRight() {
        assertEquals("123".rightPadded(size = 6), "123   ")
        assertEquals("123".rightPadded(size = 6, spacer = "0"), "123000")
        assertEquals("1234567".rightPadded(size = 6), "1234567")
    }

    // endregion

    // region -> SnakeCase

    @Test
    fun testSnakeCase() {
        assertEquals("StringUtilsTests".toSnakeCase(), "string_utils_tests")
        assertEquals("AAABBBCCC".toSnakeCase(), "aaabbbccc")
        assertEquals("abcDefGhiJk123".toSnakeCase(), "abc_def_ghi_jk123")
    }

    // endregion


    @Test
    fun testSHA1() {
        val src = "ABC"
        assertEquals("3c01bdbb26f358bab27f267924aa2c9a03fcfdb8", src.sha1())
    }

    @Test
    fun testSHA256() {
        val src = "ABC"
        assertEquals("b5d4045c3f466fa91fe2cc6abe79232a1a57cdf104f7a26e716e0a1e2789df78", src.sha256())
    }

    @Test
    fun testLiteralEscaped() {
        assertEquals("ABC".literalEscaped, "ABC")
        assertEquals("\r".literalEscaped, "")
        assertEquals("\n".literalEscaped, "\\n")
        assertEquals("\t".literalEscaped, "\\t")
        assertEquals("\"".literalEscaped, "\\\"")
        assertEquals("\'".literalEscaped, "\\\'")
        assertEquals("\r\n\t\"\'".literalEscaped, "\\n\\t\\\"\\\'")
    }

}
