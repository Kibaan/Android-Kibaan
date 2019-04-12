package kibaan.android.storage

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import kibaan.android.ios.CGFloat
import kibaan.android.ios.StringEnumDefault
import kibaan.android.ios.enumerated
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalStorageTest {

    private var appContext: Context = InstrumentationRegistry.getTargetContext()

    val key = "key"

    @Test
    fun testString() {
        val setting = LocalStorage()
        val value = "testify"
        setting.setString(key, value = value, willSave = false)
        val result = setting.getString(key, defaultValue = "")
        assertEquals(value, result)
    }

    @Test
    fun testStringArray() {
        val setting = LocalStorage()
        val value = listOf("abc", "def", ",,", "\t")
        setting.setStringArray(key = key, value = value, willSave = false)
        val result = setting.getStringArray(key)
        assertEquals(value.size, result.size)
        value.enumerated().forEach { assertEquals(it.element, result[it.offset]) }
    }

    @Test
    fun testInt() {
        val setting = LocalStorage()
        val value = 12345
        setting.setInt(key, value = value, willSave = false)
        val result = setting.getInt(key, defaultValue = 0)
        assertEquals(value, result)
    }

    @Test
    fun testBool() {
        val setting = LocalStorage()
        setting.setBool("true", value = true, willSave = false)
        setting.setBool("false", value = false, willSave = false)
        assertEquals(setting.getBool("true"), true)
        assertEquals(setting.getBool("false"), false)
    }

    @Test
    fun testFloat() {
        val setting = LocalStorage()
        val value: CGFloat = 12.345678
        setting.setFloat(key, value = value, decimalLength = 2, willSave = false)
        val result = setting.getFloat(key)
        assertEquals(result, 12.35, 0.0)
    }

    @Test
    fun testEnum() {
        val setting = LocalStorage()
        val value: SampleEnum = SampleEnum.valueA
        setting.setEnum(key, value = value, willSave = false)
        val result = setting.getEnum(key, type = SampleEnum::class, defaultValue = SampleEnum.valueC)
        assertEquals(value, result)
    }

    @Test
    fun testEnumArray() {
        val setting = LocalStorage()
        val value: List<SampleEnum> = listOf(SampleEnum.valueA, SampleEnum.valueB, SampleEnum.valueC)
        setting.setEnumArray(key, value = value, willSave = false)
        val result = setting.getEnumArray(key, type = SampleEnum::class)
        assertEquals(value.size, result.size)
        value.enumerated().forEach { assertEquals(it.element, result[it.offset]) }
    }

    @Test
    fun testEnumOrNilArray() {
        val setting = LocalStorage()
        val value: List<SampleEnum?> = listOf(SampleEnum.valueA, SampleEnum.valueB, null, SampleEnum.valueC)
        setting.setEnumOrNilArray(key, value = value, willSave = false)
        val result = setting.getEnumOrNilArray(key, type = SampleEnum::class)
        assertEquals(value.size, result.size)
        value.enumerated().forEach { assertEquals(it.element, result[it.offset]) }
    }

    @Test
    fun testCodable() {
        val setting = LocalStorage()

        val obj = SampleClass("foo", 23)
        setting.setCodable(key, value = obj, willSave = false)

        val result = setting.getCodable(key, SampleClass("", 0))

        assertEquals(obj.name, result.name)
        assertEquals(obj.age, 23)
    }

    @Test
    fun testCodableList() {
        val setting = LocalStorage()

        val list = listOf(
                SampleClass("foo", 1),
                SampleClass("bar", 2),
                SampleClass("baz", 3)
        )
        setting.setCodable(key, value = list, willSave = false)

        val result = setting.getCodable<List<SampleClass>>(key, listOf())

        list.zip(result).forEach {
            assertEquals(it.first.name, it.second.name)
            assertEquals(it.first.age, it.second.age)
        }
    }

    @Test
    fun testCodableStringMap() {
        val setting = LocalStorage()

        val map = mapOf(
                "foo" to "1",
                "bar" to "2",
                "baz" to "3"
        )
        setting.setCodable(key, value = map, willSave = false)

        val result = setting.getCodable<Map<String, String>>(key, mapOf())

        map.entries.forEach {
            assertEquals(it.value, result[it.key])
        }
    }

    @Test
    fun testCodableObjectMap() {
        val setting = LocalStorage()

        val map: Map<String, SampleClass> = mapOf(
                "aaa" to SampleClass("foo", 1),
                "bbb" to SampleClass("bar", 2),
                "ccc" to SampleClass("baz", 3)
        )

        setting.setCodable(key, value = map, willSave = false)

        val result = setting.getCodable<Map<String, SampleClass>>(key, mapOf())

        assertEquals(SampleClass::class.java, result["aaa"]?.javaClass)

        map.entries.forEach {
            val origin = it.value
            val decoded = result[it.key]
            assertEquals(origin.name, decoded?.name)
            assertEquals(origin.age, decoded?.age)
        }
    }

    enum class SampleEnum : StringEnumDefault {
        valueA, valueB, valueC
    }

    data class SampleClass(val name: String, val age: Int)

}