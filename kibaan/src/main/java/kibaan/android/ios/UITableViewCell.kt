package kibaan.android.ios

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import butterknife.ButterKnife
import kibaan.android.R
import kibaan.android.extension.dpToPx
import kibaan.android.extension.numberOfLines
import kibaan.android.ui.SmartLabel


open class UITableViewCell : LinearLayout {

    // region -> Variables

    internal var tableView: UITableView? by didSet(null) {
        reflectTintColor()
    }

    @Suppress("unused")
    val textLabel: SmartLabel? by lazy {
        val label = SmartLabel(context)
        label.gravity = Gravity.CENTER_VERTICAL
        label.setPadding(context.dpToPx(10), 0, context.dpToPx(10), 0)
        label.minHeight = context.dpToPx(44.0)
        label.ellipsize = TextUtils.TruncateAt.END
        label.numberOfLines = 1
        contentView?.addView(label, LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT))
        return@lazy label
    }

    var contentView: ViewGroup? = null
        set(value) {
            field = value
            if (value != null) {
                addView(value, LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f))
                ButterKnife.bind(this, value)
                awakeFromNib()
            }
        }

    var accessoryView: View? = null
        set(value) {
            if (value != null) {
                if (field != value) {
                    value.removeFromSuperview()
                    addView(value, LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT))
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
        val padding = context.dpToPx(10)
        imageView.setImageResource(R.drawable.disclosure_indicator)
        imageView.setPadding(padding, 0, padding, 0)
        imageView.colorFilter = tintColorFilter
        return@lazy imageView
    }

    open val checkMarkView: View by lazy {
        val imageView = ImageView(context)
        val padding = context.dpToPx(10)
        imageView.setImageResource(R.drawable.checkmark)
        imageView.setPadding(padding, 0, padding, 0)
        imageView.colorFilter = tintColorFilter
        return@lazy imageView
    }

    open var tintColor: UIColor = UIColor.defaultTint
        set(value) {
            field = value
            reflectTintColor()
        }

    private val tintColorFilter: ColorFilter get() {
        var tintColor = tintColor
        if (tintColor == UIColor.defaultTint) {
            tintColor = tableView?.tintColor ?: UIColor.defaultTint
        }
        return PorterDuffColorFilter(tintColor.intValue, PorterDuff.Mode.SRC_ATOP)
    }

    // endregion

    // region -> Constructor

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, tableView: UITableView): super(context) {
        this.tableView = tableView
    }

    // endregion

    // region -> Initializer

    init {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    // endregion

    // region -> Life cycle

    open fun layoutSubViews() {}

    open fun awakeFromNib() {}

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

    // region -> Other

    private fun reflectTintColor() {
        (disclosureIndicatorView as? ImageView)?.colorFilter = tintColorFilter
        (checkMarkView as? ImageView)?.colorFilter = tintColorFilter
    }

    // endregion

    enum class AccessoryType : IntEnumDefault {
        none, disclosureIndicator, checkmark
    }
}
