package kibaan.android.ios

data class UIEdgeInsets(var top: CGFloat, var left: CGFloat, var bottom: CGFloat, var right: CGFloat) {
    companion object {
        val zero: UIEdgeInsets
            get() = UIEdgeInsets(0.0, 0.0, 0.0, 0.0)
    }
}