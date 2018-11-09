package jp.co.altonotes.android.kibaan.ios

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation

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
        visibility = if (value) View.INVISIBLE else View.VISIBLE
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

val View.frame: CGRect
    get() = CGRect(0.0, 0.0, width = width.toDouble(), height = height.toDouble())

fun View.isDescendant(of: View): Boolean {
    return this.id != of.id && of.findViewById<View>(id) != null
}

// endregion

// region -> ViewGroup

fun ViewGroup.addSubview(view: View) {
    view.removeFromSuperview()
    addView(view)
}

// endregion