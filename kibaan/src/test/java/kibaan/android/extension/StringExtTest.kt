@file:Suppress("SpellCheckingInspection")

package kibaan.android.extension

import org.junit.Assert.*
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

    // region -> Remove

    @Test
    fun testRemove() {
        assertEquals("TestTest", "TestATest".remove(of = "A"))
        assertEquals("TestTest", "TestATestA".remove(of = "A"))
    }

    @Test
    fun testRemoveAll() {
        assertEquals("TestATestBC", "TestATestBC".removeAll(listOf()))
        assertEquals("TestATestB", "TestATestBC".removeAll(listOf("C")))
        assertEquals("TestTestC", "TestATestBC".removeAll(listOf("A", "B")))
    }

    // endregion

    // region -> xxxxValue

    @Test
    fun testDoubleValue() {
        assertEquals(0.1, "0.1".doubleValue, 0.0)
        assertEquals(12345.0, "12345".doubleValue, 0.0)
        assertEquals(-1.54, "-1.54".doubleValue, 0.0)
        assertEquals(2.9584, "+2.9584".doubleValue, 0.0)
        assertEquals(0.0, "29.94.24".doubleValue, 0.0)
        assertEquals(0.0, "abc".doubleValue, 0.0)
    }

    @Test
    fun testFloatValue() {
        assertEquals(0.1, "0.1".floatValue, 0.0)
        assertEquals(12345.0, "12345".floatValue, 0.0)
        assertEquals(-1.54, "-1.54".floatValue, 0.0)
        assertEquals(2.9584, "+2.9584".floatValue, 0.0)
        assertEquals(0.0, "29.94.24".floatValue, 0.0)
        assertEquals(0.0, "abc".floatValue, 0.0)
    }

    @Test
    fun testIntegerValue() {
        assertEquals(" 111/222".integerValue, 111)
        assertEquals("111".integerValue, 111)
        assertEquals("222.000".integerValue, 222)
        assertEquals("-333".integerValue, -333)
        assertEquals("+444".integerValue, 444)
        assertEquals("ー555".integerValue, 0)
        assertEquals("＋666".integerValue, 0)
        assertEquals("   777".integerValue, 777)
        assertEquals("\n\n888".integerValue, 888)
        assertEquals(" 999 000".integerValue, 999)
        assertEquals("   +111   ".integerValue, 111)
        assertEquals("   -222   ".integerValue, -222)
        assertEquals("3E+2".integerValue, 3)
        assertEquals(" ".integerValue, 0)
    }

    @Test
    fun testLongValue() {
        assertEquals(123, "123".longValue)
        assertEquals(-1500, "-1500".longValue)
        assertEquals(999, "+999".longValue)
        assertEquals(0, "123.5".longValue)
        assertEquals(0, "abc".longValue)
    }

    // endregion

    // region -> Others

    @Test
    fun testHasAnyPrefix() {
        assertTrue("+123".hasAnyPrefix(listOf("+")))
        assertTrue("+123".hasAnyPrefix(listOf("-", "+")))
        assertFalse("123".hasAnyPrefix(listOf("-")))
        assertFalse("123".hasAnyPrefix(listOf("-", "+")))
    }

    @Test
    fun testAnyPrefix() {
        assertEquals("+", "+395".anyPrefix(listOf("+", "-", "/")))
        assertEquals(null, "395".anyPrefix(listOf("+", "-", "/")))
        assertEquals("/", "/+395".anyPrefix(listOf("+", "-", "/")))
        assertEquals(null, "".anyPrefix(listOf("+", "-", "/")))
    }

    @Test
    fun testIsNumber() {
        val nullValue: String? = null
        assertTrue("1".isNumber)
        assertTrue("1.5".isNumber)
        assertTrue("-30.2".isNumber)
        assertTrue("+14.90".isNumber)
        assertTrue("913953285239".isNumber)
        assertFalse("935325a".isNumber)
        assertFalse("anfd".isNumber)
        assertFalse(nullValue.isNumber)
    }

    @Test
    fun testEnclosed() {
        assertEquals("9583", "(9583)".enclosed("(", ")"))
        assertEquals("9583", "指値(9583)です".enclosed("(", ")"))
        assertEquals("1,000円", "指値(1,000円)/ｽﾀﾝﾀﾞｰﾄﾞ".enclosed("(", ")"))
        assertEquals("これで良いのか？", "指値(これで良いのか？".enclosed("(", ")"))
        assertEquals(null, "こっちはダメだと思う)です".enclosed("(", ")"))
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

    // region -> Split(Left)

    @Test
    fun testSplitLeftSimple() {
        val result = "123456789".splitFromLeft(length = 3)
        assertEquals(3, result.size)
        assertEquals("123", result[0])
        assertEquals("456", result[1])
        assertEquals("789", result[2])
    }

    @Test
    fun testSplitLeftShortOne() {
        val result = "12".splitFromLeft(length = 3)
        assertEquals(1, result.size)
        assertEquals("12", result[0])
    }

    @Test
    fun testSplitLeftShortMulti() {
        val result = "AAAABBBBCC".splitFromLeft(length = 4)
        assertEquals(3, result.size)
        assertEquals("AAAA", result[0])
        assertEquals("BBBB", result[1])
        assertEquals("CC", result[2])
    }

    @Test
    fun testSplitLeftBlank() {
        val result = "".splitFromLeft(length = 4)
        assertEquals(1, result.size)
        assertEquals("", result[0])
    }

    // endregion

    // region -> Split(Right)

    @Test
    fun testSplitRightSimple() {
        val result = "123456789".splitFromRight(length = 3)
        assertEquals(3, result.size)
        assertEquals("123", result[0])
        assertEquals("456", result[1])
        assertEquals("789", result[2])
    }

    @Test
    fun testSplitRightShortOne() {
        val result = "12".splitFromRight(length = 3)
        assertEquals(1, result.size)
        assertEquals("12", result[0])
    }

    @Test
    fun testSplitRightShortMulti() {
        val result = "AAAABBBBCC".splitFromRight(length = 4)
        assertEquals(3, result.size)
        assertEquals("AA", result[0])
        assertEquals("AABB", result[1])
        assertEquals("BBCC", result[2])
    }

    @Test
    fun testSplitRightBlank() {
        val result = "".splitFromRight(length = 4)
        assertEquals(1, result.size)
        assertEquals("", result[0])
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
    fun testLiteralEscaped() {
        assertEquals("ABC".literalEscaped, "ABC")
        assertEquals("\r".literalEscaped, "")
        assertEquals("\n".literalEscaped, "\\n")
        assertEquals("\t".literalEscaped, "\\t")
        assertEquals("\"".literalEscaped, "\\\"")
        assertEquals("\'".literalEscaped, "\\\'")
        assertEquals("\r\n\t\"\'".literalEscaped, "\\n\\t\\\"\\\'")
    }

    @Test
    fun testLocalizedString() {
        assertEquals("Context is null", "test".localizedString)
    }

    @Test
    fun testIsEmpty() {
        val nullValue: String? = null
        assertTrue("".isEmpty)
        assertTrue(nullValue.isEmpty)
        assertFalse("1".isEmpty)
    }

    @Test
    fun testIsNotEmpty() {
        val nullValue: String? = null
        assertTrue("1".isNotEmpty)
        assertFalse("".isNotEmpty)
        assertFalse(nullValue.isNotEmpty)
    }

    @Test
    fun testEmptyConverted() {
        val nullValue: String? = null
        assertEquals("--", "".emptyConverted("--"))
        assertEquals("--", nullValue.emptyConverted("--"))
        assertEquals("123", "123".emptyConverted("--"))
    }

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
    fun testUrlEncoded() {
        assertEquals("%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af".toUpperCase(), "こんにちは".urlEncoded)
        assertEquals(
            "URL%e3%82%a8%e3%83%b3%e3%82%b3%e3%83%bc%e3%83%89%e3%81%ae%e3%83%86%e3%82%b9%e3%83%88%e3%81%a7%e3%81%99".toUpperCase(),
            "URLエンコードのテストです".urlEncoded
        )
    }

    @Test
    fun testUrlDecoded() {
        assertEquals("こんにちは", "%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af".urlDecoded)
        assertEquals(
            "URLエンコードのテストです",
            "URL%e3%82%a8%e3%83%b3%e3%82%b3%e3%83%bc%e3%83%89%e3%81%ae%e3%83%86%e3%82%b9%e3%83%88%e3%81%a7%e3%81%99".urlDecoded
        )
    }
}
