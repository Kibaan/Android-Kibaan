package jp.co.altonotes.android.kibaan.ui

import android.widget.Button
import jp.co.altonotes.android.kibaan.ios.UIButton
import jp.co.altonotes.android.kibaan.ios.removeAll

/**
 * 一つだけ選択可能なボタンのグループ。ボタンと値のセットを複数登録して使用する。
 * 登録されたボタンの中の一つを選択状態にすることができ、選択されたボタンに紐づく値を取得できる。
 * また、値を設定するとそれに紐づくボタンが選択状態になる。
 */
class ButtonGroup<T> {

    /**
     * ボタンと値のマップ
     */
    private val buttonValueMap: MutableMap<Button, T> = mutableMapOf()

    /**
     * 選択されたボタンに紐づく値
     */
    var selectedValue: T? = null
        set(value) {
            field = value
            for ((button, target) in buttonValueMap) {
                button.isSelected = (target == value)
            }
        }

    /**
     * 値のリスト
     */
    val values: List<T>
        get() = buttonValueMap.map { it.value }

    /**
     * 登録されたボタンの情報をクリアする
     */
    fun clear() {
        buttonValueMap.removeAll()
    }

    /**
     * ボタンとそれに紐づく値を登録する
     */
    fun register(button: Button, value: T) {
        buttonValueMap[button] = value
    }

    fun select(button: Button?) {
        selectedValue = if (button != null) {
            buttonValueMap[button]
        } else {
            null
        }
    }

    /**
     * 指定したボタンを選択する
     */
    fun select(button: UIButton) {
        selectedValue = buttonValueMap[button]
    }

    /**
     * 指定した値に紐づくボタンを選択する
     */
    fun select(type: T) {
        selectedValue = type
    }

    /**
     * 指定した値に紐づくボタンを取得する
     */
    fun get(value: T): Button? {
        return buttonValueMap.filter { it.value == value }.map { it.key }.firstOrNull()
    }
}
