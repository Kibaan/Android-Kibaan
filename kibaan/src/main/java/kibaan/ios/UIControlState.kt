package kibaan.ios

/**
 * UI状態
 * Created by yamamoto on 2018/05/16.
 */
enum class UIControlState(override val rawValue: Int) : IntEnum {
    normal(0),
    highLighted(1 shl 0),
    disabled(1 shl 1),
    selected(1 shl 2);

    val states: IntArray
        get() {
            return when {
                this == normal -> intArrayOf(-android.R.attr.state_selected, android.R.attr.state_enabled)
                this == highLighted -> intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
                this == disabled -> intArrayOf(-android.R.attr.state_enabled)
                this == selected -> intArrayOf(android.R.attr.state_selected, android.R.attr.state_enabled)
                else -> intArrayOf(android.R.attr.state_enabled)
            }
        }

    infix fun or(other: UIControlState): Int {
        return rawValue or other.rawValue
    }
}