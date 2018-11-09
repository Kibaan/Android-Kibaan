package jp.co.altonotes.android.kibaan.ios

class UITouch(var x: CGFloat, var y: CGFloat, val tapCount: Int = 1) {

    fun location(): CGPoint {
        return CGPoint(x, y)
    }
}