package kibaan.android.extension

import kibaan.android.framework.SmartActivity
import kibaan.android.ios.*
import java.lang.StringBuilder
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest

/**
 * スネークケースに変換する
 */
fun String.toSnakeCase(): String {
    var canGoNextBlock = false
    return map {
        val separator = if (canGoNextBlock && it.isUpperCase()) "_" else ""
        canGoNextBlock = it.isLowerCase()
        separator + it.toLowerCase()
    }.joinToString("")
}

/**
 * Subscript
 */
operator fun String.get(range: IntRange): String? {
    if (count <= range.first || count <= range.last) {
        return null
    }
    return substring(range)
}

/**
 * 3...
 */
fun String.substringFrom(from: Int): String? {
    if (this.count < from || from < 0) {
        return null
    }
    return substring(startIndex = from)
}

/**
 * ...3
 */
fun String.substringTo(to: Int): String? {
    if (this.count < to || to < 0) {
        return null
    }
    return substring(startIndex = 0, endIndex = to)
}

/**
 * fromで指定されたindexからlength分の文字列を切り出す
 */
fun String.substring(from: Int, length: Int): String? {
    if (this.count < from || this.count < from + length || from < 0 || length < 0) {
        return null
    }
    return substring(startIndex = from, endIndex = from + length)
}

/**
 * 対象の文字列を削除する
 */
fun String.remove(of: String): String {
    return replacingOccurrences(of, "")
}

/**
 * 複数の対象の文字列を削除する
 */
fun String.removeAll(items: List<String>): String {
    var value = this
    items.forEach { value = value.remove(it) }
    return value
}

/**
 * 文字列をDoubleに変換する。数値でない場合は0になる
 */
val String.doubleValue: Double
    get() = toDoubleOrNull() ?: 0.0

/**
 * 文字列をCGFloatに変換する。数値でない場合は0になる
 */
val String.floatValue: CGFloat
    get() = toDoubleOrNull() ?: 0.0

/**
 * 文字列をIntに変換する。数値でない場合は0になる
 */
val String.integerValue: Int
    get() = toIntOrNull() ?: 0

/**
 * 文字列をLongに変換する。数値出ない場合は0になる
 */
val String.longValue: Long
    get() = toLongOrNull() ?: 0

/**
 * 指定した文字数ごとに区切った配列を返す
 */
fun String.split(length: Int): List<String> {
    val array = mutableListOf<String>()
    var i = 0
    while (i < this.length) {
        val str = substring(i, i + length)
        if (str != null) {
            array.append(str)
        }
        i += length
    }
    return array
}

/**
 * 引数のいずれかの文字列で始まる場合はtrue
 */
fun String.hasAnyPrefix(prefixes: List<String>): Boolean {
    return prefixes.contains { this.hasPrefix(it) }
}

/**
 * 引数のいずれかの文字列から始まる場合は、該当する文字列を返す
 */
fun String.anyPrefix(prefixes: List<String>): String? {
    return prefixes.firstOrNull { this.hasPrefix(it) }
}

/**
 * 数値を表す文字列か判定する
 */
val String?.isNumber: Boolean
    get() {
        if (this != null) {
            try {
                toDouble()
                return true
            } catch (e: Exception) {
            }
        }
        return false
    }

/**
 * 特定の文字に囲まれた文字列を取得する
 */
fun String.enclosed(left: String, right: String): String? {
    val startIndex = this.indexOf(left)
    if (startIndex != -1) {
        var endIndex = this.length
        val index = this.lastIndexOf(right)
        if (index != -1) {
            endIndex = index
        }
        return this.substring(startIndex + 1, endIndex)
    }
    return null
}

/**
 * 数字を3桁カンマ区切りにした文字列を返す
 * 連続した数字のみカンマ区切りにし、数字以外の文字は無視する
 * ex. "AAA(1234)BBB(5678)" → "AAA(1,234)BBB(5,678)"
 */
