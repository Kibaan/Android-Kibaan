package kibaan.android.sample.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import kibaan.android.ios.*

class CGContextTextView: View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val context = UIGraphicsGetCurrentContext(context, canvas) ?: return

        val rect = CGRect(x = 0, y= 0, width = width, height = height)

        context.setFillColor(UIColor.lightGray)
        context.fill(rect)

        val paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.alignment = NSTextAlignment.right

        val attr = mapOf<NSAttributedString.Key, Any>(
            NSAttributedString.Key.paragraphStyle to paragraphStyle,
            NSAttributedString.Key.foregroundColor to UIColor.green,
            NSAttributedString.Key.font to UIFont.systemFont(17.0)
        )

        "あいうえお".draw(inRect = rect, withAttributes = attr)
    }
}