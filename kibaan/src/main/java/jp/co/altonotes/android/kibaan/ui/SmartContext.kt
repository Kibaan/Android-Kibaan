package jp.co.altonotes.android.kibaan.ui

import android.graphics.Typeface
import jp.co.altonotes.android.kibaan.controller.SmartActivity
import jp.co.altonotes.android.kibaan.ios.CGFloat
import jp.co.altonotes.android.kibaan.ios.UIFont
import jp.co.altonotes.android.kibaan.service.SingletonService
import jp.co.altonotes.android.kibaan.util.DeviceUtils


interface SmartFontProtocol {

    /** 端末のサイズに合わせてフォントサイズを自動で調整するか（4.7inch端末を基準とする） */
    var adjustsFontSizeForDevice: Boolean
    /** SmartContextに設定した共通フォントを使用するか */
    var useGlobalFont: Boolean

    /** 指定されたフォントの文字サイズとフォントを変換して返す */
    fun convertFont(font: UIFont?): UIFont? {
        val font = font ?: return null
        val fontManager = SmartContext.shared
        val size = font.pointSize * (if (adjustsFontSizeForDevice) fontManager.screenScale else 1.0)
        val convertedFont = fontManager.getFont(size = size, type = if (font.isBold) SmartContext.FontType.bold else SmartContext.FontType.regular)
        return if (useGlobalFont && convertedFont != null) {
            convertedFont
        } else {
            font.withSize(size)
        }
    }
}

class SmartContext {

    // region -> Constants

    enum class FontType {
        regular,
        bold
    }

    /** 基準となる横幅DP */
    private val baseWidth: CGFloat = 360.0

    // endregion


    companion object {
        val shared: SmartContext get() = SingletonService.get(SmartContext::class)
    }

    fun setActivity(activity: SmartActivity?) {
        shared.activity = activity
    }
    private var activity: SmartActivity? = null
    private var typeFaceMap: MutableMap<FontType, Typeface> = mutableMapOf()

    val screenScale: CGFloat
        get() {
            val context = activity
            if (privateScreenScale == null && context != null) {
                privateScreenScale = DeviceUtils.shortLengthDP(context) / baseWidth
            }
            return privateScreenScale ?: 1.0
        }
    private var privateScreenScale: CGFloat? = null

    // region ->Methods

    /**
     * FontTypeに対応するFontのフォントファイル名を設定する
     * ※main/assets配下に読み込む対象のファイルを配置すること
     */
    fun setFontFromAsset(path: String, type: FontType = FontType.regular) {
        val context = activity ?: return
        setFont(Typeface.createFromAsset(context.assets, path), type)
    }

    /**
     * FontTypeに対応するFontを設定する
     */
    fun setFont(typeFace: Typeface, type: FontType = FontType.regular) {
        typeFaceMap[type] = typeFace
    }

    /**
     * サイズとフォントタイプを指定して、フォントを取得する
     */
    fun getFont(size: CGFloat, type: FontType): UIFont? {
        val typeface = typeFaceMap[type]
        if (typeface != null) {
            return UIFont(typeface, size)
        }
        return defaultFont(size = size, type = type)
    }

    /**
     * デフォルトのフォントを取得する
     */
    private fun defaultFont(size: CGFloat, type: FontType): UIFont? {
        return when (type) {
            FontType.regular -> UIFont(Typeface.DEFAULT, pointSize = size)
            FontType.bold -> UIFont(Typeface.DEFAULT_BOLD, pointSize = size)
        }
    }

    // endregion

}