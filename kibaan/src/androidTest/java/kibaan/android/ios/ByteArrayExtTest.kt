package kibaan.android.ios

import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.Charset

@RunWith(AndroidJUnit4::class)
class ByteArrayExtTest {

    val text = "今わたしは誘惑を感ずるままに、クリスマスの逸話を記してみたいと思ふ。"
    val data = text.data(Charset.forName("UTF-8"))

    @Test
    fun testBase64EncodedStringShort() {
        val data = "あいう".data(Charset.forName("UTF-8"))
        assertEquals("44GC44GE44GG", data.base64EncodedString())
    }

    @Test
    fun testBase64EncodedString() {
        assertEquals("5LuK44KP44Gf44GX44Gv6KqY5oOR44KS5oSf44Ga44KL44G+44G+44Gr44CB44Kv44Oq44K544Oe44K544Gu6YC46Kmx44KS6KiY44GX44Gm44G/44Gf44GE44Go5oCd44G144CC",
            data.base64EncodedString())
    }

    @Test
    fun testBase64EncodedStringLineBreak() {
        assertEquals("5LuK44KP44Gf44GX44Gv6KqY5oOR44KS5oSf44Ga44KL44G+44G+44Gr44CB44Kv44Oq44K544Oe\r\n44K544Gu6YC46Kmx44KS6KiY44GX44Gm44G/44Gf44GE44Go5oCd44G144CC",
            data.base64EncodedString(arrayOf(Base64EncodingOptions.lineLength76Characters)))
    }

    @Test
    fun testBase64EncodedStringLineBreakLF() {
        assertEquals("5LuK44KP44Gf44GX44Gv6KqY5oOR44KS5oSf44Ga44KL44G+44G+44Gr44CB44Kv44Oq44K544Oe\n44K544Gu6YC46Kmx44KS6KiY44GX44Gm44G/44Gf44GE44Go5oCd44G144CC",
            data.base64EncodedString(arrayOf(Base64EncodingOptions.lineLength76Characters, Base64EncodingOptions.endLineWithLineFeed)))
    }
}
