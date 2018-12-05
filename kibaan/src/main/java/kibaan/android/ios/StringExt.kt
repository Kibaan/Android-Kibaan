package kibaan.android.ios

import android.graphics.Paint
import android.graphics.Rect
import kibaan.android.extension.substringTo
import java.net.URLDecoder
import java.nio.charset.Charset

fun String.data(using: Charset): ByteArray {
    return toByteArray(using)
}

fun String.components(separatedBy: String): List<String> {
    return split(separatedBy)
}

fun String.hasPrefix(prefix: String): Boolean {
    return startsWith(prefix)
}

fun String.hasSuffix(suffix: String): Boolean {
    return endsWith(suffix)
}

fun String.removeLast(): String {
    val last = lastOrNull() ?: return this
    return removeLast(last.toString())
}

fun String.removeLast(suffix: String): String {
    return if (endsWith(suffix)) substringTo(length - suffix.length) ?: this else this
}

fun String.replacingOccurrences(of: String, with: String): String {
    return replace(of, with)
}

fun String.prefix(length: Int): String {
    return substringTo(length) ?: ""
}

fun String.lowercased(): String {
    return toLowerCase()
}

fun String.trim(target: String): String {
    return replacingOccurrences(target, "")
}

fun String.append(value: String): String {
    return this + value
}

val String.removingPercentEncoding: String
    get() = URLDecoder.decode(this, "UTF-8")

fun String.dropLast(): String {
    return substring(0, length - 1)
}

val String?.count: Int
    get() = this?.length ?: 0

fun String.range(text: String): IntRange? {
    val start = indexOf(text)
    if (start == -1) return null
    return IntRange(start, start + text.length)
}

val String.hashValue: Int
    get() = hashCode()

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