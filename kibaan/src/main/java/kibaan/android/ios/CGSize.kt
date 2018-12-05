package kibaan.android.ios

data class CGSize(var width: CGFloat, var height: CGFloat) {
    companion object {
        val zero: CGSize get() = CGSize(0.0, 0.0)
    }
}