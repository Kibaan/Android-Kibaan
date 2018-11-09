package jp.co.altonotes.android.kibaan.ios

data class CGPoint(var x: CGFloat = 0.0, var y: CGFloat = 0.0) {
    companion object {
        val zero: CGPoint get() = CGPoint(0.0, 0.0)
    }
}