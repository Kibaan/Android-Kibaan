package kibaan.android.framework

enum class TransitionType {
    normal, coverVertical, notAnimated;

    val animation: TransitionAnimation?
        get() {
            return when (this) {
                coverVertical -> TransitionAnimation.coverVertical
                else -> null
            }
        }
}
