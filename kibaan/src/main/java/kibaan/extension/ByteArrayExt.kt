package kibaan.extension

import android.content.Context
import android.content.Context.MODE_PRIVATE
import kibaan.ios.joined
import java.io.IOException

/**
 * 16進数の表示フォーマット。
 * 大文字(FFFF)または小文字(ffff)
 */
enum class HexCase {
    upper,
    lower
}

/**
 * /// 16進数文字列（大文字）
 */
val ByteArray.hexString: String
    get() = hexArray(charCase = HexCase.upper).joined(separator = " ")


/**
 * 1バイトずつ16進数文字列に変換した配列を返す
 */
fun ByteArray.hexArray(charCase: HexCase = HexCase.lower): List<String> {
    val format = if ((charCase == HexCase.lower)) "%02x" else "%02X"
    return map { String.format(format = format, args = *arrayOf(it)) }

}

/**
 * 指定したファイルからデータを読み込む
 */
@Suppress("FunctionName")
fun ByteArray(fileName: String, context: Context): ByteArray? {
    return try {
        context.openFileInput(fileName).readBytes()
    } catch (e: IOException) {
        null
    }
}

/**
 * 指定したファイルにデータを書き込む
 */
@Throws(IOException::class)
fun ByteArray.writeTo(fileName: String, context: Context, mode: Int = MODE_PRIVATE) {
    val fileOutputStream = context.openFileOutput(fileName, mode)
    fileOutputStream.write(this)
}
