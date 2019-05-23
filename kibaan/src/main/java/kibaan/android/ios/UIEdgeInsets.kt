package kibaan.android.ios

data class UIEdgeInsets(var top: Int, var left: Int, var bottom: Int, var right: Int) {
    companion object {
        val zero: UIEdgeInsets
            get() = UIEdgeInsets(0, 0, 0, 0)
    }
}