val String.numberFormat: String
    get() {
        var text = this

        // +-はいったん取り除いて後で復元
        var sign: String? = null
        if (text.hasPrefix("+")) {
            sign = "+"
            text = text.removePrefix("+")
        } else if (text.hasPrefix("-")) {
            sign = "-"
            text = text.removePrefix("-")
        }

        val numbers = text.split(".")
        val number = numbers[0]
        val decimal = if (1 < numbers.count) numbers[1] else null

        val builder = StringBuilder(20)
        var numberIndex = -1
        number.reversed().forEach { c ->
            if (c.isDigit()) {
                numberIndex++
            } else {
                numberIndex = -1
            }
            if (0 < numberIndex && numberIndex % 3 == 0) {
                builder.append(",")
            }
            builder.append(c)
        }
        builder.reverse()

        if (sign != null) {
            builder.insert(0, sign)
        }

        if (decimal != null) {
            builder.append(".")
            builder.append(decimal)
        }
        return builder.toString()
    }

/**
 * 数字を+-符号付き3桁カンマ区切りにした文字列を返す
 * ex. "+123456.789" -> "+123,456.789"
 * ex. "-123456.789" -> "-123,456.789"
 * ex. "-0.00" -> "0.00"
 */
val String.signedNumberFormat: String
    get() {
        val result = removePrefix("+").removePrefix("-")
        val doubleValue = doubleValue
        var sign = ""
        if (0 < doubleValue) {
            sign = "+"
        } else if (doubleValue < 0) {
            sign = "-"
        }
        return sign + result.numberFormat
    }

/**
 * 指定した桁まで文字列の右側を特定の文字で埋める
 * 指定桁を超えている場合は何もしない
 */
fun String.rightPadded(size: Int, spacer: String = " "): String {
    var result = this
    val add = size - count
    if (0 < add) {
        (0 until add).forEach { _ ->
            result += spacer
        }
    }
    return result
}


/**
 * 指定した桁まで文字列の左側を特定の文字で埋める
 * 指定桁を超えている場合は何もしない
 */
fun String.leftPadded(size: Int, spacer: String = " "): String {
    var result = this
    val add = size - count
    if (0 < add) {
        (0 until add).forEach { _ ->
            result = spacer + result
        }
    }
    return result
}

val String.literalEscaped: String
    get() {
        val conversion = mapOf("\r" to "", "\n" to "\\n", "\"" to "\\\"", "\'" to "\\'", "\t" to "\\t")

        var result = this
        conversion.forEach {
            result = result.replace(it.key, it.value)
        }
        return result
    }


/**
 * ローカライズされた文字列を取得する
 */
val String.localizedString: String
    get() {
        val context = SmartActivity.sharedOrNull ?: return "Context is null"
        val stringId = context.resources.getIdentifier(this, "string", context.packageName)
        return stringId.localizedString
    }

/**
 * nullか空の場合trueを返す
 */
val String?.isEmpty: Boolean
    get() {
        return this?.isEmpty() ?: true
    }

/**
 * nullか空でない場合trueを返す
 */
val String?.isNotEmpty: Boolean
    get() {
        return !isEmpty
    }

/**
 * 空を置き換えた値を返す
 */
fun String?.emptyConverted(emptyMark: String): String {
    val value = this
    if (value != null && value.isNotEmpty) {
        return value
    }
    return emptyMark
}

fun String.sha1(): String {
    return this.shaString("SHA-1")
}

fun String.sha256(): String {
    return this.shaString("SHA-256")
}

fun String.shaString(type: String): String {
    val hexChars = "0123456789abcdef"
    val bytes = MessageDigest.getInstance(type).digest(this.toByteArray())
    val result = StringBuffer(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(hexChars[i shr 4 and 0x0f])
        result.append(hexChars[i and 0x0f])
    }
    return result.toString()
}

val String.urlEncoded: String
    get() = URLEncoder.encode(this, "UTF-8")

val String.urlDecoded: String
    get() = URLDecoder.decode(this, "UTF-8")