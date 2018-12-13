package kibaan.android.extension

import org.junit.Assert.assertEquals
import org.json.JSONObject
import org.junit.Test

class JSONObjectExtTest {

    @Test
    fun testStringOrNull() {
        val jsonObject = JSONObject()
        val value = "12345"
        jsonObject.put("data", value)
        assertEquals(value, jsonObject.getStringOrNull("data"))
        assertEquals(null, jsonObject.getStringOrNull("null"))
    }

    @Test
    fun testIntOrNull() {
        val jsonObject = JSONObject()
        jsonObject.put("data1", "12345")
        jsonObject.put("data2", "12345.56")
        jsonObject.put("data3", 123456789)
        jsonObject.put("data4", 1.52)
        assertEquals(12345, jsonObject.getIntOrNull("data1"))
        assertEquals(null, jsonObject.getIntOrNull("data2"))
        assertEquals(123456789, jsonObject.getIntOrNull("data3"))
        assertEquals(1, jsonObject.getIntOrNull("data4"))
        assertEquals(null, jsonObject.getIntOrNull("null"))
    }
}
