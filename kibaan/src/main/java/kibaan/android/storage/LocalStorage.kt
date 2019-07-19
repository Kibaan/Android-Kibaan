package kibaan.android.storage

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kibaan.android.extension.isNotEmpty
import kibaan.android.framework.SmartActivity
import kibaan.android.ios.CGFloat
import kibaan.android.ios.StringEnum
import kibaan.android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.reflect.KClass


/**
 * 端末に保存するローカル設定
 */
open class LocalStorage {
    var items: MutableMap<String, String?> = mutableMapOf()
    val fileName: String

    val context: Context?
        get() = SmartActivity.shared

    constructor(fileName: String, load: Boolean = true) {
        this.fileName = fileName
        if (load) {
            loadItems()
        }
    }

    constructor(load: Boolean = true) {
        // 引数なしコンストラクタではクラス名をファイル名とする
        fileName = this::class.simpleName.toString()
        if (load) {
            loadItems()
        }
    }

    constructor(other: LocalStorage) {
        fileName = other.fileName
        items = other.items.toMutableMap()
    }

    /**
     * ファイルからデータを読み込む
     */
    private fun loadItems() {

        var jsonString: String? = null
        try {
            jsonString = context?.openFileInput(filePath)?.bufferedReader().use {
                it?.readText()
            }
        } catch (e: FileNotFoundException) {
            // 初回はファイルがないので何もしない
        } catch (e: java.lang.Exception) {
            Log.e(javaClass.simpleName, "JSON data may be broken.", e)
        }

        if (jsonString.isNotEmpty) {
            val jsonObject = JSONObject(jsonString)
            val keys = jsonObject.keys()

            while (keys.hasNext()) {
                val key = keys.next()
                var item: String? = null
                if (!jsonObject.isNull(key)) {
                    try {
                        item = jsonObject.getString(key)
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }
                items[key] = item
            }
        }
    }

    /**
     * ファイルにデータを保存する
     */
    fun save() {
        val jsonObject = JSONObject()
        items.forEach {
            jsonObject.put(it.key, it.value)
        }
        val jsonString = jsonObject.toString()

        try {
            val output = SmartActivity.shared?.openFileOutput(filePath, Context.MODE_PRIVATE)
            if (output != null) {
                PrintWriter(output).use { writer ->
                    writer.print(jsonString)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun overwriteItems(other: LocalStorage) {
        items = other.items.toMutableMap()
    }

    // region -> String

    fun getString(key: String, defaultValue: String): String {
        val value = getStringOrNil(key)
        if (value != null) {
            return value
        }
        return defaultValue
    }

    fun getStringOrNil(key: String): String? {
        return items[key]
    }

    fun setString(key: String, value: String, willSave: Boolean = true) {
        setItem(key, value, willSave)
    }

    fun setStringOrNil(key: String, value: String?, willSave: Boolean = true) {
        setItem(key, value, willSave)
    }

    // endregion

    // region -> String Array
    fun getStringArray(key: String, defaultValue: List<String> = listOf()): List<String> {
        val array = getStringArrayOrNil(key)
        if (array != null) {
            return array
        }
        return defaultValue
    }

    fun getStringArrayOrNil(key: String): List<String>? {
        val string = items[key]

        if (string != null) {
            val result = mutableListOf<String>()
            val jsonArray = JSONArray(string)

            val count = jsonArray.length()
            for (i in 0 until count) {
                result.add(jsonArray.get(i) as String)
            }
            return result
        }
        return null
    }

    fun setStringArray(key: String, value: List<String>, willSave: Boolean = true) {
        val jsonArray = JSONArray()
        value.forEach {
            jsonArray.put(it)
        }
        setItem(key, jsonArray.toString(), willSave)
    }

    // endregion

    // region -> Bool

    fun getBool(key: String, defaultValue: Boolean = false): Boolean {
        val value = getBoolOrNil(key)
        if (value != null) {
            return value
        }
        return defaultValue
    }

    fun getBoolOrNil(key: String): Boolean? {
        return stringToBool(items[key])
    }

    fun setBool(key: String, value: Boolean, willSave: Boolean = true) {
        setItem(key, boolToString(value), willSave)
    }

    fun setBoolOrNil(key: String, value: Boolean, willSave: Boolean = true) {
        setItem(key, boolToString(value), willSave)
    }

    // endregion

    // region -> Int

    fun getInt(key: String, defaultValue: Int = 0): Int {
        val value = getIntOrNil(key)
        if (value != null) {
            return value
        }
        return defaultValue
    }

    fun getIntOrNil(key: String): Int? {
        val string = items[key]
        if (string != null) {
            return string.toIntOrNull()
        }
        return null
    }

    fun setInt(key: String, value: Int, willSave: Boolean = true) {
        setItem(key, value.toString(), willSave)
    }

    fun setIntOrNil(key: String, value: Int?, willSave: Boolean = true) {
        setItem(key, value?.toString(), willSave)
    }

    // endregion

    // region -> Float

    fun getFloat(key: String, defaultValue: CGFloat = 0.0): CGFloat {
        val value = getFloatOrNil(key)
        if (value != null) {
            return value
        }
        return defaultValue
    }

    fun getFloatOrNil(key: String): CGFloat? {
        val string = items[key]
        val d = string?.toDoubleOrNull()
        if (d != null) {
            return d
        }
        return null
    }

    fun setFloat(key: String, value: CGFloat, decimalLength: Int, willSave: Boolean = true) {
        val decimal = BigDecimal(value).setScale(decimalLength, RoundingMode.HALF_UP)
        setItem(key, decimal.toString(), willSave)
    }

    fun setFloatOrNil(key: String, value: CGFloat?, decimalLength: Int, willSave: Boolean = true) {
        if (value != null) {
            setFloat(key, value, decimalLength, willSave)
        }
        setItem(key, null, willSave)
    }

    // endregion

    // region -> Enum

    /** rawValueがStringの enumを保存する */
    inline fun <reified T> getEnum(key: String, type: KClass<T>, defaultValue: T): T where T : Enum<T>, T : StringEnum {
        val value = getEnumOrNil(key, type = type)
        if (value != null) {
            return value
        }
        return defaultValue
    }

    inline fun <reified T> getEnumOrNil(key: String, type: KClass<T>): T? where T : Enum<T>, T : StringEnum {
        val string = items[key]
        if (string != null) {
            return makeEnum(type, string)
        }
        return null
    }

    inline fun <reified T> setEnum(key: String, value: T, willSave: Boolean = true) where T : Enum<T>, T : StringEnum {
        setItem(key, value.rawValue, willSave)
    }

    inline fun <reified T> setEnumOrNil(key: String, value: T?, willSave: Boolean = true) where T : Enum<T>, T : StringEnum {
        setItem(key, value?.rawValue, willSave)
    }

    // endregion

    // region -> Enum Array
    inline fun <reified T> getEnumArray(key: String, type: KClass<T>, defaultValue: List<T> = listOf()): List<T> where T : Enum<T>, T : StringEnum {
        val value = getStringArrayOrNil(key)
        if (value != null) {
            return value.mapNotNull { makeEnum(type, it) }
        }
        return defaultValue
    }

    inline fun <reified T> setEnumArray(key: String, value: List<T>, willSave: Boolean = true) where T : Enum<T>, T : StringEnum {
        val rawValues = value.map { it.rawValue }
        setStringArray(key, rawValues, willSave)
    }

    // endregion

    // region -> Enum or nil Array
    inline fun <reified T> getEnumOrNilArray(key: String, type: KClass<T>, defaultValue: List<T?> = listOf()): List<T?> where T : Enum<T>, T : StringEnum {
        val stringArray = getStringArrayOrNil(key)
        if (stringArray != null) {
            return stringArray.map { makeEnum(type, it) }
        }
        return defaultValue
    }

    inline fun <reified T> setEnumOrNilArray(key: String, value: List<T?>, willSave: Boolean = true) where T : Enum<T>, T : StringEnum {
        setStringArray(key, value.map { it?.rawValue ?: "" }, willSave)
    }

    // endregion


    // region -> Codable
    inline fun <reified T> getCodable(key: String, defaultValue: T) : T where T : Any {
        val string = getStringOrNil(key) ?: return defaultValue
        val type = object: TypeToken<T>() {}.type
        return Gson().fromJson(string, type)
    }

    inline fun <reified T> getCodableOrNil(key: String) : T? where T : Any {
        val string = getStringOrNil(key) ?: return null
        val type = object: TypeToken<T>() {}.type
        return Gson().fromJson(string, type)
    }

    fun setCodable(key: String, value: Any, willSave: Boolean = true) {
        val json = Gson().toJson(value)
        setString(key, json, willSave)

    }
    // endregion

    fun setItem(key: String, value: String?, willSave: Boolean) {
        items[key] = value
        if (willSave) {
            save()
        }
    }

    inline fun <reified T> makeEnum(type: KClass<T>, rawValue: String): T? where T : Enum<T>, T : StringEnum {
        return enumValues<T>().firstOrNull { item -> item.rawValue == rawValue }
    }

    /// bool値をStringに変換する
    private fun boolToString(value: Boolean?): String? {
        if (value != null) {
            return if (value) "true" else "false"
        }
        return null
    }

    /// bool値をStringに変換する
    private fun stringToBool(value: String?): Boolean? {
        if (value != null) {
            if (value == "true") {
                return true
            } else if (value == "false") {
                return false
            }
        }
        return null
    }

    // region -> Computed property

    val filePath: String
        get() = fileName

    // endregion
}