package jp.co.altonotes.android.kibaan.ios

/**
 * UI状態
 * Created by yamamoto on 2018/05/16.
 */
enum class UIControlState {
    normal, highLighted, disabled, selected;

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
}