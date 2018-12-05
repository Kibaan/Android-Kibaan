package kibaan.ui

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import kibaan.AndroidUnique
import kibaan.ios.UIColor
import java.util.regex.Pattern

class LinkLabel : SmartLabel {

    // region -> Inner Class

    class LinkInfo(var text: String, var onTap: ((LinkInfo) -> Unit)? = null)

    // endregion

    // region -> Constructor

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // endregion

    // region -> Functions

    /**
     * テキストの任意位置にリンクを設定する。複数設定可能
     */
    fun setLinks(linkList: List<LinkInfo>, color: UIColor) {
        val text = text ?: return
        setText(createSpannableString(text.toString(), linkList))
        setLinkTextColor(color.intValue)
        movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * テキストの任意位置にリンクを設定する。複数設定可能
     * ※末尾省略を許容する
     */
    @AndroidUnique
    fun setLinksWithEllipsizeEnd(linkList: List<LinkInfo>, color: UIColor) {
        val text = text ?: return
        setLinkTextColor(color.intValue)
        movementMethod = LinkMovementMethod.getInstance()
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (lineCount > maxLines) {
                    val endOfLastLine = layout.getLineEnd(maxLines - 1)
                    val newVal = text.subSequence(0, endOfLastLine - 1).toString() + "..."
                    setText(createSpannableString(newVal, linkList))
                } else {
                    setText(createSpannableString(text.toString(), linkList))
                }
            }
        })
    }

    /**
     * 指定した文字列一箇所にリンクを設定する
     */
    fun setLinkText(linkText: String, color: UIColor, onTap: (() -> Unit)? = null) {
        val linkInfo = LinkInfo(linkText) { onTap?.invoke() }
        setLinks(listOf(linkInfo), color = color)
    }

    // endregion

    // region -> Private

    private fun createSpannableString(message: String, linkTexts: List<LinkInfo>): SpannableString {
        val spannableString = SpannableString(message)
        linkTexts.forEach { linkInfo ->
            findTextAndSetSpanRecursive(spannableString, message = message, linkInfo = linkInfo)
        }
        return spannableString
    }

    private fun findTextAndSetSpanRecursive(spannableString: SpannableString, message: String, linkInfo: LinkInfo) {
        val pattern = Pattern.compile(linkInfo.text)
        var prevFindEnd = 0
        val matcher = pattern.matcher(message)
        while (matcher.find(prevFindEnd)) {
            val start = matcher.start()
            val end = matcher.end()
            prevFindEnd = end
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(textView: View) {
                    linkInfo.onTap?.invoke(linkInfo)
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    // endregion

    // region -> Other

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val sText = Spannable.Factory.getInstance().newSpannable(text)
        val widget = this
        val action = event?.action
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            val x = event.x + widget.scrollX - widget.totalPaddingLeft
            val y = event.y + widget.scrollY - widget.totalPaddingTop
            val layout = widget.layout
            val line = layout.getLineForVertical(y.toInt())
            val off = layout.getOffsetForHorizontal(line, x)
            if (x < layout.getLineMax(line)) {
                val link = sText.getSpans(off, off, ClickableSpan::class.java)
                if (link.isNotEmpty()) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget)
                    }
                    return true
                }
            }
        }
        return false
    }

    // endregion
}