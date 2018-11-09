package jp.co.altonotes.android.kibaan.ios

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import butterknife.ButterKnife
import jp.co.altonotes.android.kibaan.R
import jp.co.altonotes.android.kibaan.util.DeviceUtils


open class UITableViewCell : LinearLayout {

    // region -> Variables

    var contentView: View? = null
        set(value) {
            field = value
            if (value != null) {
                addView(value, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f))
                ButterKnife.bind(this, value)
                onViewCreated()
            }
        }

    var accessoryView: View? = null
        set(value) {
            if (value != null) {
                if (field != value) {
                    addView(value, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT))
                    value.backgroundColor = contentView?.backgroundColor
                }
            } else {
                accessoryView?.removeFromSuperview()
            }
            field = value
            value?.bringToFront()
        }

    open var accessoryType: AccessoryType = AccessoryType.none
        set(value) {
            field = value
            accessoryView = when (value) {
                AccessoryType.disclosureIndicator -> disclosureIndicatorView
                AccessoryType.checkmark -> checkMarkView
                else -> null
            }
        }

    open val disclosureIndicatorView: View by lazy {
        val imageView = ImageView(context)
        val padding = DeviceUtils.toPx(context, 10)
        imageView.setImageResource(R.drawable.disclosure_indicator)
        imageView.setPadding(padding, 0, padding, 0)
        imageView.colorFilter = tintColorFilter
        return@lazy imageView
    }

    open val checkMarkView: View by lazy {
        val imageView = ImageView(context)
        val padding = DeviceUtils.toPx(context, 10)
        imageView.setImageResource(R.drawable.checkmark)
        imageView.setPadding(padding, 0, padding, 0)
        imageView.colorFilter = tintColorFilter
        return@lazy imageView
    }

    open var tintColor: UIColor = UIColor.white
        set(value) {
            field = value
            (disclosureIndicatorView as? ImageView)?.colorFilter = tintColorFilter
            (checkMarkView as? ImageView)?.colorFilter = tintColorFilter
        }

    private val tintColorFilter: ColorFilter get() = PorterDuffColorFilter(tintColor.intValue, PorterDuff.Mode.SRC_ATOP)

    // endregion

    // region -> Constructor

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // endregion

    // region -> Initializer

    init {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    // endregion

    // region -> Life cycle

    open fun layoutSubViews() {}

    open fun onViewCreated() {}

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        layoutSubViews()
    }

    // endregion

    // region -> Setter

    open fun setHighlighted(highlighted: Boolean, animated: Boolean) {
        // do nothing.
    }

    open fun setSelected(selected: Boolean, animated: Boolean) {
        // do nothing.
    }

    // endregion

    // region -> Override

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        setSelected(selected, true)
    }

    // endregion

    enum class AccessoryType : IntEnumDefault {
        none, disclosureIndicator, checkmark
    }
}
