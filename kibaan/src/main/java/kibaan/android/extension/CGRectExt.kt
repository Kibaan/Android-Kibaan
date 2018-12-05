package kibaan.android.extension

import kibaan.android.ios.CGFloat
import kibaan.android.ios.CGRect
import kotlin.math.max
import kotlin.math.min

/**
 * 短辺の長さ
 */
val CGRect.shortLength: CGFloat
    get() = min(width, height)

/**
 * 長辺の長さ
 */
val CGRect.longLength: CGFloat
    get() = max(width, height)

/**
 * 幅
 */
var CGRect.width: CGFloat
    get() {
        return size.width
    }
    set(value) {
        size.width = value
    }
/**
 * 高さ
 */
var CGRect.height: CGFloat
    get() {
        return size.height
    }
    set(value) {
        size.height = value
    }

/**
 * X座標
 */
var CGRect.x: CGFloat
    get() {
        return origin.x
    }
    set(value) {
        origin.x = value
    }

/**
 * Y座標
 */
var CGRect.y: CGFloat
    get() {
        return origin.y
    }
    set(value) {
        origin.y = value
    }
