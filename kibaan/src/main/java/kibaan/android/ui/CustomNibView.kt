package kibaan.android.ui

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import butterknife.ButterKnife
import kibaan.android.extension.toSnakeCase
import kibaan.android.ios.UIColor
import kibaan.android.ios.backgroundColor
import kibaan.android.util.Log

/**
 * XMLを自動でロードするView
 * 基本はクラスメイト同名のXMLを読み込むが、xibNameをoverrideして任意のXMLファイルを読み込ませることが出来る
 */
@Suppress("LeakingThis")
abstract class CustomNibView : FrameLayout {

    // region -> Constructor

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // endregion

    // region -> Variables

    open fun xibName(): String? {
        return null
    }

    // endregion

    init {
        val resourceName = xibName() ?: javaClass.simpleName.toSnakeCase()
        val layoutId = context.resources.getIdentifier(resourceName, "layout", context.packageName)
        try {
            ButterKnife.bind(LayoutInflater.from(context).inflate(layoutId, this, true))
        } catch (e: Resources.NotFoundException) {
            Log.e(javaClass.simpleName, "Layout file not found!!! file name is [$resourceName]")
            throw e
        }
        backgroundColor = UIColor.clear
    }
}
