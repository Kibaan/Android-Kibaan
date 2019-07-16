package kibaan.android.ios

import android.graphics.Color
import kibaan.android.extension.substringFrom
import java.lang.Math.min

/**
 * iOSのUIColorクラスのシミュレート。色を表す
 * Created by Yamamoto Keita on 2018/01/18.
 */
class UIColor {

    companion object {
        val white = UIColor(rgbValue = 0xFFFFFF)
        val black = UIColor(rgbValue = 0x000000)
        val red = UIColor(rgbValue = 0xFF0000)
        val green = UIColor(rgbValue = 0x00FF00)
        val blue = UIColor(rgbValue = 0x0000FF)
        val clear = UIColor(rgbValue = 0xFFFFFF, alpha = 0.0)
        val orange = UIColor(rgbValue = 0xFFA500)
        val gray = UIColor(rgbValue = 0x808080)
        val yellow = UIColor(rgbValue = 0xFFFF00)
        val cyan = UIColor(rgbValue = 0x00FFFF)
        val lightGray = UIColor(rgbValue = 0xFFFFFF, alpha = 0.667)
        val defaultTint = UIColor(rgbValue = 0x007AF)
    }

    /**
     * ARGBのInt値
     */
    private var argbInt = 0xFFFFFFFF.toInt()

    val intValue: Int get() = argbInt

    /**
     * 文字列のカラーコード（アルファは含まない）
     */
    val colorCode: String? get() = String.format("%06X", 0xFFFFFF and intValue)

    /**
     * ARGBのInt値を指定して色を作成する
     */
    constructor(argbInt: Int) {
        this.argbInt = argbInt
    }

    /**
     * 16進数のカラーコードとアルファ（不透明度）を指定して色を作成する
     * ex.UIColor(rgbValue = 0x171b35)
     */
    constructor(rgbValue: Int, alpha: Double = 1.0) {
        setColorInt(rgbValue, alpha)
    }

    constructor(rgbValue: Long, alpha: Double = 1.0) : this(rgbValue.toInt(), alpha)

    constructor(rgbHex: String, alpha: Double = 1.0) {
        val code = rgbHex.removePrefix("#")
        if (code.count != 6) {
            return
        }
        val rgbValue = code.toIntOrNull(16) ?: 0xFFFFFF
        setColorInt(rgbValue, alpha)
    }

    constructor(argbHex: String) {
        var code = argbHex.removePrefix("#")
        var alpha: CGFloat = 1.0
        if (code.count == 8) {
            val aStr = code.substring(0 .. 1)
            code = code.substringFrom(2) ?: ""
            alpha = (aStr.toIntOrNull(16) ?: 0) / 255.0
        }
        val rgbValue = code.toIntOrNull(16) ?: 0xFFFFFF
        setColorInt(rgbValue, alpha)
    }

    constructor(white: Double = 1.0, alpha: Double = 1.0) {
        val iWhite = to255(white)
        argbInt = Color.argb(to255(alpha), iWhite, iWhite, iWhite)
    }

    constructor(red: Double, green: Double, blue: Double, alpha: Double) {
        argbInt = Color.argb(to255(alpha), to255(red), to255(green), to255(blue))
    }

    private fun setColorInt(rgbValue: Int, alpha: Double) {
        var iAlpha = to255(alpha)   // 0x 00 00 00 FF
        iAlpha = iAlpha shl (8 * 3) // 0x FF 00 00 00
        argbInt = iAlpha or rgbValue
    }

    private fun to255(d: Double): Int {
        return (d * 255.0).toInt()
    }

    override fun equals(other: Any?): Boolean {
        val rhs = (other as? UIColor) ?: return false
        return this.intValue == rhs.intValue
    }

    override fun hashCode(): Int {
        return argbInt
    }

    /**
     * 明度を上げた色を作成する
     */
    fun whiteAdded(value: CGFloat): UIColor {
        val red = min(Color.red(intValue) / 255.0 + value, 1.0)
        val green = min(Color.green(intValue) / 255.0 + value, 1.0)
        val blue = min(Color.blue(intValue) / 255.0 + value, 1.0)
        return UIColor(red = red, green = green, blue = blue, alpha = Color.alpha(intValue) / 255.0)
    }

    fun withAlphaComponent(alpha: Double): UIColor {
        return UIColor(argbInt and 0x00FFFFFF, alpha)

    }
}