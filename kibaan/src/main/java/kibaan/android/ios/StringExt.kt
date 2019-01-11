package kibaan.android.ios

import android.graphics.Paint
import android.graphics.Rect
import kibaan.android.extension.substringTo
import java.net.URLDecoder
import java.nio.charset.Charset

/**
 * 指定されたCharsetでバイト配列に変換する
 */
fun String.data(using: Charset): ByteArray {
    return toByteArray(using)
}

/**
 * 指定された文字で分割された文字列からの部分文字列を含む配列を返す
 */
fun String.components(separatedBy: String): List<String> {
    return split(separatedBy)
}

/**
 * 指定された接頭辞と一致するかどうかを返す
 */
fun String.hasPrefix(prefix: String): Boolean {
    return startsWith(prefix)
}

/**
 * 指定された接尾辞と一致するかどうかを返す
 */
fun String.hasSuffix(suffix: String): Boolean {
    return endsWith(suffix)
}

/**
 * 末尾の文字列を削除する
 */
fun String.removeLast(): String {
    val last = lastOrNull() ?: return this
    return removeLast(last.toString())
}

/**
 * 指定された接尾辞を削除する
 */
fun String.removeLast(suffix: String): String {
    return if (endsWith(suffix)) substringTo(length - suffix.length) ?: this else this
}

/**
 * 対象の文字列を指定された文字列に置換する
 */
fun String.replacingOccurrences(of: String, with: String): String {
    return replace(of, with)
}

/**
 * 指定された長さ分の接頭辞を取得する
 */
fun String.prefix(length: Int): String {
    return substringTo(length) ?: ""
}

/**
 * 小文字に変換する
 */
fun String.lowercased(): String {
    return toLowerCase()
}

/**
 * 空文字、改行をトリムする
 */
fun String.trim(target: String): String {
    return replacingOccurrences(target, "")
}

/**
 * Adds an element to the end of the collection.
 */
fun String.append(value: String): String {
    return this + value
}

/**
 * Returns a new string made from the receiver by replacing all percent encoded sequences with the matching UTF-8 characters.
 */
val String.removingPercentEncoding: String
    get() = URLDecoder.decode(this, "UTF-8")

/**
 * Returns a string containing all but the last element of the sequence.
 */
fun String.dropLast(): String {
    return substring(0, length - 1)
}

/**
 * The number of elements in the collection.
 */
val String?.count: Int
    get() = this?.length ?: 0

/**
 * 指定された文字列を含む範囲を返す.
 * 見つからない場合は"null"が返される.
 */
fun String.range(text: String): IntRange? {
    val start = indexOf(text)
    if (start == -1) return null
    return IntRange(start, start + text.length)
}

/**
 * Returns a hash code value for the object.
 */
val String.hashValue: Int
    get() = hashCode()

/**
 * 文字列のサイズを返す.
 */
fun String.size(font: UIFont): CGSize {
    val paint = Paint()
    paint.textSize = font.pointSize.toFloat()
    paint.typeface = font.typeface
    paint.isAntiAlias = true

    val bounds = Rect()
    paint.getTextBounds(this, 0, this.length, bounds)
    val width = paint.measureText(this)

    return CGSize(width = width.toDouble(), height = bounds.height().toDouble())
}