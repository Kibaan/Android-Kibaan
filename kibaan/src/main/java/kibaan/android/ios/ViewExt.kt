package kibaan.android.ios

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import kibaan.android.extension.pxToDp

/**
 * ViewにiOSのUIViewと似た挙動をさせるためのエクステンション
 *
 * Created by yamamoto on 2018/02/05.
 */

// region -> View

var View.intTag: Int
    get() = tag as? Int ?: 0
    set(value) {
        tag = value
    }

var View.isHidden: Boolean
    get() = visibility == View.INVISIBLE || visibility == View.GONE
    set(value) {
        visibility = if (value) {
            if (parent is UIStackView) View.GONE else View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

var View.isGone: Boolean
    get() = visibility == View.GONE
    set(value) {
        visibility = if (value) View.GONE else View.VISIBLE
    }

var View.backgroundColor: UIColor?
    get() {
        val drawable = background as? ColorDrawable
        if (drawable != null) {
            return UIColor(argbInt = drawable.color)
        }
        return UIColor.clear
    }
    set(value) {
        setBackgroundColor(value?.intValue ?: android.R.color.transparent)
    }

fun View.animateAlpha(withDuration: Long, toAlpha: Float) {
    val alphaAnimate = AlphaAnimation(this.alpha, toAlpha)
    alphaAnimate.duration = withDuration
    alphaAnimate.fillAfter = true
    startAnimation(alphaAnimate)
}

val View.superview: ViewGroup? get() = parent as? ViewGroup

fun View.removeFromSuperview() {
    (parent as? ViewGroup)?.removeView(this)
}

fun View.setNeedsDisplay() {
    invalidate()
}

val View.frame: CGRect
    get() {
        val width = context.pxToDp(width).toDouble()
        val height = context.pxToDp(height).toDouble()
        val x = context.pxToDp(left).toDouble()
        val y = context.pxToDp(top).toDouble()
        return CGRect(x, y, width = width, height = height)
    }

val View.pxFrame: CGRect
    get() {
        return CGRect(left, top, width, height)
    }

val View.bounds: CGRect
    get() {
        val width = context.pxToDp(width).toDouble()
        val height = context.pxToDp(height).toDouble()
        return CGRect(0.0, 0.0, width = width, height = height)
    }

val View.pxBounds: CGRect
    get() {
        return CGRect(0, 0, width, height)
    }

fun View.isDescendant(of: View): Boolean {
    return this.id != of.id && of.findViewById<View>(id) != null
}

// endregion

// region -> ViewGroup

fun ViewGroup.addSubview(view: View, params: ViewGroup.LayoutParams? = null) {
    view.removeFromSuperview()
    if (params != null) {
        addView(view, params)
    } else {
        addView(view)
    }
}

val ViewGroup.subviews: List<View> get() = (0 until childCount).map { getChildAt(it) }

// endregion